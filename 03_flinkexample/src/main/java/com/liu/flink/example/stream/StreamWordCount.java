package com.liu.flink.example.stream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;



public class StreamWordCount {

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) throws Exception {
        // 获取流应用的环境变量
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> streamSource =
                env.readTextFile("hdfs:///user/flink/input/yarn-parallels-nodemanager-CDH.log");
        streamSource
                .flatMap((String s, Collector<String> collector)  -> {
                    String[] words = s.split(" ");
                    for (int i = 0; i < words.length; i++) {
                        collector.collect(words[i]);
                    }
                })
                .map((String s) -> new Tuple2<>(s, 1)) //注意使用java的Tuple2
                .keyBy(0)
                .sum(1)
                .print();
        env.execute("Streaming WordCount");
    }
}
