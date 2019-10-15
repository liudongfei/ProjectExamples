package com.liu.kafka.simple;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * 自定义分区,key中以"1"开头的消息都分到partition0，
 * 其他的分到partition1.
 * @Auther: liudongfei
 * @Date: 2019/3/12 18:51
 * @Description:
 */
public class MyPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        Integer countForTopic = cluster.partitionCountForTopic(topic);
        String keyString = key.toString();
        if (countForTopic == 2 && keyString != null && keyString.startsWith("1")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
