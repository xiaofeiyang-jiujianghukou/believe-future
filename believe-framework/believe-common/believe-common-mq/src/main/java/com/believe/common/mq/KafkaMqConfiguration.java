package com.believe.common.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(name = "believe.mq.type", havingValue = "KAFKA")
public class KafkaMqConfiguration {

    public KafkaMqConfiguration() {
        log.info("MQ 模式: Kafka");
    }
}
