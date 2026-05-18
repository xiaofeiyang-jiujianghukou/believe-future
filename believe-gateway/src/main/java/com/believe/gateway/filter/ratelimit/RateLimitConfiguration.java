package com.believe.gateway.filter.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfiguration {

    @Bean
    @ConditionalOnProperty(name = "believe.gateway.rate-limit.type", havingValue = "sentinel")
    public RateLimiter sentinelRateLimiter(
            @Value("${believe.gateway.rate-limit.enabled:false}") boolean enabled,
            @Value("${believe.gateway.rate-limit.qps:100}") int qps) {
        return new SentinelRateLimiter(enabled, qps);
    }

    @Bean
    @ConditionalOnProperty(name = "believe.gateway.rate-limit.type", havingValue = "sliding-window", matchIfMissing = true)
    public RateLimiter slidingWindowRateLimiter(
            @Value("${believe.gateway.rate-limit.enabled:false}") boolean enabled,
            @Value("${believe.gateway.rate-limit.qps:100}") int qps) {
        return new SlidingWindowRateLimiter(enabled, qps);
    }
}
