package com.liu.flink.example.stream;

import com.liu.flink.example.People;

import java.util.Arrays;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


public class DataTypeDemo {
    /**
     * main，flink支持的数据类型.
     * @param args args
     */
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 开启kryo序列化，默认对于基本类型flink采用自带的序列化工具进行，除此之外调用kryo进行提取
        env.getConfig().enableForceKryo();
        // 原生数据类型，从给定元素集中创建数据集
        DataStreamSource<String> streamSource = env.fromElements("hello", "flink", "world");
        // 原生数据类型，从集合中创建数据集
        DataStreamSource<Integer> streamSource1 = env.fromCollection(Arrays.asList(3, 2, 1, 4, 5));
        // java Tuple类型
        DataStreamSource<Tuple2<String, Integer>> streamSource2 = env.fromElements(new Tuple2<String, Integer>("a", 1),
                new Tuple2<String, Integer>("b", 1), new Tuple2<String, Integer>("c", 2));
        // POJO类型
        DataStreamSource<People> streamSource3 = env.fromElements(new People("zhangsan", 14),
                new People("lisi", 15), new People("wangwu", 17),
                new People("zhaoliu", 13));

    }
}
