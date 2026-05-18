package com.believe.common.data.autoconfigure;

import com.believe.common.data.config.MybatisPlusConfig;
import com.believe.common.data.config.dynamic.multi.MultiDataSourceConfig;
import com.believe.common.data.config.dynamic.readwrite.ReadWriteSplitConfig;
import com.believe.common.data.config.sharding.ShardingSphereConfig;
import com.believe.common.data.config.single.SingleDataSourceConfig;
import com.believe.common.data.injector.CustomSqlInjector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass(DataSource.class)
@EnableConfigurationProperties(DataProperties.class)
@Import({
        DataAutoConfiguration.CommonDataConfig.class,           // 通用配置
        DataAutoConfiguration.DataSourceModeRouter.class        // 模式路由
})
public class DataAutoConfiguration {

    /**
     * 通用配置（所有模式共享）
     */
    @Configuration
    @Import({
            MybatisPlusConfig.class,
            CustomSqlInjector.class
    })
    static class CommonDataConfig {}

    /**
     * 模式路由：根据 believe.data.mode 决定导入哪个配置
     */
    @Configuration
    static class DataSourceModeRouter {

        @Configuration
        @ConditionalOnProperty(prefix = "believe.data", name = "mode", havingValue = "single", matchIfMissing = true)
        @Import(SingleDataSourceConfig.class)
        static class Single {}

        @Configuration
        @ConditionalOnProperty(prefix = "believe.data", name = "mode", havingValue = "readwrite")
        @Import(ReadWriteSplitConfig.class)
        static class ReadWrite {}

        @Configuration
        @ConditionalOnProperty(prefix = "believe.data", name = "mode", havingValue = "multi")
        @Import(MultiDataSourceConfig.class)
        static class Multi {}

        @Configuration
        @ConditionalOnProperty(prefix = "believe.data", name = "mode", havingValue = "sharding")
        @Import(ShardingSphereConfig.class)
        static class Sharding {}
    }
}