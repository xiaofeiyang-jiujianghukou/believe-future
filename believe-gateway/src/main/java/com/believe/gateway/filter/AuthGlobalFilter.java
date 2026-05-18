package com.believe.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.believe.common.core.result.Result;
import com.believe.common.core.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RefreshScope
public class AuthGlobalFilter implements WebFilter, Ordered {

    private static final Set<String> DEFAULT_WHITELIST = Set.of(
            "/auth/login", "/auth/register", "/auth/captcha", "/auth/logout",
            "/actuator", "/favicon.ico"
    );

    private final Set<String> whitelist;

    public AuthGlobalFilter(
            @Value("${believe.gateway.auth.whitelist:#{null}}") List<String> configuredWhitelist) {
        if (configuredWhitelist == null || configuredWhitelist.isEmpty()) {
            this.whitelist = DEFAULT_WHITELIST;
        } else {
            Set<String> merged = new HashSet<>(DEFAULT_WHITELIST);
            merged.addAll(configuredWhitelist);
            this.whitelist = Collections.unmodifiableSet(merged);
            log.info("Auth whitelist e,xtended: defaults + {}", configuredWhitelist);
        }
    }

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
        return whitelist.stream().anyMatch(path::startsWith);
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
