# believe-common-log

日志链路追踪模块，提供全链路 TraceId 自动传递和 `@Log` 注解方法级日志记录。

## 适用场景

- 微服务间调用链路追踪
- 关键方法执行耗时监控
- 请求参数与返回值审计

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-log</artifactId>
</dependency>
```

引入后无需任何配置即可启用。模块通过 `@AutoConfiguration` 自动注册 `TraceIdFilter` 和 `LogAspect`。

### 使用示例

**1. 链路追踪（自动生效）**

无需编码，`TraceIdFilter` 在请求到达时自动工作：
- 从请求头 `X-Trace-Id` 提取 TraceId，若不存在则自动生成 UUID
- 自动写入 SLF4J MDC（日志中通过 `%X{traceId}` 输出）
- 自动写入 `RequestContext`
- 请求结束时自动清理

```log
# 日志输出示例（配置 logback pattern 包含 %X{traceId}）
2026-05-18 21:00:00.123 [traceId:a1b2c3d4e5f6] INFO  - 请求处理开始
```

> 跨服务调用时，下游的 `TraceIdFilter` 会复用上游传递的 `X-Trace-Id` 请求头，实现全链路打通。

**2. @Log 方法注解**

```java
@RestController
public class OrderController {

    // 基础用法：记录方法名 + 参数 + 耗时
    @Log
    @PostMapping("/orders")
    public Result<Order> create(@RequestBody OrderDTO dto) {
        return Result.ok(orderService.create(dto));
    }

    // 自定义描述
    @Log("用户下单")
    @PostMapping("/orders/place")
    public Result<Order> place(@RequestBody PlaceOrderRequest req) {
        return Result.ok(orderService.place(req));
    }

    // 打印返回值（默认关闭，避免大对象）
    @Log(value = "查询订单", printResult = true)
    @GetMapping("/orders/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        return Result.ok(orderService.getById(id));
    }

    // 不打印参数（涉密接口）
    @Log(value = "密码校验", printArgs = false)
    @PostMapping("/verify-password")
    public Result<Boolean> verify(@RequestBody PasswordRequest req) {
        return Result.ok(authService.verify(req));
    }
}

@Service
public class OrderService {

    // Service 层同样生效
    @Log("订单创建")
    public Order create(OrderDTO dto) {
        // ...
    }
}
```

日志输出效果：

```log
INFO  >> 用户下单 args: [PlaceOrderRequest(productId=1001, quantity=2)]
INFO  << 用户下单 (156ms)

# 异常时
ERROR << 用户下单 failed after 89ms: 库存不足
```

## 注解属性

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `value()` | String | `""`（使用方法签名） | 操作描述 |
| `printArgs()` | boolean | `true` | 是否打印方法参数 |
| `printResult()` | boolean | `false` | 是否打印返回值 |
