package com.liu.spark.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.Optional;
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
import java.util.List;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/8 13:05
 * @Description: 基于UpdateStateByKey实现全局统计WordCount
 */
public class UpdateStateByKeyWordCount {

    /**
     * main.
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("UpdateStateByKeyWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        jssc.checkpoint("hdfs:///user/liudongfei/spark/wordcount_checkpoint");
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
        JavaPairDStream<String, Integer> dStream3 = dStream2.updateStateByKey(
                new Function2<List<Integer>, Optional<Integer>, Optional<Integer>>() {
                @Override
                public Optional<Integer> call(List<Integer> v1, Optional<Integer> v2) throws Exception {
                    Integer tmpValue = 0;
                    if (v2.isPresent()) {
                        tmpValue = v2.get();
                    }
                    for (Integer integer : v1) {
                        tmpValue += integer;
                    }
                    return Optional.of(tmpValue);
                }

            });
        dStream3.print();

        jssc.start();
        jssc.awaitTermination();
        jssc.stop();
        jssc.close();
    }
}
