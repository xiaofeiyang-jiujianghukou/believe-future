package com.believe.gateway.filter.ratelimit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "believe.gateway.rate-limit")
public class RateLimitProperties {

    private boolean enabled = false;
    private int qps = 100;
    private String type = "sliding-window";
}
