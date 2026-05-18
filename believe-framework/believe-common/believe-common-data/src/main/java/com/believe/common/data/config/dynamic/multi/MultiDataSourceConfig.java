package com.believe.common.data.config.dynamic.multi;

import com.believe.common.data.config.BaseDataSourceConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "believe.data.multi-datasource", name = "enabled", havingValue = "true")
public class MultiDataSourceConfig extends BaseDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().type(com.zaxxer.hikari.HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().type(com.zaxxer.hikari.HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.report")
    public DataSource reportDataSource() {
        return DataSourceBuilder.create().type(com.zaxxer.hikari.HikariDataSource.class).build();
    }

    @Override
    protected Map<String, DataSource> getTargetDataSources() {
        Map<String, DataSource> map = new HashMap<>();
        map.put("master", masterDataSource());
        map.put("slave", slaveDataSource());
        map.put("report", reportDataSource());
        return map;
    }

    @Override
    protected DataSource getDefaultDataSource() {
        return masterDataSource();
    }
}