package com.liu.flink.example.stream;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TransformDemo {
    /**
     * main,转换操作的样例.
     * @param args args
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple2<String, Integer>> streamSource = env.fromElements(new Tuple2<>("a", 3),
                new Tuple2<>("d", 4), new Tuple2<>("c", 2), new Tuple2<>("c", 5), new Tuple2<>("a", 5));
        streamSource.keyBy(0).reduce((t1, t2) -> new Tuple2<>(t1.f0, t1.f1 + t2.f1)).print();

        KeyedStream<Tuple2<Integer, Integer>, Tuple> keyedStream = env.fromElements(new Tuple2<>(1, 5),
                new Tuple2<>(2, 2), new Tuple2<>(2, 4), new Tuple2<>(1, 3)).keyBy(0);
        //keyedStream.sum(1).print();// 并不是一次将最终的整个数据集的最后求和结果输出，而是将每条记录叠加输出
        //keyedStream.max(1).print();// 同sum操作
        keyedStream.maxBy(1);// max和maxBy之间的区别在于max返回流中的最大值但是maxBy返回具有最大值的键

        env.execute("demo");
    }
}
