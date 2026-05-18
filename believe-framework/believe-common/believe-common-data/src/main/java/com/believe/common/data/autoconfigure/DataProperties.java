package com.believe.common.data.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据层配置属性
 *
 * <p>配置前缀：believe.data</p>
 *
 * <pre>
 * believe:
 *   data:
 *     mode: single                    # single/readwrite/multi/sharding
 *     database-type: mysql            # mysql/postgresql/dameng/kingbase
 *     read-write-split:
 *       enabled: false
 *       master: master
 *       slave: slave
 *     multi-datasource:
 *       enabled: false
 *       primary: master
 *       names: master, slave, report
 *     sharding:
 *       enabled: false
 *       config-location: classpath:sharding/sharding-config.yaml
 *       show-sql: true
 *     mybatis-plus:
 *       auto-fill: true
 *       pagination: true
 *       optimistic-lock: true
 *       logic-delete: true
 *       logic-delete-value: 1
 *       logic-not-delete-value: 0
 *       table-prefix: ""
 *       sql-log: false
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "believe.data")
public class DataProperties {

    /**
     * 数据源模式：single / readwrite / multi / sharding
     */
    private String mode = "single";

    /**
     * 数据库类型：mysql / postgresql / dameng / kingbase
     */
    private String databaseType = "mysql";

    /**
     * 读写分离配置
     */
    private ReadWriteSplit readWriteSplit = new ReadWriteSplit();

    /**
     * 多数据源配置
     */
    private MultiDataSource multiDataSource = new MultiDataSource();

    /**
     * 分库分表配置
     */
    private Sharding sharding = new Sharding();

    /**
     * MyBatis-Plus 配置
     */
    private MybatisPlus mybatisPlus = new MybatisPlus();

    // ========== 内部配置类 ==========

    @Data
    public static class ReadWriteSplit {
        /** 是否启用 */
        private boolean enabled = false;
        /** 主库数据源名称 */
        private String master = "master";
        /** 从库数据源名称 */
        private String slave = "slave";
    }

    @Data
    public static class MultiDataSource {
        /** 是否启用 */
        private boolean enabled = false;
        /** 默认数据源 */
        private String primary = "master";
        /** 数据源名称列表 */
        private List<String> names = new ArrayList<>(List.of("master", "slave", "report"));
        /** 数据源配置映射 */
        private Map<String, DataSourceConfig> datasources = new HashMap<>();
    }

    @Data
    public static class Sharding {
        /** 是否启用 */
        private boolean enabled = false;
        /** YAML 配置文件路径 */
        private String configLocation = "classpath:sharding/sharding-config.yaml";
        /** 是否显示 SQL */
        private boolean showSql = true;
        /** 是否显示简单 SQL */
        private boolean simpleSql = false;
    }

    @Data
    public static class MybatisPlus {
        /** 自动填充 */
        private boolean autoFill = true;
        /** 分页 */
        private boolean pagination = true;
        /** 乐观锁 */
        private boolean optimisticLock = true;
        /** 逻辑删除 */
        private boolean logicDelete = true;
        /** 逻辑删除值 */
        private Integer logicDeleteValue = 1;
        /** 逻辑未删除值 */
        private Integer logicNotDeleteValue = 0;
        /** 表前缀 */
        private String tablePrefix = "";
        /** 是否开启 SQL 日志 */
        private boolean sqlLog = false;
    }

    @Data
    public static class DataSourceConfig {
        /** 驱动类名 */
        private String driverClassName;
        /** JDBC URL */
        private String url;
        /** 用户名 */
        private String username;
        /** 密码 */
        private String password;
        /** 最大连接数 */
        private Integer maximumPoolSize = 10;
        /** 最小空闲连接数 */
        private Integer minimumIdle = 5;
        /** 连接超时时间（毫秒） */
        private Long connectionTimeout = 30000L;
        /** 空闲超时时间（毫秒） */
        private Long idleTimeout = 600000L;
        /** 最大生命周期（毫秒） */
        private Long maxLifetime = 1800000L;
    }
}