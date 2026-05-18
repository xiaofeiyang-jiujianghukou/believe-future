package com.believe.common.core.context;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 请求上下文（线程隔离，实例方式）
 *
 * <p>使用示例：
 * <pre>
 * // 设置
 * RequestContext.get()
 *     .setTraceId("abc123")
 *     .setClientIp("192.168.1.100")
 *     .setUserAgent("Mozilla/5.0...");
 *
 * // 获取
 * String traceId = RequestContext.get().getTraceId();
 * String clientIp = RequestContext.get().getClientIp();
 *
 * // 清理
 * RequestContext.clear();
 * </pre>
 */
public class RequestContext {

    private static final ThreadLocal<RequestContext> CONTEXT = ThreadLocal.withInitial(RequestContext::new);

    private final Map<String, String> store = new HashMap<>();

    private RequestContext() {}

    public static RequestContext get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    // ========== 链式设置 ==========

    public RequestContext setTraceId(String traceId) {
        store.put("traceId", traceId);
        return this;
    }

    public RequestContext setClientIp(String clientIp) {
        store.put("clientIp", clientIp);
        return this;
    }

    public RequestContext setUserAgent(String userAgent) {
        store.put("userAgent", userAgent);
        return this;
    }

    // ========== 获取值 ==========

    public String getTraceId() {
        return store.get("traceId");
    }

    public String getClientIp() {
        return store.get("clientIp");
    }

    public String getUserAgent() {
        return store.get("userAgent");
    }

    // ========== 便捷判断 ==========

    public boolean hasTraceId() {
        return store.containsKey("traceId") && store.get("traceId") != null;
    }

    // ========== 调试 ==========

    public Map<String, String> getAll() {
        return new HashMap<>(store);
    }
}