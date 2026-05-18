# believe-common-spi

SPI（Service Provider Interface）扩展机制模块，定义框架的可扩展接口。业务模块只需实现接口并通过 `ServiceLoader` 注册，即可替换默认行为。

## 适用场景

- 替换默认的序列化实现
- 接入自定义通知渠道（短信/邮件/企业微信）
- 替换分布式 ID 生成策略

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-spi</artifactId>
</dependency>
```

### 扩展点接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `DataSerializer` | `serialize(Object)` / `deserialize(byte[], Class)` | 数据序列化/反序列化 |
| `NotifyChannel` | `send(String recipient, String title, String content)` | 通知发送 |
| `IdGenerator` | `nextId()` / `nextIdStr()` | 分布式 ID 生成 |

### 内置扩展实现（believe-common-spi-extension）

引入 `believe-common-spi-extension` 模块即可获得以下开箱即用的实现：

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-spi-extension</artifactId>
</dependency>
```

| SPI 接口 | 实现类 | 标识 | 激活条件 |
|----------|--------|------|---------|
| `NotifyChannel` | `SmsNotifyChannel` | sms | `believe.spi.notify.sms.api-url` |
| `NotifyChannel` | `EmailNotifyChannel` | email | `JavaMailSender` 可用 + `believe.spi.notify.email.from` |
| `NotifyChannel` | `DingTalkNotifyChannel` | dingtalk | `believe.spi.notify.dingtalk.webhook-url` |
| `IdGenerator` | `SnowflakeIdGenerator`（雪花算法） | — | 默认启用（可通过自定义 `IdGenerator` Bean 覆盖） |
| `IdGenerator` | `LeafSegmentIdGenerator`（美团号段） | — | `DataSource` 可用 + `believe.spi.id.leaf.biz-tag` |
| `DataSerializer` | `JsonDataSerializer` | json | 默认启用 |
| `DataSerializer` | `ProtobufDataSerializer` | protobuf | `MessageLite` 在 classpath 上 |

### 使用示例

**1. 实现自定义通知渠道**

```java
// 实现 SPI 接口
package com.believe.user.spi;

public class SmsNotifyChannel implements NotifyChannel {

    @Override
    public void send(String title, String content, Set<String> receivers) {
        // 接入阿里云短信服务
        for (String phone : receivers) {
            smsClient.send(phone, title, content);
        }
    }
}
```

**2. 注册 SPI 实现**

在 `META-INF/services/` 下创建文件：

```
# src/main/resources/META-INF/services/com.believe.common.spi.NotifyChannel
com.believe.user.spi.SmsNotifyChannel
```

**3. 使用 SPI 扩展**

```java
@Service
public class NotifyService {

    private final NotifyChannel channel;

    public NotifyService() {
        // ServiceLoader 加载 SPI 实现
        this.channel = ServiceLoader.load(NotifyChannel.class)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到 NotifyChannel 实现"));
    }

    public void notifyUser(Long userId, String message) {
        User user = userService.getById(userId);
        channel.send("系统通知", message, Set.of(user.getPhone()));
    }
}
```

**4. 配合 Spring Boot 条件装配（推荐）**

```java
@Configuration
public class NotifyConfiguration {

    @Bean
    @ConditionalOnMissingBean(NotifyChannel.class)
    public NotifyChannel defaultNotifyChannel() {
        // 默认日志通知（兜底）
        return (title, content, receivers) ->
                log.info("默认通知: title={}, receivers={}", title, receivers);
    }
}
```

> `@ConditionalOnMissingBean` 配合 `ServiceLoader`：若有 SPI 实现则覆盖默认，无则兜底。

**5. 综合案例：自定义 ID 生成器**

```java
// SPI 实现：雪花算法
package com.believe.user.spi;

public class SnowflakeIdGenerator implements IdGenerator {

    private final Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    @Override
    public long nextId() {
        return snowflake.nextId();
    }

    @Override
    public String nextIdStr() {
        return String.valueOf(snowflake.nextId());
    }
}
```

注册后，框架中所有 `IdUtil` 调用都会使用你的雪花算法实现，无需修改现有代码。

**6. 配置内置扩展**

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
        datacenter-id: 2
      leaf:
        biz-tag: order_id
        step: 2000
```

无需编写任何 Java 代码，引入依赖并配置 YAML 即可激活对应实现。所有 Bean 均通过 `@ConditionalOnMissingBean` 注册，自定义实现会自动覆盖默认扩展。
