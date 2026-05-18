package com.believe.common.data.config.single;

import com.believe.common.data.config.BaseDataSourceConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础数据源配置（单库模式）
 * <p>当读写分离、多数据源、分库分表都未开启时生效</p>
 */
@Configuration
@ConditionalOnProperty(
        name = {
                "believe.data.read-write-split.enabled",
                "believe.data.multi-datasource.enabled",
                "believe.data.sharding.enabled"
        },
        havingValue = "false",
        matchIfMissing = true
)
public class SingleDataSourceConfig extends BaseDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource singleDataSource() {
        return DataSourceBuilder.create().type(com.zaxxer.hikari.HikariDataSource.class).build();
    }

    @Override
    protected Map<String, DataSource> getTargetDataSources() {
        // 单数据源模式不需要多数据源映射
        return new HashMap<>();
    }

    @Override
    protected DataSource getDefaultDataSource() {
        return singleDataSource();
    }

    @Override
    protected boolean isDynamicRouting() {
        // 单数据源模式，不需要动态路由
        return false;
    }
}