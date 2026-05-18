# believe-job

xxl-job 执行器自动配置模块，引入即自动注册执行器，配合 `JobTraceHelper` 实现任务级链路追踪。

## 适用场景

- 分布式定时任务调度
- 需要链路追踪的任务执行

## 快速开始

### Maven

```xml
<dependency>
    <groupId>com.believe</groupId>
    <artifactId>believe-job</artifactId>
</dependency>
```

> `xxl-job-core 2.5.0` 已作为传递依赖引入，无需额外声明。

### 使用示例

**1. 配置调度中心**

```yaml
believe:
  job:
    enabled: true
    admin-addresses: http://xxl-job-admin:8080/xxl-job-admin
    app-name: believe-user-executor
    port: 9998
    access-token: your_token
    log-path: /data/applogs/xxl-job/jobhandler
    log-retention-days: 30
```

无需任何 Java 配置，执行器自动注册到调度中心。

**2. 编写任务处理器**

```java
@Component
public class OrderJob {

    @XxlJob("closeTimeoutOrders")
    public void closeTimeoutOrders() {
        JobTraceHelper.initTrace("closeTimeoutOrders");
        try {
            // 扫描超时未支付订单并关闭
            orderService.closeTimeoutOrders();
        } finally {
            JobTraceHelper.clearTrace();
        }
    }
}
```

**3. 使用 JobTraceHelper 串联链路**

```java
@XxlJob("syncUserData")
public void syncUserData() {
    JobTraceHelper.initTrace("syncUserData");
    try {
        String traceId = RequestContext.get().getTraceId();
        // 此 traceId 会通过 Feign RequestInterceptor 自动传递到下游服务
        userService.syncToES();
    } finally {
        JobTraceHelper.clearTrace();
    }
}
```

## 配置项

`believe.job.*`：

| 配置 | 默认值 | 说明 |
|------|--------|------|
| `enabled` | `true` | 是否启用执行器 |
| `admin-addresses` | `http://127.0.0.1:8080/xxl-job-admin` | 调度中心地址 |
| `app-name` | `believe-job-executor` | 执行器名称 |
| `access-token` | `default_token` | 通信令牌 |
| `ip` | — | 执行器 IP（留空自动获取） |
| `port` | `9999` | 执行器端口 |
| `log-path` | `/data/applogs/xxl-job/jobhandler` | 日志路径 |
| `log-retention-days` | `30` | 日志保留天数 |

## 关闭执行器

```yaml
believe:
  job:
    enabled: false
```

执行器不会注册，用于本地开发或不需要任务调度的场景。
