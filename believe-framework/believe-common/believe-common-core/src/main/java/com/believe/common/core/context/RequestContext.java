package com.believe.common.core.context;

import java.util.HashMap;
import java.util.Map;

public class RequestContext {

    private static final ThreadLocal<Map<String, String>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    public static void setTraceId(String traceId) {
        CONTEXT.get().put("traceId", traceId);
    }

    public static String getTraceId() {
        return CONTEXT.get().get("traceId");
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
