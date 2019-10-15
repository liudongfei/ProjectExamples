package com.liu.spark.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.List;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/8 14:51
 * @Description: 每10秒统计最近60秒的搜索热点词，打印前3名
 */
public class WindowHotWord {
    /**
     * main.
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("WindowHotWord");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
        /**
         * 数据格式 leo hello.
         */
        JavaReceiverInputDStream<String> dStream = jssc.socketTextStream("localhost", 9999);
        JavaDStream<String> dStream1 = dStream.map(new Function<String, String>() {
            @Override
            public String call(String line) throws Exception {
                return line.split(" ")[1];
            }
        });
        JavaPairDStream<String, Integer> dStream2 = dStream1.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<>(word, 1);
            }
        });
        JavaPairDStream<String, Integer> dStream3 = dStream2.reduceByKeyAndWindow(
                new Function2<Integer, Integer, Integer>() {
                @Override
                public Integer call(Integer v1, Integer v2) throws Exception {
                    return v1 + v2;
                }
            }, Durations.seconds(60), Durations.seconds(10));
        JavaPairDStream<String, Integer> dStream4 = dStream3.transformToPair(
                new Function<JavaPairRDD<String, Integer>, JavaPairRDD<String, Integer>>() {
                @Override
                public JavaPairRDD<String, Integer> call(JavaPairRDD<String, Integer> rdd) throws Exception {
                    List<Tuple2<String, Integer>> res = rdd.mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {
                        @Override
                        public Tuple2<Integer, String> call(Tuple2<String, Integer> tuple) throws Exception {
                            return new Tuple2<>(tuple._2, tuple._1);
                        }
                    }).sortByKey(false).mapToPair(new PairFunction<Tuple2<Integer, String>, String, Integer>() {
                        @Override
                        public Tuple2<String, Integer> call(Tuple2<Integer, String> tuple) throws Exception {
                            return new Tuple2<>(tuple._2, tuple._1);
                        }
                    }).take(3);
                    for (Tuple2<String, Integer> tuple : res) {
                        System.out.println(tuple._1 + ":\t" + tuple._2);
                    }
                    return rdd;
                }
            });
        dStream4.print();

        jssc.start();
        jssc.awaitTermination();
        jssc.stop();
    }

}
