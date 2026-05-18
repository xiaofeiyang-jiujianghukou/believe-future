package com.believe.common.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "org.apache.rocketmq.spring.core.RocketMQTemplate")
@ConditionalOnProperty(name = "believe.mq.type", havingValue = "ROCKETMQ", matchIfMissing = true)
public class RocketMqConfiguration {

    public RocketMqConfiguration() {
        log.info("MQ 模式: RocketMQ");
    }
}
