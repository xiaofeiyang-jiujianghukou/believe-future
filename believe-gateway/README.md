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
    rate-limit:
      enabled: true
      qps: 100
```

## 过滤器链

5 层 WebFilter，按 Order 顺序执行：

| Order | 过滤器 | 职责 |
|-------|--------|------|
| -2 | `AuthGlobalFilter` | JWT 解析，白名单 `/auth/**`、`/actuator/**` 放行，写入 `X-User-Id` |
| -1 | `TraceIdGlobalFilter` | 提取/生成 `X-Trace-Id`，传播至下游 |
| 0 | `RateLimitGlobalFilter` | 滑动窗口限流，QPS 超限返回 429 |
| 1 | `GrayReleaseGlobalFilter` | 检测 `X-Gray-Release` 头，标记灰度版本 |
| 5 | `RequestLogGlobalFilter` | 记录请求日志：`[traceId] METHOD path status duration` |

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

Sentinel 5.0 移除了 `com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager`，限流降级响应改用自定义 `WebFilter` 实现。该模块使用滑动窗口计数器替代，通过 `believe.gateway.rate-limit` 配置控制。
