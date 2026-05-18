package com.believe.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GrayReleaseGlobalFilter implements WebFilter, Ordered {

    private static final String GRAY_HEADER = "X-Gray-Release";
    private static final String GRAY_VERSION = "gray";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String grayHeader = exchange.getRequest().getHeaders().getFirst(GRAY_HEADER);
        if (GRAY_VERSION.equals(grayHeader)) {
            exchange = exchange.mutate()
                    .request(r -> r.header("X-Service-Version", GRAY_VERSION))
                    .build();
            log.debug("Gray release routing enabled for: {}", exchange.getRequest().getURI().getPath());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
