package com.believe.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.believe.common.core.result.Result;
import com.believe.common.core.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Set;

@Slf4j
@Component
public class AuthGlobalFilter implements WebFilter, Ordered {

    private static final Set<String> WHITELIST = Set.of(
            "/auth/login", "/auth/register", "/auth/captcha", "/auth/logout",
            "/actuator", "/favicon.ico"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isWhitelist(path)) {
            return chain.filter(exchange);
        }

        try {
            StpUtil.checkLogin();
            String userId = String.valueOf(StpUtil.getLoginId());
            exchange = exchange.mutate()
                    .request(r -> r.header("X-User-Id", userId)
                            .header("X-Username", userId))
                    .build();
        } catch (Exception e) {
            log.debug("Auth failed for path {}: {}", path, e.getMessage());
            return writeUnauthorized(exchange);
        }

        return chain.filter(exchange);
    }

    private boolean isWhitelist(String path) {
        return WHITELIST.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> writeUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = JsonUtil.toJson(Result.error(4001, "未登录或 Token 已过期"));
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
