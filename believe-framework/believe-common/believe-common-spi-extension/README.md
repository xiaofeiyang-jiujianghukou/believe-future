# believe-common-spi-extension

SPI 扩展实现模块，为 `believe-common-spi` 定义的三个扩展点提供开箱即用的默认实现，涵盖通知渠道、分布式 ID 生成和数据序列化三大领域。

## 适用场景

- 需要快速接入短信/邮件/钉钉通知，不想从零对接第三方 SDK
- 需要雪花算法或美团 Leaf 号段算法的分布式 ID 生成器
- 需要 JSON 或 Protobuf 序列化的统一实现

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-spi-extension</artifactId>
</dependency>
```

> `spring-boot-starter-mail` 和 `com.google.protobuf:protobuf-java` 为 optional 依赖，按需引入。

### 使用示例

**1. 短信通知**

```yaml
believe:
  spi:
    notify:
      sms:
        api-url: https://sms-api.example.com/send
```

```java
@RestController
public class NotifyController {

    @Autowired
    private SmsNotifyChannel smsNotifyChannel;

    @PostMapping("/notify/sms")
    public Result<Void> sendSms(@RequestParam String phone, @RequestParam String msg) {
        smsNotifyChannel.send(phone, "验证码", msg);
        return Result.ok();
    }
}
```

**2. 邮件通知**

引入 `spring-boot-starter-mail` 依赖：

```yaml
spring:
  mail:
    host: smtp.example.com
    username: noreply@example.com
    password: xxx

believe:
  spi:
    notify:
      email:
        from: noreply@believe.com
```

```java
@Autowired
private EmailNotifyChannel emailNotifyChannel;

public void sendWelcomeEmail(String to) {
    emailNotifyChannel.send(to, "欢迎注册", "感谢您注册 Believe 平台！");
}
```

**3. 钉钉通知**

```yaml
believe:
  spi:
    notify:
      dingtalk:
        webhook-url: https://oapi.dingtalk.com/robot/send?access_token=xxx
```

```java
@Autowired
private DingTalkNotifyChannel dingTalkNotifyChannel;

public void alertOnCall(String phone, String error) {
    dingTalkNotifyChannel.send(phone, "系统告警", error);
}
```

**4. 雪花算法 ID 生成（默认启用）**

```yaml
believe:
  spi:
    id:
      snowflake:
        worker-id: 1
        datacenter-id: 2
```

```java
@Autowired
private IdGenerator idGenerator;  // 自动注入 SnowflakeIdGenerator

public String createOrderId() {
    return idGenerator.nextIdStr();  // 输出: 1382746492012396544
}
```

> `SnowflakeIdGenerator` 通过 `@ConditionalOnMissingBean(IdGenerator.class)` 注册，自定义 `IdGenerator` Bean 会自动覆盖。

**5. 美团 Leaf 号段 ID 生成**

需要数据库已配置 DataSource：

```yaml
believe:
  spi:
    id:
      leaf:
        biz-tag: order_id
        step: 2000
```

首次启动自动创建 `leaf_alloc` 表并初始化号段，之后每次从数据库批量取号，本地缓冲消费，大幅减少 DB 访问。

**6. JSON 序列化（默认启用）**

```java
@Autowired
private JsonDataSerializer jsonDataSerializer;

public byte[] serializeToBytes(User user) {
    return jsonDataSerializer.serialize(user);
}

public User deserializeFromBytes(byte[] data) {
    return jsonDataSerializer.deserialize(data, User.class);
}
```

**7. Protobuf 序列化**

引入 `com.google.protobuf:protobuf-java` 依赖，对象需为 protoc 生成的 `MessageLite` 实现类：

```java
@Autowired
private ProtobufDataSerializer protobufDataSerializer;

// UserProto.User 为 protoc 生成的类，实现了 MessageLite
UserProto.User user = UserProto.User.newBuilder()
        .setId(1L)
        .setName("张三")
        .build();

byte[] data = protobufDataSerializer.serialize(user);
UserProto.User parsed = protobufDataSerializer.deserialize(data, UserProto.User.class);
```

### 全部配置项

```yaml
believe:
  spi:
    notify:
      sms:
        api-url: https://sms-api.example.com/send
      email:
        from: noreply@believe.com
      dingtalk:
        webhook-url: https://oapi.dingtalk.com/robot/send?access_token=xxx
    id:
      snowflake:
        worker-id: 1
        datacenter-id: 1
      leaf:
        biz-tag: order_id
        step: 1000
```

## 实现一览

| SPI 接口 | 实现类 | 标识 | 激活条件 |
|----------|--------|------|---------|
| `NotifyChannel` | `SmsNotifyChannel` | sms | `believe.spi.notify.sms.api-url` |
| `NotifyChannel` | `EmailNotifyChannel` | email | `JavaMailSender` + `believe.spi.notify.email.from` |
| `NotifyChannel` | `DingTalkNotifyChannel` | dingtalk | `believe.spi.notify.dingtalk.webhook-url` |
| `IdGenerator` | `SnowflakeIdGenerator` | — | 默认启用（`@ConditionalOnMissingBean`） |
| `IdGenerator` | `LeafSegmentIdGenerator` | — | `DataSource` + `believe.spi.id.leaf.biz-tag` |
| `DataSerializer` | `JsonDataSerializer` | json | 默认启用 |
| `DataSerializer` | `ProtobufDataSerializer` | protobuf | `MessageLite` 在 classpath |

## 覆盖机制

所有 Bean 均通过 `@ConditionalOnMissingBean` 注册。只需在项目中定义自己的实现：

```java
@Component
public class WechatNotifyChannel implements NotifyChannel {
    // 自定义微信通知，自动替代内置的 SmsNotifyChannel
}
```

即可完全替代内置实现，框架零侵入。
