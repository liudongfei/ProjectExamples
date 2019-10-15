package com.liu.flink.streaming.sink;

import com.alibaba.fastjson.JSON;
import com.liu.flink.streaming.datasource.Student;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/7/6 13:11
 * @Description:
 */
public class KafkaUtil {
    private static String broker_list = "mincdh:9092";

    /**
     * .
     * @param args args
     */
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", broker_list);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer producer = new KafkaProducer<String, String>(props);
        for (int i = 1; i <= 100; i++) {
            Student student = new Student(i, "zhisheng" + i, "password" + i, 18 + (i % 15));
            ProducerRecord record = new ProducerRecord<String, String>(
                    "mytopic", null, null, JSON.toJSONString(student));
            producer.send(record);
            System.out.println("发送数据: " + JSON.toJSONString(student));
        }
        producer.flush();
    }
}
