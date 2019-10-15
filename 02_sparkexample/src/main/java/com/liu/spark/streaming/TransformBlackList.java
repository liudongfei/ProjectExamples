package com.liu.spark.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.ArrayList;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/8 14:00
 * @Description: 广告点击黑名单过滤
 */
public class TransformBlackList {
    /**
     * main.
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("TransformBlackList");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        ArrayList<Tuple2<String, Boolean>> blacklist = new ArrayList<>();
        /**
         * 黑名单
         */
        blacklist.add(new Tuple2<>("tom", true));
        JavaPairRDD<String, Boolean> blackListRdd = jssc.sparkContext().parallelizePairs(blacklist);
        JavaReceiverInputDStream<String> dStream = jssc.socketTextStream("localhost", 9999);
        JavaPairDStream<String, String> dStream1 = dStream.mapToPair(new PairFunction<String, String, String>() {
            @Override
            public Tuple2<String, String> call(String line) throws Exception {
                return new Tuple2<>(line.split(" ")[1], line);
            }
        });
        JavaDStream<String> dStream2 = dStream1.transform(new Function<JavaPairRDD<String, String>, JavaRDD<String>>() {
            @Override
            public JavaRDD<String> call(JavaPairRDD<String, String> pairRdd) throws Exception {
                return pairRdd.leftOuterJoin(blackListRdd).filter(
                        new Function<Tuple2<String, Tuple2<String, Optional<Boolean>>>, Boolean>() {
                        @Override
                        public Boolean call(Tuple2<String, Tuple2<String, Optional<Boolean>>> tuple) throws Exception {
                            if (tuple._2._2().isPresent() && tuple._2._2.get()) {
                                return false;
                            }
                            return true;
                        }
                    }).map(new Function<Tuple2<String, Tuple2<String, Optional<Boolean>>>, String>() {
                        @Override
                        public String call(Tuple2<String, Tuple2<String, Optional<Boolean>>> tuple) throws Exception {
                            return tuple._2._1;
                        }
                    });
            }
        });
        dStream2.print();
        jssc.start();
        jssc.awaitTermination();
        jssc.close();
    }
}
