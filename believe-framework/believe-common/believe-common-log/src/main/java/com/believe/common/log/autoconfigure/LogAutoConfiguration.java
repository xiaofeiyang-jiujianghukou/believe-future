package com.believe.common.log.autoconfigure;

import com.believe.common.log.aspect.LogAspect;
import com.believe.common.log.filter.TraceIdFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({TraceIdFilter.class, LogAspect.class})
public class LogAutoConfiguration {
}
