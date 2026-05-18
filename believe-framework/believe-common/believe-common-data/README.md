# believe-common-data

数据持久化增强模块，提供四种数据源模式（单库/读写分离/多数据源/分库分表）、声明式数据源切换、MyBatis-Plus 自动填充和分页等能力。

## 适用场景

- 单数据库应用（默认模式）
- 主从架构读写分离
- 多业务库独立管理
- 大数据量分库分表

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-data</artifactId>
</dependency>
```

### 使用示例

**1. 单数据源（默认，零配置）**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/believe
    username: root
    password: root
```

无需额外配置，直接使用 MyBatis-Plus。

**2. 读写分离**

```yaml
believe:
  data:
    read-write-split:
      enabled: true

spring:
  datasource:
    master:
      url: jdbc:mysql://master:3306/believe
      username: root
      password: root
    slave:
      url: jdbc:mysql://slave:3306/believe
      username: root
      password: root
```

读操作自动走从库：

```java
@Service
public class UserService {

    // 自动走从库
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    // @DataSource 注解强制走主库（避免主从延迟）
    @DataSource("master")
    public User getByIdWithMaster(Long id) {
        return userMapper.selectById(id);
    }
}
```

**3. 多数据源**

```yaml
believe:
  data:
    multi-datasource:
      enabled: true

spring:
  datasource:
    master:
      url: jdbc:mysql://db1:3306/believe_user
    slave:
      url: jdbc:mysql://db2:3306/believe_order
    report:
      url: jdbc:mysql://db3:3306/believe_report
```

声明式切换：

```java
@Service
public class ReportService {

    @DataSource("report")  // 类级别：所有方法走 report 库
    public List<Report> listAll() {
        return reportMapper.selectList(null);
    }
}

@Service
public class OrderService {

    @DataSource("slave")  // 方法级别：该方法走 slave 库
    public Order getOrder(Long id) {
        return orderMapper.selectById(id);
    }

    // 未标注的方法走默认 master 库
    public void createOrder(Order order) {
        orderMapper.insert(order);
    }
}
```

**4. 分库分表**

```yaml
believe:
  data:
    sharding:
      enabled: true

# ShardingSphere 规则通过 YAML 文件配置
# classpath:sharding-config.yml
```

**5. 实体继承 BaseEntity**

```java
@Data
@TableName("t_user")
public class User extends BaseEntity {
    private String username;
    private String email;
    // id, createTime, updateTime, createBy, updateBy, deleted 自动继承
}
```

继承后自动获得的能力：

| 字段 | 自动行为 |
|------|---------|
| `id` | 自增主键 |
| `createTime` | 插入时自动填充当前时间 |
| `updateTime` | 插入和更新时自动填充当前时间 |
| `createBy` | 从 `AuthContext.get().getUserId()` 获取，未登录默认 `system` |
| `updateBy` | 同上 |
| `deleted` | 逻辑删除标记（MyBatis-Plus `@TableLogic`） |

**6. Mapper 扫描**

Mapper 接口放在 `com.believe.**.mapper` 路径下自动扫描，无需手动配置 `@MapperScan`。

## 数据源模式切换

| 模式 | 激活条件 | 说明 |
|------|---------|------|
| 单数据源 | 默认（其他模式均未开启） | 开发/简单场景 |
| 读写分离 | `believe.data.read-write-split.enabled=true` | 一主一从 |
| 多数据源 | `believe.data.multi-datasource.enabled=true` | master/slave/report |
| 分库分表 | `believe.data.sharding.enabled=true` | ShardingSphere 接管 |

四种模式互斥，通过 `@ConditionalOnProperty` 控制激活。

## 配置项

`believe.data.*`：

| 配置 | 默认值 | 说明 |
|------|--------|------|
| `mybatis-plus.auto-fill` | `true` | 是否启用自动填充 |
| `read-write-split.enabled` | `false` | 启用读写分离 |
| `multi-datasource.enabled` | `false` | 启用多数据源 |
| `sharding.enabled` | `false` | 启用分库分表 |
