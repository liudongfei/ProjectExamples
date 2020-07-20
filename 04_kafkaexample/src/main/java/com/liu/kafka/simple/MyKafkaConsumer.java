package com.liu.kafka.simple;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
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

        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                PropertiesUtil.getStringValue("app.properties","bootstrap.servers"));
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                PropertiesUtil.getStringValue("app.properties","key.deserializer"));
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                PropertiesUtil.getStringValue("app.properties","value.deserializer"));

        prop.put(ConsumerConfig.GROUP_ID_CONFIG,
                PropertiesUtil.getStringValue("app.properties","group.id"));

        prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        prop.put(ConsumerConfig.CLIENT_ID_CONFIG, "mytopic-consumer");


        // 消费者，非线程安全
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop);

        List<String> topicList = Arrays.asList("mytopic");
        //consumer.subscribe(topicList);//根据主题取数据
        //consumer.subscribe(Pattern.compile("my*"));//按照正则表达式消费
        consumer.assign(Arrays.asList(new TopicPartition("mytopic", 3)));//根据分区取数据
        //consumer.poll(1000);
        //consumer.seekToBeginning(Arrays.asList(new TopicPartition("mytopic", 3)));
        //consumer.seek(new TopicPartition("mytopic", 3), 269);
        // partition 3的当前消费offset
        long lastConsumedOffset = -1;
        try {

            while (true) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ZERO);
                LOGGER.info("get batch!");
                // 按照分区维度消费数据
                for (TopicPartition tp : consumerRecords.partitions()) {
                    if (tp.partition() == 3) {
                        lastConsumedOffset = consumerRecords.records(tp)
                                .get(consumerRecords.records(tp).size() - 1).offset();
                        LOGGER.info("partition 3 lastConsumedOffset:\t{}", lastConsumedOffset);
                    }
                    for (ConsumerRecord<String, String> record : consumerRecords.records(tp)) {
                        LOGGER.info(record.partition() + "\t" + record.value());
                    }
                }

                //按照主题维度进行消费
                for (String topic : topicList) {
                    for (ConsumerRecord<String, String> record : consumerRecords.records(topic)) {
                        LOGGER.info(record.topic() + "\t" + record.value());
                    }
                }


                // 不按照分区和主题消费
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
