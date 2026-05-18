package com.believe.common.core.utils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ID 生成工具类
 *
 * <p>雪花算法/号段模式等分布式ID方案，等真正需要时再引入
 */
public final class IdUtil {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private IdUtil() {}

    /** UUID（无横线，推荐） */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /** UUID（带横线） */
    public static String uuidWithDash() {
        return UUID.randomUUID().toString();
    }

    /** 简单自增ID（仅用于开发测试） */
    public static long simpleId() {
        return ID_GENERATOR.getAndIncrement();
    }

    /** 重置自增计数器 */
    public static void resetSimpleId() {
        ID_GENERATOR.set(1);
    }
}