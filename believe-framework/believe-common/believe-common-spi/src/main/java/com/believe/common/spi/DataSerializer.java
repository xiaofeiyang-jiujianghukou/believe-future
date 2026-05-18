package com.believe.common.spi;

public interface DataSerializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<T> clazz);

    String getType();
}
