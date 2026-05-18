package com.believe.gateway.filter.ratelimit;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.believe.common.core.result.Result;
import com.believe.common.core.utils.JsonUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class SentinelRateLimiter implements RateLimiter {

    private static final String RESOURCE_NAME = "gateway-rate-limit";

    private final RateLimitProperties properties;

    @PostConstruct
    public void initRules() {
        loadRules(properties.getQps());
        log.info("Sentinel rate limit initialized: resource={}, qps={}", RESOURCE_NAME, properties.getQps());
    }

    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefresh() {
        loadRules(properties.getQps());
        log.info("Sentinel rate limit refreshed: resource={}, qps={}", RESOURCE_NAME, properties.getQps());
    }

    private void loadRules(int qps) {
        FlowRule rule = new FlowRule(RESOURCE_NAME);
        rule.setCount(qps);
        rule.setGrade(com.alibaba.csp.sentinel.slots.block.RuleConstant.FLOW_GRADE_QPS);
        FlowRuleManager.loadRules(Collections.singletonList(rule));
    }

    @Override
    public Mono<Void> rateLimit(ServerWebExchange exchange, WebFilterChain chain) {
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }

        Entry entry = null;
        try {
            entry = SphU.entry(RESOURCE_NAME);
            Entry finalEntry = entry;
            return chain.filter(exchange).doFinally(signalType -> {
                if (finalEntry != null) {
                    finalEntry.exit();
                }
            });
        } catch (BlockException e) {
            log.warn("Sentinel rate limited: path={}", exchange.getRequest().getURI().getPath());
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String body = JsonUtil.toJson(Result.error(3001, "请求过于频繁，请稍后再试"));
            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(body.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
    }
}
