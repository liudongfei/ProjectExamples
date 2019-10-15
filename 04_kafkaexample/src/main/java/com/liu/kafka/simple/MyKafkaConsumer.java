package com.liu.kafka.simple;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * 简单的kafka消费者实例.
 * @Auther: liudongfei
 * @Date: 2019/3/12 15:17
 * @Description:
 */
public class MyKafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyKafkaConsumer.class);

    /**
     * main.
     * @param args args.
     * @throws InterruptedException exception
     */
    public static void main(String[] args) throws InterruptedException {

        Properties prop = new Properties();
        prop.put("bootstrap.servers",
                PropertiesUtil.getStringValue("app.properties","bootstrap.servers"));
        prop.put("group.id",
                PropertiesUtil.getStringValue("app.properties","group.id"));
        prop.put("key.deserializer",
                PropertiesUtil.getStringValue("app.properties","key.deserializer"));
        prop.put("value.deserializer",
                PropertiesUtil.getStringValue("app.properties","value.deserializer"));
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop);

        consumer.subscribe(Arrays.asList("mytopic"));//根据主题取数据
        //consumer.assign(Arrays.asList(new TopicPartition("mytopic", 0)));//根据分区取数据
        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ZERO);
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    LOGGER.info(consumerRecord.key() + ":\t" + consumerRecord.value()
                            + "\t" + consumerRecord.offset() + "\t" + consumerRecord.partition()
                            + "\t" + consumerRecord.timestamp());
                }
                Thread.sleep(1000);
                //consumer.commitSync();
                LOGGER.info("——————————————");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
