package com.believe.common.data.config;

import com.believe.common.data.config.dynamic.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置基类
 * <p>提供动态数据源的核心能力</p>
 */
public abstract class BaseDataSourceConfig {

    /**
     * 获取所有目标数据源的映射
     */
    protected abstract Map<String, DataSource> getTargetDataSources();

    /**
     * 获取默认数据源
     */
    protected abstract DataSource getDefaultDataSource();

    /**
     * 是否启用动态路由
     * <p>单数据源时返回 false，直接返回默认数据源</p>
     */
    protected boolean isDynamicRouting() {
        return true;
    }

    /**
     * 创建数据源（单数据源或动态路由）
     */
    public final DataSource createDataSource() {
        if (!isDynamicRouting()) {
            // 单数据源模式，直接返回默认数据源
            return getDefaultDataSource();
        }

        // 动态路由模式
        Map<Object, Object> targetDataSources = new HashMap<>(getTargetDataSources());
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(getDefaultDataSource());
        dynamicDataSource.setTargetDataSources(targetDataSources);

        return new LazyConnectionDataSourceProxy(dynamicDataSource);
    }

    // ========== 通用工具方法 ==========

    protected DataSource buildDataSource(String jdbcUrl, String username, String password) {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMaximumPoolSize(10);
        return ds;
    }
}