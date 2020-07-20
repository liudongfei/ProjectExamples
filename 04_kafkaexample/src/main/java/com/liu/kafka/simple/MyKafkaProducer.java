package com.liu.kafka.simple;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

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
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties prop = new Properties();

        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                PropertiesUtil.getStringValue("app.properties","bootstrap.servers"));
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                PropertiesUtil.getStringValue("app.properties","key.serializer"));
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                PropertiesUtil.getStringValue("app.properties","value.serializer"));

        //该生产者的id
        prop.put(ProducerConfig.CLIENT_ID_CONFIG, "mytopic-producer");

        //消息发送确认机制，1：leader节点，0：不需要确认，-1，all：所有的ISR中的副本都成功写入
        prop.put(ProducerConfig.ACKS_CONFIG, "1");

        //发送异常重试次数
        prop.put(ProducerConfig.RETRIES_CONFIG, 10);

        // 一个连接上已经发出但未响应的请求数量大小
        prop.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        // 更新元数据的事件间隔
        prop.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, 300000);

        //客户端消息收集器的缓存大小
        //prop.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432B");

        //发送一批次批次消息的IO Buffer大小
        //prop.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384B");

        //消息压缩方式
        //prop.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        // 消费者，线程安全
        KafkaProducer<String, String> producer = new KafkaProducer<>(prop);
        //List<PartitionInfo> mytopic = producer.partitionsFor("mytopic");

        //System.out.println(mytopic);
        for (int i = 0; i < 100; i++) {
            //Thread.sleep(500);
            System.out.println(i + 1);
            //只写入value，均衡分区
            //producer.send(new ProducerRecord("mytopic", "hello world" + i));
            //写入key和value，会按照key分区，相同的key会放入同一个分区中
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    "mytopic", 3, i + "", "hello" + i);

            //三种发送方式
            //第一，发后即忘
            //producer.send(record);

            //第二，同步发送
            //producer.send(record).get();

            //第三，异步发送
            producer.send(record, new Callback() {
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
