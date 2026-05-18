# believe-gateway

API 网关服务，统一流量入口。基于 Spring Cloud Gateway 5.0 WebFlux 响应式架构，集成 Sa-Token 认证、Sentinel 限流、Nacos 注册发现。

## 适用场景

- 微服务统一入口，路由转发
- JWT 认证鉴权，白名单放行
- 全链路 TraceId 追踪
- 灰度发布流量路由

## 快速开始

### 启动

```bash
mvn spring-boot:run
```

服务启动在 8080 端口，管理端点 8081。路由通过 Nacos 自动发现下游服务。

### 配置

```yaml
server:
  port: 8080

management:
  server:
    port: 8081
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus

spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        - id: believe-auth
          uri: lb://believe-auth
          predicates:
            - Path=/api/auth/**
          filters:
            - StripPrefix=1
        - id: believe-user
          uri: lb://believe-user
          predicates:
            - Path=/api/user/**
          filters:
            - StripPrefix=1

believe:
  gateway:
    auth:
      # 白名单追加（默认项无需配置：/auth/**, /actuator, /favicon.ico）
      whitelist: /open/api/**,/public/**
    rate-limit:
      enabled: true
      qps: 100
      # type: sliding-window   # 默认值，内置滑动窗口
      # type: sentinel          # 切换为 Sentinel 限流
```

## 过滤器链

5 层 WebFilter，按 Order 顺序执行：

| Order | 过滤器 | 职责 |
|-------|--------|------|
| -2 | `AuthGlobalFilter` | JWT 解析，白名单 `/auth/**`、`/actuator/**` 放行，写入 `X-User-Id` |
| -1 | `TraceIdGlobalFilter` | 提取/生成 `X-Trace-Id`，传播至下游 |
| 0 | `RateLimitGlobalFilter` | 限流（策略模式，默认滑动窗口，可选 Sentinel） |
| 1 | `GrayReleaseGlobalFilter` | 检测 `X-Gray-Release` 头，标记灰度版本 |
| 5 | `RequestLogGlobalFilter` | 记录请求日志：`[traceId] METHOD path status duration` |

## 限流策略

`RateLimitGlobalFilter` 采用策略模式，通过 `believe.gateway.rate-limit.type` 切换实现：

### 内置滑动窗口（默认）

```yaml
believe:
  gateway:
    rate-limit:
      enabled: true
      qps: 100
      # type 不设置或设为 sliding-window
```

基于 `ConcurrentHashMap` 按路径分桶计数，每秒重置窗口，超过 QPS 返回 429。

### Sentinel 限流（可选）

```yaml
believe:
  gateway:
    rate-limit:
      enabled: true
      type: sentinel
      qps: 100
```

使用 `SphU.entry()` 接入 Sentinel 流控体系，支持 Sentinel Dashboard 动态规则管理。资源名为 `gateway-rate-limit`，可按需在 Dashboard 中自定义规则。

## 路由规范

- `POST /api/auth/login` → `lb://believe-auth`（白名单，跳过认证）
- `GET /api/user/info` → `lb://believe-user`（需携带有效 JWT）

## 认证失败响应

```json
{
    "code": 4001,
    "message": "未登录或 Token 已过期",
    "data": null,
    "timestamp": 1716038400000
}
```

## 依赖

| 组件 | 用途 |
|------|------|
| Spring Cloud Gateway 5.0 | 响应式网关 |
| Nacos | 服务发现 + 配置管理 |
| Sa-Token Reactor | 网关层 JWT 认证 |
| Sentinel | 限流熔断 |

## 踩坑记录

**1. Spring Cloud Gateway 5.0 artifact 改名**

```xml
<!-- 4.x（已废弃） -->
<artifactId>spring-cloud-starter-gateway</artifactId>

<!-- 5.0（Spring Boot 4.0 / Spring Cloud 2025.1.0） -->
<artifactId>spring-cloud-gateway-server-webflux</artifactId>
```

版本对照：

| Spring Boot | Spring Cloud | Gateway artifact |
|-------------|--------------|------------------|
| 3.x | 2024.0.x | `spring-cloud-starter-gateway` (4.2.x) |
| 4.0.x | 2025.1.0 | `spring-cloud-gateway-server-webflux` (5.0.x) |

**2. Sentinel `GatewayCallbackManager` 移除**

Sentinel 5.0 移除了 `com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager`，限流降级响应改用自定义 `WebFilter` 实现。本模块使用策略模式，默认内置滑动窗口，也可配置 `believe.gateway.rate-limit.type=sentinel` 启用 Sentinel（基于 `SphU.entry()` + reactor `doFinally` 退出 Entry）。
