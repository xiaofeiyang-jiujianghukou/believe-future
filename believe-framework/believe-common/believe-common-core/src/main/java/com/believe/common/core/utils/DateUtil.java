package com.believe.common.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

/**
 * 日期工具类
 */
public final class DateUtil {

    // ========== 常用日期格式 ==========

    /**
     * 默认日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * ISO 日期时间格式：yyyy-MM-dd'T'HH:mm:ss
     */
    public static final String ISO_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * 日期格式：yyyy-MM-dd
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 时间格式：HH:mm:ss
     */
    public static final String TIME_PATTERN = "HH:mm:ss";

    /**
     * 紧凑日期时间格式：yyyyMMddHHmmss
     */
    public static final String COMPACT_DATETIME_PATTERN = "yyyyMMddHHmmss";

    /**
     * 紧凑日期格式：yyyyMMdd
     */
    public static final String COMPACT_DATE_PATTERN = "yyyyMMdd";

    // ========== 默认格式化器 ==========

    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    private DateUtil() {}

    // ========== 格式化 ==========

    /**
     * 格式化 LocalDateTime 为默认格式
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_DATETIME_FORMATTER) : null;
    }

    /**
     * 格式化 LocalDateTime 为指定格式
     * @param dateTime 日期时间
     * @param pattern 格式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化 LocalDate 为默认日期格式
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * 格式化 LocalTime 为默认时间格式
     * @param time 时间
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : null;
    }

    // ========== 解析 ==========

    /**
     * 解析字符串为 LocalDateTime（使用默认格式）
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DEFAULT_DATETIME_FORMATTER);
    }

    /**
     * 解析字符串为 LocalDateTime（使用指定格式）
     * @param dateTimeStr 日期时间字符串
     * @param pattern 格式
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 解析字符串为 LocalDate
     * @param dateStr 日期字符串
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 解析字符串为 LocalTime
     * @param timeStr 时间字符串
     * @return LocalTime
     */
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    // ========== 当前时间 ==========

    /**
     * 获取当前时间字符串（默认格式）
     * @return 当前时间字符串
     */
    public static String now() {
        return format(LocalDateTime.now());
    }

    /**
     * 获取当前日期字符串
     * @return 当前日期字符串
     */
    public static String today() {
        return format(LocalDate.now());
    }

    /**
     * 获取当前时间字符串（HH:mm:ss）
     * @return 当前时间字符串
     */
    public static String currentTime() {
        return format(LocalTime.now());
    }

    // ========== 便捷方法 ==========

    /**
     * 获取一天的开始时间（00:00:00）
     * @param date 日期
     * @return 开始时间
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 获取一天的结束时间（23:59:59）
     * @param date 日期
     * @return 结束时间
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    /**
     * 判断是否为空
     */
    public static boolean isNull(TemporalAccessor temporal) {
        return temporal == null;
    }

    /**
     * 判断是否非空
     */
    public static boolean isNotNull(TemporalAccessor temporal) {
        return temporal != null;
    }
}