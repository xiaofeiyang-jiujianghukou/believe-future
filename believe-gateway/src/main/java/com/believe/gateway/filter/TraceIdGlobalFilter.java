package com.believe.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class TraceIdGlobalFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String headerId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        final String traceId = (headerId != null && !headerId.isBlank())
                ? headerId
                : UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
        exchange = exchange.mutate()
                .request(r -> r.header("X-Trace-Id", traceId))
                .build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
