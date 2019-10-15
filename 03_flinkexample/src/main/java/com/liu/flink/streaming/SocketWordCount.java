package com.liu.flink.streaming;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * 通过socket接入数据流进行单词数量统计.
 * @Auther: liudongfei
 * @Date: 2018/12/11 13:56
 * @Description:
 */
public class SocketWordCount {

    public static void main(String[] args) throws Exception {
        ParameterTool params = ParameterTool.fromArgs(args);
        int port = params.getInt("port");
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> dataStreamSource = environment.socketTextStream("localhost", port, "\n");
        SingleOutputStreamOperator<WordWithCount> wordWithCountSingleOutputStreamOperator = dataStreamSource.flatMap(new FlatMapFunction<String, WordWithCount>() {
            @Override
            public void flatMap(String line, Collector<WordWithCount> collector) throws Exception {
                for (String word : line.split(" ")) {
                    collector.collect(new WordWithCount(word, 1));
                }
            }
        });
        wordWithCountSingleOutputStreamOperator.keyBy("word").timeWindow(Time.seconds(5)).reduce(new ReduceFunction<WordWithCount>() {
            @Override
            public WordWithCount reduce(WordWithCount t2, WordWithCount t1) throws Exception {
                return new WordWithCount(t1.word, (t1.count + t2.count));
            }
        }).print().setParallelism(1);
        environment.execute();

    }

    public static class WordWithCount {
        public String word;
        public long count;

        public WordWithCount() {
        }

        public WordWithCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public String toString() {
            return "WordWithCount{"
                    + "word='" + word + '\''
                    + ", count=" + count
                    + '}';
        }
    }
}
