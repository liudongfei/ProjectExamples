package com.liu.logmsgproc.kafka;

import com.liu.logmsgcommon.zookeeper.SignalListener;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.internal.Kafka010Fetcher;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartitionState;
import org.apache.flink.streaming.runtime.tasks.ProcessingTimeService;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;
import org.apache.flink.util.SerializedValue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * user defined kafka fetcher
 * signal listener impl.
 * @Auther: liudongfei
 * @Date: 2019/3/18 10:54
 * @Description:
 */
public class PausableKafkaFetcher<T> extends Kafka010Fetcher<T> implements SignalListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PausableKafkaFetcher.class);
    private final ReentrantLock pauseLock = new ReentrantLock(true);
    private final Condition pauseCond = pauseLock.newCondition();
    private volatile boolean paused = false;
    private String kafkaInfo = null;

    /**
     * PausableKafkaFetcher.
     * @param sourceContext sourceContext
     * @param assignedPartitionsWithInitialOffsets assignedPartitionsWithInitialOffsets
     * @param watermarksPeriodic watermarksPeriodic
     * @param watermarksPunctuated watermarksPunctuated
     * @param processingTimeProvider processingTimeProvider
     * @param autoWatermarkInterval autoWatermarkInterval
     * @param userCodeClassLoader userCodeClassLoader
     * @param taskNameWithSubtasks taskNameWithSubtasks
     * @param metricGroup metricGroup
     * @param deserializer deserializer
     * @param kafkaProperties kafkaProperties
     * @param pollTimeout pollTimeout
     * @param useMetrics useMetrics
     * @param paused paused
     * @throws Exception exception
     */
    public PausableKafkaFetcher(SourceFunction.SourceContext<T> sourceContext,
                                Map<KafkaTopicPartition, Long> assignedPartitionsWithInitialOffsets,
                                SerializedValue<AssignerWithPeriodicWatermarks<T>> watermarksPeriodic,
                                SerializedValue<AssignerWithPunctuatedWatermarks<T>> watermarksPunctuated,
                                ProcessingTimeService processingTimeProvider,
                                long autoWatermarkInterval,
                                ClassLoader userCodeClassLoader,
                                String taskNameWithSubtasks,
                                MetricGroup metricGroup,
                                KeyedDeserializationSchema<T> deserializer,
                                Properties kafkaProperties,
                                long pollTimeout,
                                boolean useMetrics,
                                boolean paused) throws Exception {
        super(sourceContext, assignedPartitionsWithInitialOffsets, watermarksPeriodic, watermarksPunctuated,
                processingTimeProvider, autoWatermarkInterval, userCodeClassLoader, taskNameWithSubtasks,
                metricGroup, deserializer, kafkaProperties, pollTimeout, useMetrics);
        try {
            this.kafkaInfo = kafkaProperties.toString();
            LOGGER.info("initialize kafka fetcher, paused:" + paused);
            pauseLock.lockInterruptibly();
        } catch (Exception e) {
            //throw new Exception(e);
        }
        try {
            this.paused = paused;
            pauseCond.signal();
        } finally {
            pauseLock.unlock();
        }

    }

    @Override
    protected void emitRecord(T record, KafkaTopicPartitionState<TopicPartition> partition, long offset,
                              ConsumerRecord<?, ?> consumerRecord) throws Exception {
        super.emitRecord(record, partition, offset, consumerRecord);
        try {
            pauseLock.lockInterruptibly();
        } catch (InterruptedException e) {
            //interrupted
        }
        try {
            while (paused) {
                pauseCond.await();
            }
        } finally {
            pauseLock.unlock();
        }
    }

    @Override
    public void onSignal(String path, String value) {
        try {
            pauseLock.lockInterruptibly();
        } catch (InterruptedException e) {
            //Interrupted
        }
        try {
            if ("0".equals(value)) {
                paused = true;
                LOGGER.info("pausing kafka fetcher: " + this.kafkaInfo);
            } else if ("1".equals(value)) {
                paused = false;
                LOGGER.info("resume kafka fetcher: " + this.kafkaInfo);
            }
            pauseCond.signal();
        } finally {
            pauseLock.unlock();
        }
    }
}
