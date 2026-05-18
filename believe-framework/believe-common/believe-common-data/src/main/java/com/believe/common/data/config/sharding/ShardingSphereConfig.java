package com.believe.common.data.config.sharding;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ShardingSphere 分库分表配置
 * <p>配置项：believe.data.sharding.enabled=true 时开启</p>
 *
 * <p>实际分片规则在 src/main/resources/sharding/sharding-config.yaml 中定义</p>
 */
@Configuration
@ConditionalOnProperty(prefix = "believe.data.sharding", name = "enabled", havingValue = "true")
public class ShardingSphereConfig {

    // 配置类仅用于控制开关
    // 分片规则由 YAML 文件驱动，无需 Java 代码
}