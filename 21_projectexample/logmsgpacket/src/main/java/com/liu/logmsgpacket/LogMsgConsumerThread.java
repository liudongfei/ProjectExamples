package com.liu.logmsgpacket;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.rpc.protocol.UtilTcpClientService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/10 10:34
 * @Description:
 */
public class LogMsgConsumerThread implements Runnable {
    private Consumer consumer;
    private int partitionId;
    private UtilTcpClientService utilTcpClientService;

    /**
     * constructor.
     * @param consumer consumer
     * @param partitionId partitionId
     * @param utilTcpClientService utilTcpClientService
     */
    public LogMsgConsumerThread(Consumer consumer, int partitionId, UtilTcpClientService utilTcpClientService) {
        this.consumer = consumer;
        this.partitionId = partitionId;
        this.utilTcpClientService = utilTcpClientService;
    }

    @Override
    public void run() {
        consumer.assign(Arrays.asList(new TopicPartition("mytopic", partitionId)));
        try {
            while (true) {
                ConsumerRecords<String, KafkaEventBean> consumerRecords = consumer.poll(10);
                for (ConsumerRecord<String, KafkaEventBean> consumerRecord : consumerRecords) {
                    // TODO
                    utilTcpClientService.sendPacket(consumerRecord.value().getDataContent());
                    System.out.println(consumerRecord.value());

                }
                Thread.sleep(1000);
                //consumer.commitSync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
