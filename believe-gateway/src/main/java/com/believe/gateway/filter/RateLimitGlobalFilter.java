package com.believe.gateway.filter;

import com.believe.gateway.filter.ratelimit.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitGlobalFilter implements WebFilter, Ordered {

    private final RateLimiter rateLimiter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return rateLimiter.rateLimit(exchange, chain);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
