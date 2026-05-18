package com.believe.common.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "believe.mq")
public class MqProperties {

    public enum Type {
        ROCKETMQ, RABBITMQ, KAFKA
    }

    private Type type = Type.ROCKETMQ;
}
