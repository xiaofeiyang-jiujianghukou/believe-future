package com.believe.common.data.aspect;

import com.believe.common.data.annotation.DataSource;
import com.believe.common.data.config.dynamic.DynamicDataSourceContextHolder;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class DataSourceAspect {

    @Before("@annotation(dataSource)")
    public void beforeSwitchDataSource(DataSource dataSource) {
        DynamicDataSourceContextHolder.setDataSourceKey(dataSource.value());
    }

    @After("@annotation(dataSource)")
    public void afterSwitchDataSource(DataSource dataSource) {
        DynamicDataSourceContextHolder.clear();
    }
}