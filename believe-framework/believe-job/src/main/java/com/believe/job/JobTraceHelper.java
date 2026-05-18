package com.believe.job;

import com.believe.common.core.context.RequestContext;
import com.believe.common.core.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public final class JobTraceHelper {

    private JobTraceHelper() {
    }

    public static void initTrace(String jobName) {
        String traceId = IdUtil.uuid();
        RequestContext.get().setTraceId(traceId);
        MDC.put("traceId", traceId);
        log.debug("Job [{}] started with traceId: {}", jobName, traceId);
    }

    public static void clearTrace() {
        RequestContext.clear();
        MDC.clear();
    }
}
