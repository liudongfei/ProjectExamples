package com.liu.flink.streaming;

import com.liu.flink.kafka.StringDesSchema;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.HashMap;
import java.util.Properties;

/**
 * 接入Kafka的数据流进行单词统计.
 * @Auther: liudongfei
 * @Date: 2019/7/3 10:18
 * @Description:
 */
public class KafkaWordCount {

    /**
     * main.
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        System.out.println("start");
        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "mincdh:9092");
        prop.setProperty("fetch.min.bytes", "2048");
        prop.setProperty("max.poll.records", "10000");
        prop.setProperty("max.poll.interval.ms", "500");
        prop.setProperty("group.id", "myconsumer");

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("state.checkpoints.dir", "hdfs:///tmp/flink/checkpoint/storing/");
        ParameterTool.fromArgs(args);
        ParameterTool parameterTool = ParameterTool.fromMap(paramMap);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(parameterTool);

        // 开启checkpoint，每1000ms一次
        env.enableCheckpointing(1000);
        // 设置checkpoint模式为，严格一次模式
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 设置每次checkpoint的超时时间，超时丢弃
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        // 设置两次checkpoint之间的最小时间
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        // 设置checkpoint的最大并发，也就是同时只能运行一个checkpoint
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // 用于开启checkpoints的外部持久化，在job failed的时候externalized checkpoint state无法自动清理，
        // 但是在job canceled的时候可以配置是删除还是保留state
        //env.getCheckpointConfig().enableExternalizedCheckpoints(
        //CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        FlinkKafkaConsumer010<String> kafkaConsumer010 =
                new FlinkKafkaConsumer010<>("mytopic", new StringDesSchema(), prop);
        SingleOutputStreamOperator<String> dataStreamSource = env.addSource(kafkaConsumer010)
                .setParallelism(5).name("kafka");
        SingleOutputStreamOperator<String> operator1 = dataStreamSource.map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return s + "map";
            }
        }).setParallelism(5).name("map");

        operator1.print().setParallelism(5);
        env.execute();
    }
}
