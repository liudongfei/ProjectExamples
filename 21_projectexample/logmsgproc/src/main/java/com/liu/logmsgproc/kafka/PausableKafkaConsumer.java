package com.liu.logmsgproc.kafka;

import com.liu.logmsgcommon.util.ConstantUtil;
import com.liu.logmsgcommon.zookeeper.ZKStateSignalService;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.config.OffsetCommitMode;
import org.apache.flink.streaming.connectors.kafka.internals.AbstractFetcher;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;
import org.apache.flink.util.PropertiesUtil;
import org.apache.flink.util.SerializedValue;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * user defined consumer.
 * @Auther: liudongfei
 * @Date: 2019/3/18 10:47
 * @Description:
 */
public class PausableKafkaConsumer<T> extends FlinkKafkaConsumer010<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PausableKafkaConsumer.class);
    private String watchPath;
    private volatile ZKStateSignalService zkSignalService;
    private PausableKafkaFetcher<T> myFetcher;

    public PausableKafkaConsumer(String topic, DeserializationSchema<T> valueDeserializer, Properties props) {
        super(topic, valueDeserializer, props);
        this.watchPath = props.getProperty(ConstantUtil.PAUSEABLE_KAFKA_TOPIC_PATH);
    }

    public PausableKafkaConsumer(String topic, KeyedDeserializationSchema<T> deserializer, Properties props) {
        super(topic, deserializer, props);
        this.watchPath = props.getProperty(ConstantUtil.PAUSEABLE_KAFKA_TOPIC_PATH);
    }

    public PausableKafkaConsumer(List<String> topics, DeserializationSchema<T> deserializer, Properties props) {
        super(topics, deserializer, props);
        this.watchPath = props.getProperty(ConstantUtil.PAUSEABLE_KAFKA_TOPIC_PATH);
    }

    public PausableKafkaConsumer(List<String> topics, KeyedDeserializationSchema<T> deserializer, Properties props) {
        super(topics, deserializer, props);
        this.watchPath = props.getProperty(ConstantUtil.PAUSEABLE_KAFKA_TOPIC_PATH);
    }

    @Override
    public void open(Configuration configuration) {
        super.open(configuration);
        ExecutionConfig.GlobalJobParameters globalJobParameters =
                getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        Map<String, String> globalParamMap = globalJobParameters.toMap();
        if (watchPath == null) {
            LOGGER.error("fail to open pausable kafka consumer, no watch path was found!");
            return;
        }
        String zkAddress = globalParamMap.get("zookeeper.address");
        String watchPrex = globalParamMap.get("zookeeper.rootpath");
        // regist zksignalservice
        zkSignalService = ZKStateSignalService.getInstance(zkAddress ,watchPrex);
        LOGGER.info("successfully opened pausable kafka consumer {}", watchPath);
    }

    @Override
    public void cancel() {
        super.cancel();
        unregisterSignal();
    }

    @Override
    public void close() throws Exception {
        super.close();
        unregisterSignal();
    }

    private void unregisterSignal() {
        if (zkSignalService != null) {
            String fullPath = "/" + watchPath;
            LOGGER.info("unregistering pausable kafka fetcher signal listener to path: {}", fullPath);
            zkSignalService.unregisterSignalListener(fullPath, myFetcher);
        }

    }

    @Override
    protected AbstractFetcher<T, ?> createFetcher(
            SourceContext<T> sourceContext,
            Map<KafkaTopicPartition, Long> assignedPartitionsWithInitialOffsets,
            SerializedValue<AssignerWithPeriodicWatermarks<T>> watermarksPeriodic,
            SerializedValue<AssignerWithPunctuatedWatermarks<T>> watermarksPunctuated,
            StreamingRuntimeContext runtimeContext, OffsetCommitMode offsetCommitMode) throws Exception {
        boolean useMetrics = !PropertiesUtil.getBoolean(properties, KEY_DISABLE_METRICS, false);

        // make sure that auto commit is disabled when our offset commit mode is ON_CHECKPOINTS;
        // this overwrites whatever setting the user configured in the properties
        if (offsetCommitMode == OffsetCommitMode.ON_CHECKPOINTS || offsetCommitMode == OffsetCommitMode.DISABLED) {
            properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        }
        boolean paused = false;
        String fullPath = "/" + watchPath;
        if (zkSignalService != null) {
            String signalValue = zkSignalService.getInitialSignalValue(fullPath);
            if ("0".equals(signalValue)) {
                paused = true;
            }
        }
        PausableKafkaFetcher<T> fetcher = new PausableKafkaFetcher<T>(
                sourceContext,
                assignedPartitionsWithInitialOffsets,
                watermarksPeriodic,
                watermarksPunctuated,
                runtimeContext.getProcessingTimeService(),
                runtimeContext.getExecutionConfig().getAutoWatermarkInterval(),
                runtimeContext.getUserCodeClassLoader(),
                runtimeContext.getTaskNameWithSubtasks(),
                runtimeContext.getMetricGroup(),
                deserializer,
                properties,
                pollTimeout,
                useMetrics,
                paused);
        if (zkSignalService != null) {
            LOGGER.info("start registering pausable kafka fetcher signal listener to path: {}", fullPath);
            zkSignalService.registerSignalListener(fullPath, fetcher);
        }
        myFetcher = fetcher;
        return fetcher;
    }
}
