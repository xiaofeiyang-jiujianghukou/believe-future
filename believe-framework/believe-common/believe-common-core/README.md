# believe-common-core

基础核心模块，提供统一返回体、异常体系、上下文持有者和通用工具，是整个框架的**唯一底层依赖**。

## 适用场景

所有 believe 微服务必须引入，提供以下基础能力：
- API 统一响应格式
- 线程级认证/请求上下文传递
- 错误码标准化
- JSON 序列化（Jackson 3）、日期、ID 生成等工具

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-core</artifactId>
</dependency>
```

> 版本由 `believe-framework` 父 POM 统一管理，子模块无需指定 version。

### 使用示例

**1. 统一返回体**

```java
// 成功返回
@GetMapping("/user/{id}")
public Result<User> getUser(@PathVariable Long id) {
    User user = userService.getById(id);
    return Result.ok(user);
}

// 分页返回
@GetMapping("/users")
public Result<PageResult<User>> listUsers(int page, int size) {
    Page<User> page = userService.page(new Page<>(page, size));
    return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
}

// 失败返回
if (user == null) {
    return Result.error(ErrorCode.BIZ_DATA_NOT_FOUND, "用户不存在");
}
```

**2. 上下文设置**

```java
// 认证上下文（登录拦截器中设置）
AuthContext.get()
    .setUserId(10086L)
    .setUsername("zhangsan")
    .setToken("eyJhbGci...")
    .setAuthorities(Set.of("user:read", "user:write"));

// 请求上下文（TraceIdFilter 中设置）
RequestContext.get()
    .setTraceId("abc123def456")
    .setClientIp("192.168.1.100");

// 业务代码中获取
Long userId = AuthContext.get().getUserId();
String traceId = RequestContext.get().getTraceId();

// 请求结束后清理
AuthContext.clear();
RequestContext.clear();
```

**3. 抛出业务异常**

```java
if (order == null) {
    throw new BelieveException(ErrorCode.BIZ_DATA_NOT_FOUND, "订单不存在");
}

if (stock < quantity) {
    throw new BizException("库存不足，剩余: " + stock);
}
```

**4. JSON 序列化**

```java
String json = JsonUtil.toJson(user);
User user = JsonUtil.fromJson(json, User.class);
```

**5. 工具方法**

```java
String id = IdUtil.uuid();           // a1b2c3d4...（无横线）
String now = DateUtil.format(LocalDateTime.now());  // 2026-05-18 21:00:00
LocalDateTime start = DateUtil.startOfDay(LocalDate.now());
```

## 核心 API

| 类 | 关键方法 |
|----|---------|
| `Result<T>` | `ok()`, `ok(data)`, `error(code, msg)`, `isSuccess()`, `isError()` |
| `PageResult<T>` | `of(total, records)`, `empty()`, `hasData()`, `getTotalPages()` |
| `AuthContext` | `get().setUserId().setUsername().setToken()` — 实例式链式 ThreadLocal |
| `RequestContext` | `get().setTraceId().setClientIp()` — 实例式链式 ThreadLocal |
| `BelieveException` | `new BelieveException(code, msg)`, `getCode()` |
| `JsonUtil` | `toJson(obj)`, `fromJson(json, class)`, `mapper()` |
| `DateUtil` | `format()`, `parse()`, `startOfDay()`, `endOfDay()` |
| `IdUtil` | `uuid()`, `uuidWithDash()`, `simpleId()` |

## 错误码规范

| 分类 | 范围 | 说明 |
|------|------|------|
| 1xxx | 参数错误 | PARAM_MISSING(1001), PARAM_INVALID(1002) |
| 2xxx | 业务错误 | BIZ_GENERAL(2000), BIZ_DATA_NOT_FOUND(2001) |
| 3xxx | 系统错误 | SYS_GENERAL(3000), SYS_TIMEOUT(3001) |
| 4xxx | 权限错误 | AUTH_UNAUTHORIZED(4001), AUTH_FORBIDDEN(4003) |
| 5xxx | RPC错误 | RPC_GENERAL(5000), RPC_TIMEOUT(5001) |
