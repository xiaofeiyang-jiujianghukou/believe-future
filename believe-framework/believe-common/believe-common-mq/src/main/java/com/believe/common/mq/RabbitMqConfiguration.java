package com.believe.common.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnProperty(name = "believe.mq.type", havingValue = "RABBITMQ")
public class RabbitMqConfiguration {

    public RabbitMqConfiguration() {
        log.info("MQ 模式: RabbitMQ");
    }
}
