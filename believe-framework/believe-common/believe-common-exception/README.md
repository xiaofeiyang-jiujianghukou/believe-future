# believe-common-exception

全局异常处理模块，自动拦截各类异常并返回标准化的 `Result` 响应。

## 适用场景

所有 Web 服务必须引入，提供统一的异常响应格式，避免异常信息直接暴露给客户端。

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-common-exception</artifactId>
</dependency>
```

引入即生效，`GlobalExceptionHandler` 通过 `@RestControllerAdvice` 自动拦截所有 Controller 异常。

### 使用示例

**1. 抛出业务异常（自动转换为标准响应）**

```java
@RestController
public class OrderController {

    @GetMapping("/orders/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            throw new BelieveException(ErrorCode.BIZ_DATA_NOT_FOUND, "订单不存在");
        }
        return Result.ok(order);
    }
}
```

客户端收到的响应：

```json
{
    "code": 2001,
    "message": "订单不存在",
    "data": null,
    "timestamp": 1716038400000
}
```

**2. 自动拦截的异常类型**

无需手动 try-catch，以下异常自动拦截：

| 场景 | 触发的异常 | HTTP 状态码 | 错误码 |
|------|-----------|------------|--------|
| 业务异常 | `BelieveException` | 按错误码映射 | 业务方指定 |
| 参数校验失败 | `MethodArgumentNotValidException` | 400 | 1002 |
| 缺少请求参数 | `MissingServletRequestParameterException` | 400 | 1001 |
| 请求体格式错误 | `HttpMessageNotReadableException` | 400 | 1002 |
| 资源不存在 | `NoResourceFoundException` | 404 | 2001 |
| 请求方法错误 | `HttpRequestMethodNotSupportedException` | 405 | 1002 |
| 未捕获异常（兜底） | `Exception` | 500 | 3000 |

**3. 参数校验示例**

```java
@PostMapping("/orders")
public Result<Order> create(@Valid @RequestBody OrderDTO dto) {
    return Result.ok(orderService.create(dto));
}

@Data
public class OrderDTO {
    @NotBlank(message = "商品名称不能为空")
    private String productName;

    @Min(value = 1, message = "数量至少为1")
    private Integer quantity;
}
```

校验失败时自动返回：

```json
{
    "code": 1002,
    "message": "productName: 商品名称不能为空; quantity: 数量至少为1",
    "data": null,
    "timestamp": 1716038400000
}
```

## 错误码 → HTTP 状态码映射

| 错误码分类 | HTTP 状态码 |
|-----------|------------|
| 1xxx（参数） | 400 |
| 2xxx（业务） | 400 |
| 3xxx（系统） | 500 |
| 4001（未认证） | 401 |
| 4xxx（其他权限） | 403 |
| 5xxx（RPC） | 502 |
