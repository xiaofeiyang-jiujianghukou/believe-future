package com.believe.gateway.filter.ratelimit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfiguration {

    @Bean
    @ConditionalOnProperty(name = "believe.gateway.rate-limit.type", havingValue = "sentinel")
    public RateLimiter sentinelRateLimiter(RateLimitProperties properties) {
        return new SentinelRateLimiter(properties);
    }

    @Bean
    @ConditionalOnProperty(name = "believe.gateway.rate-limit.type", havingValue = "sliding-window", matchIfMissing = true)
    public RateLimiter slidingWindowRateLimiter(RateLimitProperties properties) {
        return new SlidingWindowRateLimiter(properties);
    }
}
