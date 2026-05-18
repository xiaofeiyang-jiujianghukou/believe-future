package com.believe.common.spi.extension.serializer;

import com.believe.common.spi.DataSerializer;
import com.google.protobuf.MessageLite;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class ProtobufDataSerializer implements DataSerializer {

    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null) {
            return new byte[0];
        }
        if (!(obj instanceof MessageLite message)) {
            throw new IllegalArgumentException("Object must implement com.google.protobuf.MessageLite, got: " + obj.getClass());
        }
        return message.toByteArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        if (data == null || data.length == 0) {
            return null;
        }
        if (!MessageLite.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class must implement com.google.protobuf.MessageLite, got: " + clazz);
        }
        try {
            Method parseFrom = clazz.getMethod("parseFrom", byte[].class);
            return (T) parseFrom.invoke(null, (Object) data);
        } catch (Exception e) {
            log.error("Protobuf deserialization failed for {}: {}", clazz.getName(), e.getMessage(), e);
            throw new RuntimeException("Protobuf deserialization failed", e);
        }
    }

    @Override
    public String getType() {
        return "protobuf";
    }
}
