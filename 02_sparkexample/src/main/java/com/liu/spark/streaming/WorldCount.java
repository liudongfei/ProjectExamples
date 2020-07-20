package com.liu.spark.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/7 14:47
 * @Description: 实时WorldCount程序开发
 */
public class WorldCount {

    /**
     * .
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        /**
         * 至少使用两个线程来运行这个程序.
         */
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("WorldCount");
        /**
         * 创建StreamingContext，传入config，和batch的时间长度.
         */
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        JavaReceiverInputDStream<String> dStream = jssc.socketTextStream("localhost", 9999);
        JavaDStream<String> dStream1 = dStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String line) throws Exception {
                return Arrays.asList(line.split(" ")).iterator();
            }
        });
        JavaPairDStream<String, Integer> dStream2 = dStream1.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<>(word, 1);
            }
        });
        JavaPairDStream<String, Integer> dStream3 = dStream2.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        dStream3.print();
        jssc.start();
        jssc.awaitTermination();
        jssc.close();
    }
}
