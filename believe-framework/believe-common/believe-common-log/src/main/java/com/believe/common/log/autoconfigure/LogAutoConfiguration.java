package com.believe.common.log.autoconfigure;

import com.believe.common.log.aspect.LogAspect;
import com.believe.common.log.filter.TraceIdFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.OncePerRequestFilter;

@AutoConfiguration
@Import(LogAspect.class)
public class LogAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(OncePerRequestFilter.class)
    @Import(TraceIdFilter.class)
    static class TraceIdFilterConfiguration {
    }
}
