# believe-common-feign

Feign 远程调用增强模块，自动在服务间调用时传递认证信息和链路追踪 ID。

## 适用场景

微服务间通过 Feign 调用时，需要自动透传：
- 链路追踪 ID（TraceId）
- 当前用户信息（UserId、Username）
- 认证 Token

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-feign</artifactId>
</dependency>
```

引入后，`FeignRequestInterceptor` 自动注册到所有 `@FeignClient`，无需额外配置。

### 使用示例

**1. 定义 Feign 客户端**

```java
@FeignClient(name = "believe-user")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    Result<User> getUser(@PathVariable Long id);
}
```

```java
@FeignClient(name = "believe-order")
public interface OrderClient {

    @PostMapping("/api/orders")
    Result<Order> create(@RequestBody OrderDTO dto);
}
```

**2. 调用方设置上下文（通常在 Filter/Interceptor 中）**

```java
// 登录拦截器中设置
AuthContext.get()
    .setUserId(currentUser.getId())
    .setUsername(currentUser.getUsername())
    .setToken(currentUser.getToken());

// TraceIdFilter 中设置
RequestContext.get().setTraceId(request.getHeader("X-Trace-Id"));
```

**3. 调用时自动透传**

```java
@Service
public class OrderService {

    @Autowired
    private UserClient userClient;

    public OrderWithUser getOrderWithUser(Long orderId) {
        // Feign 调用时自动携带以下请求头：
        // X-Trace-Id: abc123...
        // X-User-Id: 10086
        // X-Username: zhangsan
        // Authorization: Bearer eyJhbGci...
        
        Order order = orderMapper.selectById(orderId);
        Result<User> result = userClient.getUser(order.getUserId());
        return new OrderWithUser(order, result.getData());
    }
}
```

## 自动注入的请求头

| Header | 来源 | 条件 |
|--------|------|------|
| `X-Trace-Id` | `RequestContext.get().getTraceId()` | traceId 存在时 |
| `X-User-Id` | `AuthContext.get().getUserId()` | 已登录时 |
| `X-Username` | `AuthContext.get().getUsername()` | 已登录时 |
| `Authorization` | `AuthContext.get().getToken()` | Token 存在时 |

> 拦截器通过 `@ConditionalOnClass(RequestInterceptor.class)` 条件激活，仅在使用 Feign 时生效。
