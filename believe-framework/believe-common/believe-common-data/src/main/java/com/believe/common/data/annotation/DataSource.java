package com.believe.common.data.annotation;

import java.lang.annotation.*;

/**
 * 数据源切换注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String value() default "master";
}