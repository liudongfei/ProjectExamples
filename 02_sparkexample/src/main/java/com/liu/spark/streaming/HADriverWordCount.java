package com.liu.spark.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function0;
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
 * @Date: 2018/12/8 19:23
 * @Description:
 * 支持driver节点失败重启，注意提交submit时要使用cluster模式，同时加上-supervise参数
 */
public class HADriverWordCount {
    private static String checkpoint = "hdfs:///user/liudongfei/spark/checkpoint";
    private  static Function0<JavaStreamingContext> contextFactory = (Function0<JavaStreamingContext>) () -> {
        /**
         * 至少使用两个线程来运行这个程序.
         */
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("WorldCount");
        /**
         * 创建StreamingContext，传入config，和batch的时间长度.
         */
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));

        JavaReceiverInputDStream<String> dStream = jssc.socketTextStream("localhost", 9999);
        JavaDStream<String> dStream1 = dStream.flatMap(
                (FlatMapFunction<String, String>) line -> Arrays.asList(line.split(" ")).iterator());
        JavaPairDStream<String, Integer> dStream2 = dStream1.mapToPair(
                (PairFunction<String, String, Integer>) word -> new Tuple2<>(word, 1));
        JavaPairDStream<String, Integer> dStream3 = dStream2.reduceByKey(
                (Function2<Integer, Integer, Integer>) (v1, v2) -> v1 + v2);
        dStream3.print();
        jssc.checkpoint(checkpoint);
        return jssc;
    };

    /**
     * main.
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        JavaStreamingContext jssc = JavaStreamingContext.getOrCreate(checkpoint, contextFactory);
        jssc.start();
        jssc.awaitTermination();
        jssc.stop();

    }
}
