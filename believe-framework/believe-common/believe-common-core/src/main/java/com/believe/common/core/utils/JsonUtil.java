package com.believe.common.core.utils;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

public final class JsonUtil {

    private static final JsonMapper MAPPER = JsonMapper.builder()
            // Jackson 3 中，日期格式配置移到了 DateTimeFeature
            .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            // 美化输出（可选）
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    private JsonUtil() {}

    public static JsonMapper mapper() {
        return MAPPER;
    }

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }
}