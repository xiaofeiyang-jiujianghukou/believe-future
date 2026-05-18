package com.believe.common.core.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    public static void set(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    public static Object get(String key) {
        return CONTEXT.get().get(key);
    }

    public static Long getUserId() {
        return Optional.ofNullable(CONTEXT.get().get("userId"))
                .map(v -> Long.parseLong(v.toString()))
                .orElse(null);
    }

    public static String getUsername() {
        return (String) CONTEXT.get().get("username");
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
