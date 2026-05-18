package com.believe.common.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class JsonUtil {

    // 1. 核心变化：使用 com.fasterxml.jackson 包下的 ObjectMapper
    // 2. 只需简单的新建，不再手动注册JavaTimeModule
    private static final ObjectMapper MAPPER = new ObjectMapper()
            // 3. 关闭将日期写为时间戳的格式，改为ISO-8601字符串格式
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            // 4. 忽略JSON字符串中存在，但Java对象没有对应属性的情况
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonUtil() {}

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }
}