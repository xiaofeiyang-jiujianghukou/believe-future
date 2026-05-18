package com.believe.common.mq;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(MqProperties.class)
@Import({RocketMqConfiguration.class, RabbitMqConfiguration.class, KafkaMqConfiguration.class})
public class MqAutoConfiguration {
}
