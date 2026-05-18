package com.believe.gateway.filter;

import com.believe.common.core.result.Result;
import com.believe.common.core.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimitGlobalFilter implements WebFilter, Ordered {

    @Value("${believe.gateway.rate-limit.enabled:false}")
    private boolean enabled;

    @Value("${believe.gateway.rate-limit.qps:100}")
    private int qps;

    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    private volatile long windowStart = System.currentTimeMillis();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!enabled) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();
        long now = System.currentTimeMillis();

        if (now - windowStart > 1000) {
            synchronized (this) {
                if (now - windowStart > 1000) {
                    counters.clear();
                    windowStart = now;
                }
            }
        }

        int count = counters.computeIfAbsent(path, k -> new AtomicInteger(0)).incrementAndGet();
        if (count > qps) {
            log.warn("Rate limited: path={}, count={}", path, count);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String body = JsonUtil.toJson(Result.error(3001, "请求过于频繁，请稍后再试"));
            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(body.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
