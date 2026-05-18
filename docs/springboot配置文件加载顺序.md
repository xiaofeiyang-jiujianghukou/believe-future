# Spring Boot 配置文件加载顺序

## 优先级（高 → 低，数字越小优先级越高）

| 优先级 | 来源 | 说明 |
|--------|------|------|
| 1 | 命令行参数 | `--server.port=9090` |
| 2 | 环境变量 | `SPRING_APPLICATION_NAME=xxx` |
| 3 | `application-{profile}.yml` | 激活的 profile 文件 |
| 4 | `application.yml` | 主配置，**永远最先被读** |
| 5 | `bootstrap-{profile}.yml` | bootstrap profile，需手动启用 |
| 6 | `bootstrap.yml` | bootstrap 主文件，需手动启用 |
| 7 | Nacos 远程配置 | shared-configs 合并到 Environment |
| 8 | 代码默认值 | Java 类中的字段初始化 |

前面的覆盖后面的同名属性。

## 加载时序

```
① application.yml          → spring.cloud.bootstrap.enabled=true
② bootstrap.yml            → spring.config.import, spring.cloud.nacos.*
③ application-{profile}.yml
④ bootstrap-{profile}.yml
⑤ Nacos 远程配置（shared-configs）
⑥ Bean 初始化
```

## 关键规则

**application.yml vs bootstrap.yml**

- `application.yml` —— 永远加载，无需任何开关。放 `bootstrap.enabled=true`
- `bootstrap.yml` —— 默认 **不加载**（Spring Cloud 2025.x 起）。放 Nacos 地址、spring.config.import

**bootstrap.enabled 不能写在 bootstrap.yml**

`spring.cloud.bootstrap.enabled` 控制是否加载 bootstrap.yml。如果写进 bootstrap.yml，它永远不会被读到——因为默认值是 `false`，Spring 直接跳过 bootstrap 阶段，压根不打开这个文件。必须放在 `application.yml` 或环境变量中。

**Profile 文件优先级**

`application-{profile}.yml` > `application.yml`。相同属性会被覆盖，不相同的合并。

## 本项目实践

```yaml
# application.yml —— 永远加载
spring:
  cloud:
    bootstrap:
      enabled: true          # 开启后 bootstrap.yml 才会加载

# bootstrap.yml —— application.yml 处理后才加载
spring:
  config:
    import: optional:nacos:${spring.application.name}.yaml?group=DEFAULT_GROUP
  application:
    name: believe-gateway
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_HOST:localhost}:8848
        namespace: ${NACOS_NAMESPACE:dev}
      config:
        server-addr: ${NACOS_HOST:localhost}:8848
        namespace: ${NACOS_NAMESPACE:dev}
        shared-configs:
          - data-id: common-redis.yaml
            group: DEFAULT_GROUP
```
