# believe-common-redis

Redis 操作封装模块，提供 Jackson 3 序列化的 RedisTemplate、分布式锁和 Spring Cache 集成。

## 适用场景

- 分布式缓存（配合 `@Cacheable` / `@CacheEvict`）
- 分布式锁（防重复提交、资源争抢）
- Redis 数据读写

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-redis</artifactId>
</dependency>
```

### 前置条件

配置 Redis 连接信息（Spring Boot 标准配置）：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

模块通过 `@AutoConfiguration` + `@ConditionalOnClass(RedisTemplate.class)` 自动激活。

### 使用示例

**1. RedisTemplate 读写**

```java
@Component
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setUser(Long id, User user) {
        redisTemplate.opsForValue().set("user:" + id, user, Duration.ofHours(1));
    }

    public User getUser(Long id) {
        return (User) redisTemplate.opsForValue().get("user:" + id);
    }
}
```

> RedisTemplate 已配置 `JacksonJsonRedisSerializer`（Jackson 3），对象自动序列化为 JSON。

**2. Spring Cache 注解**

```java
@Service
public class UserService {

    @Cacheable(value = "user", key = "#id")
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @CacheEvict(value = "user", key = "#user.id")
    public User update(User user) {
        userMapper.updateById(user);
        return user;
    }
}
```

> CacheManager 默认 TTL 为 1 小时，禁止缓存 null 值。

**3. 分布式锁**

```java
@Component
public class OrderService {

    @Autowired
    private RedisDistributedLock lock;

    // 方式一：手动控制
    public void createOrder(Long userId) {
        String lockKey = "order:lock:" + userId;
        if (!lock.tryLock(lockKey, Duration.ofSeconds(5), Duration.ofSeconds(30))) {
            throw new BizException("操作太频繁，请稍后再试");
        }
        try {
            // 业务逻辑
        } finally {
            lock.unlock(lockKey);
        }
    }

    // 方式二：模板方法（推荐）
    public void createOrderV2(Long userId) {
        String lockKey = "order:lock:" + userId;
        lock.execute(lockKey,
                Duration.ofSeconds(5),   // 等待超时
                Duration.ofSeconds(30),  // 锁持有时间
                () -> {
                    // 业务逻辑
                    return null;
                });
    }
}
```

**分布式锁特性**：
- 基于 `SET key value NX EX ttl` 原子操作
- 解锁使用 Lua 脚本校验持有者，避免误删他人锁
- 锁令牌通过 ThreadLocal 管理，同一线程内 tryLock/unlock 自动匹配

| 方法 | 说明 |
|------|------|
| `tryLock(key, timeout, ttl)` | 尝试获取锁，返回 boolean |
| `unlock(key)` | 释放锁（Lua 脚本校验持有者） |
| `execute(key, timeout, ttl, action)` | 模板方法，自动获取→执行→释放 |
