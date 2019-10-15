package com.liu.kafka.simple;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 简单的kafka生产者实例.
 * @Auther: liudongfei
 * @Date: 2019/3/12 15:17
 * @Description:
 */
public class MyKafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyKafkaProducer.class);

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) throws InterruptedException {
        Properties prop = new Properties();
        prop.put("bootstrap.servers",
                PropertiesUtil.getStringValue("app.properties","bootstrap.servers"));
        prop.put("key.serializer",
                PropertiesUtil.getStringValue("app.properties","key.serializer"));
        prop.put("value.serializer",
                PropertiesUtil.getStringValue("app.properties","value.serializer"));

        KafkaProducer<String, String> producer = new KafkaProducer<>(prop);
        List<PartitionInfo> mytopic = producer.partitionsFor("mytopic");
        System.out.println(mytopic.size());
        //System.out.println(mytopic);
        for (int i = 0; i < 1000; i++) {
            Thread.sleep(500);
            System.out.println(i + 1);
            //只写入value，均衡分区
            //producer.send(new ProducerRecord("mytopic", "hello world" + i));
            //写入key和value，会按照key分区，相同的key会放入同一个分区中

            producer.send(new ProducerRecord("mytopic",
                    ThreadLocalRandom.current().nextInt(2),i + "", "hello" + i), new Callback() {
                        @Override
                        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                            if (e == null) {
                                LOGGER.info("msg send succeed");
                            } else {
                                LOGGER.error("msg send failed", e);
                            }
                        }
                    });
            //写入指定分区，以key和value
            //producer.send(new ProducerRecord("mytopic", 0, "", "hello"));
        }
        Thread.sleep(10000);

        producer.close();
    }
}
