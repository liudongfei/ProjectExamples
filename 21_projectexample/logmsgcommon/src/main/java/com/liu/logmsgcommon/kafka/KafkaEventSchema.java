package com.liu.logmsgcommon.kafka;

import com.google.gson.Gson;
import com.liu.logmsgcommon.bean.KafkaEventBean;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * kafka message schema.
 * @Auther: liudongfei
 * @Date: 2019/4/3 14:58
 * @Description:
 */
public class KafkaEventSchema implements Deserializer<KafkaEventBean>, Serializer<KafkaEventBean> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventSchema.class);
    private Gson gson;

    @Override
    public void configure(Map<String, ?> map, boolean b) {
        this.gson = new Gson();
    }

    @Override
    public byte[] serialize(String s, KafkaEventBean kafkaEventBean) {
        try {
            return gson.toJson(kafkaEventBean).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("error occur when convert KafkaEventBean to byte[]", e);
            return null;
        }
    }

    @Override
    public KafkaEventBean deserialize(String s, byte[] bytes) {
        try {
            return gson.fromJson(new String(bytes, "UTF-8"), KafkaEventBean.class);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("error occur when convert byte[] to KafkaEventBean", e);
            return null;
        }
    }

    @Override
    public void close() {
    }
}
