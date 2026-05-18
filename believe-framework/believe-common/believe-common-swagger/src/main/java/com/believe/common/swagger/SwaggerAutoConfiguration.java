package com.believe.common.swagger;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@AutoConfiguration
@ConditionalOnClass(name = "com.github.xiaoymin.knife4j.spring.configuration.Knife4jAutoConfiguration")
public class SwaggerAutoConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "believe.swagger")
    public SwaggerProperties swaggerProperties() {
        return new SwaggerProperties();
    }
}
