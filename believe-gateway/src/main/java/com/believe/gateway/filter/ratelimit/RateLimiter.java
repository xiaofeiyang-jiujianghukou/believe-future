package com.believe.gateway.filter.ratelimit;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public interface RateLimiter {

    Mono<Void> rateLimit(ServerWebExchange exchange, WebFilterChain chain);
}
