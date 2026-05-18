# believe-common-mq

消息队列统一封装模块，支持 RocketMQ / RabbitMQ / Kafka 三种中间件，通过一个配置属性自由切换。

## 适用场景

- 异步解耦（下单后发短信、生成报表等）
- 削峰填谷（秒杀请求排队）
- 服务间事件驱动通信

## 快速开始

### Maven

```xml
<!-- 公共封装（必选） -->
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-mq</artifactId>
</dependency>

<!-- 按需选择 MQ 实现（至少选一个） -->
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
</dependency>
<!-- 或 RabbitMQ -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
<!-- 或 Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

> 三种 MQ 依赖均为 optional，只引入你实际使用的即可。

### 使用示例

**1. 选择 MQ 类型**

```yaml
believe:
  mq:
    type: ROCKETMQ   # 可选值: ROCKETMQ（默认）/ RABBITMQ / KAFKA
```

**2. RocketMQ 示例**

引入 `rocketmq-spring-boot-starter`，配置 RocketMQ 连接：

```yaml
rocketmq:
  name-server: 127.0.0.1:9876
  producer:
    group: order-producer-group
```

发送消息：

```java
@Component
public class OrderMessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void sendOrderCreated(Order order) {
        rocketMQTemplate.convertAndSend("order-topic", order);
    }
}
```

消费消息：

```java
@Component
@RocketMQMessageListener(topic = "order-topic", consumerGroup = "order-consumer-group")
public class OrderMessageConsumer implements RocketMQListener<Order> {

    @Override
    public void onMessage(Order order) {
        log.info("收到订单消息: {}", order.getId());
    }
}
```

**3. RabbitMQ 示例**

引入 `spring-boot-starter-amqp`，配置：

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

**4. Kafka 示例**

引入 `spring-kafka`，配置：

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

## 切换机制

| 配置值 | 条件类 | 说明 |
|--------|--------|------|
| `ROCKETMQ`（默认） | `RocketMQTemplate` 存在 | 事务消息、顺序消息 |
| `RABBITMQ` | `RabbitTemplate` 存在 | 灵活路由、确认机制成熟 |
| `KAFKA` | `KafkaTemplate` 存在 | 高吞吐、流处理 |

三种模式通过 `@ConditionalOnClass` + `@ConditionalOnProperty` 双重条件安全激活，互斥生效。启动日志会打印当前激活的 MQ 模式：

```log
INFO  MQ 模式: RocketMQ
```
