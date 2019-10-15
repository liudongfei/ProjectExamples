package com.liu.flink.kafka;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

import java.io.IOException;

/**
 * kafka中消息的u 列化与反序列化
 * @Auther: liudongfei
 * @Date: 2019/7/3 10:25
 * @Description:
 */
public class StringDesSchema implements DeserializationSchema<String>, SerializationSchema<String> {
    @Override
    public String deserialize(byte[] bytes) throws IOException {
        return new String(bytes);
    }

    @Override
    public boolean isEndOfStream(String s) {
        return false;
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return TypeInformation.of(String.class);
    }

    @Override
    public byte[] serialize(String s) {
        return s.getBytes();
    }
}
