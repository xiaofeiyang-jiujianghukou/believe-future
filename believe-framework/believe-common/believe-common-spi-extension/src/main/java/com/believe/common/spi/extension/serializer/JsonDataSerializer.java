package com.believe.common.spi.extension.serializer;

import com.believe.common.core.utils.JsonUtil;
import com.believe.common.spi.DataSerializer;

import java.nio.charset.StandardCharsets;

public class JsonDataSerializer implements DataSerializer {

    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null) {
            return new byte[0];
        }
        return JsonUtil.toJson(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        if (data == null || data.length == 0) {
            return null;
        }
        return JsonUtil.fromJson(new String(data, StandardCharsets.UTF_8), clazz);
    }

    @Override
    public String getType() {
        return "json";
    }
}
