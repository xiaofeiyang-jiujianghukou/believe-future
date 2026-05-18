package com.believe.common.core.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 认证上下文（线程隔离，实例方式）
 *
 * <p>使用示例：
 * <pre>
 * // 设置
 * AuthContext.get()
 *     .setUserId(10086L)
 *     .setUsername("zhangsan")
 *     .setAuthorities(Set.of("user:read"));
 *
 * // 获取
 * Long userId = AuthContext.get().getUserId();
 *
 * // 清理
 * AuthContext.clear();
 * </pre>
 */
public class AuthContext {

    private static final ThreadLocal<AuthContext> CONTEXT = ThreadLocal.withInitial(AuthContext::new);

    private final Map<String, Object> store = new HashMap<>();

    private AuthContext() {}

    public static AuthContext get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    // ========== 链式设置 ==========

    public AuthContext setUserId(Long userId) {
        store.put("userId", userId);
        return this;
    }

    public AuthContext setUsername(String username) {
        store.put("username", username);
        return this;
    }

    public AuthContext setToken(String token) {
        store.put("token", token);
        return this;
    }

    public AuthContext setAuthorities(Set<String> authorities) {
        store.put("authorities", authorities);
        return this;
    }

    // ========== 获取值 ==========

    public Long getUserId() {
        return Optional.ofNullable(store.get("userId"))
                .map(v -> v instanceof Long ? (Long) v : Long.parseLong(v.toString()))
                .orElse(null);
    }

    public String getUsername() {
        return (String) store.get("username");
    }

    public String getToken() {
        return (String) store.get("token");
    }

    @SuppressWarnings("unchecked")
    public Set<String> getAuthorities() {
        return (Set<String>) store.get("authorities");
    }

    // ========== 便捷判断 ==========

    public boolean isLogin() {
        return getUserId() != null;
    }

    // ========== 调试 ==========

    public Map<String, Object> getAll() {
        return new HashMap<>(store);
    }
}