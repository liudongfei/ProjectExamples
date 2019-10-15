package com.liu.spark.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/7 21:10
 * @Description:
 */
public class KafkaWordCount {

    /**
     * main.
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("KafkaWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
        Map<String, Integer> topicThread = new HashMap<>();
        topicThread.put("WordCount", 1);
        JavaPairReceiverInputDStream<String, String> stream =
                KafkaUtils.createStream(jssc, "localhost:2181", "DefaultConsumerGroup", topicThread);
        JavaDStream<String> dStream = stream.flatMap(new FlatMapFunction<Tuple2<String, String>, String>() {
            @Override
            public Iterator<String> call(Tuple2<String, String> tuple) throws Exception {
                return Arrays.asList(tuple._2.split(" ")).iterator();
            }
        });
        JavaPairDStream<String, Integer> dStream1 = dStream.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<>(word, 1);
            }
        });
        JavaPairDStream<String, Integer> dStream2 = dStream1.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        dStream2.print();
        jssc.start();
        jssc.awaitTermination();
        jssc.stop();
    }
}
