package com.liu.flink.example.stream;

import org.apache.flink.api.java.io.CsvInputFormat;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.util.Arrays;
import java.util.Properties;

public class DataSourceDemo {

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 直接读取文本文件
        DataStreamSource<String> streamSource = env.readTextFile("");
        // 用指定的inputformat读取文件
        DataStreamSource<String> streamSource1 = env.readFile(new CsvInputFormat<String>(new Path("")) {
            @Override
            protected String fillRecord(String s, Object[] objects) {
                return null;
            }
        }, "", FileProcessingMode.PROCESS_CONTINUOUSLY, 100);// 一旦文件发生变化，全部重新加载文件
        // 读取socket数据源
        DataStreamSource<String> streamSource2 = env.socketTextStream("", 9999);
        // 读取集合数据源
        DataStreamSource<Tuple2<Long, Long>> streamSource3 = env
                .fromElements(new Tuple2<>(1L, 5L), new Tuple2<>(3L, 7L));
        DataStreamSource<Integer> streamSource4 = env.fromCollection(Arrays.asList(1, 3, 4, 5));

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zoookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", "test");

        // 读取kafka数据源
        DataStreamSource<String> streamSource5 = env.addSource(
                new FlinkKafkaConsumer010<String>("", new SimpleStringSchema(), properties));

        // 自定义单个线程的数据接入器
        env.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {

            }

            @Override
            public void cancel() {

            }
        });

        // 自定义并发数据接入器
        env.addSource(new ParallelSourceFunction<String>() {
            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {

            }

            @Override
            public void cancel() {

            }
        });
    }
}
