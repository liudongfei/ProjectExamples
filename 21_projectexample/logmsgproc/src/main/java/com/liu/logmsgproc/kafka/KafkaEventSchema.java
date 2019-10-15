package com.liu.logmsgproc.kafka;

import com.google.gson.Gson;
import com.liu.logmsgcommon.bean.KafkaEventBean;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import org.apache.flink.streaming.util.serialization.SerializationSchema;
import org.apache.kafka.common.errors.SerializationException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * kafka event schema.
 * @Auther: liudongfei
 * @Date: 2019/3/18 14:59
 * @Description:
 */
public class KafkaEventSchema implements DeserializationSchema<KafkaEventBean>, SerializationSchema<KafkaEventBean> {

    @Override
    public KafkaEventBean deserialize(byte[] bytes) throws IOException {
        Gson gson = new Gson();
        return gson.fromJson(new String(bytes, "UTF-8"), KafkaEventBean.class);
    }

    @Override
    public boolean isEndOfStream(KafkaEventBean kafkaEventBean) {
        return false;
    }


    @Override
    public TypeInformation<KafkaEventBean> getProducedType() {
        return TypeInformation.of(KafkaEventBean.class);
    }

    @Override
    public byte[] serialize(KafkaEventBean kafkaEventBean) {
        Gson gson = new Gson();
        try {
            return gson.toJson(kafkaEventBean).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("error occur when convert KafkaEventBean to byte[]");
        }
    }
}
