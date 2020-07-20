package com.liu.spark.streaming;

import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/7 22:16
 * @Description:
 */
public class KafkaDirectWordCount {

    /**
     * main.
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("KafkaDirectWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("metadata.broker.list", "localhost:9092");
        HashSet<String> topics = new HashSet<>();
        topics.add("WordCount");
        JavaPairInputDStream<String, String> directStream = KafkaUtils.createDirectStream(
                jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topics
        );
        JavaDStream<String> dStream = directStream.flatMap(
                (FlatMapFunction<Tuple2<String, String>, String>) tuple -> Arrays.asList(tuple._2.split(" "))
                        .iterator());
        JavaPairDStream<String, Integer> dStream1 = dStream.mapToPair(
                (PairFunction<String, String, Integer>) word -> new Tuple2<>(word, 1));
        JavaPairDStream<String, Integer> dStream2 = dStream1.reduceByKey(
                (Function2<Integer, Integer, Integer>) (v1, v2) -> v1 + v2);
        dStream2.print();

        jssc.start();
        jssc.awaitTermination();
        jssc.stop();
    }
}
