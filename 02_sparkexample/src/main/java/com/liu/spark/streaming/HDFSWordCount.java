package com.liu.spark.streaming;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/7 20:31
 * @Description: 基于hdfs的文件实时流wordcount程序
 */
public class HDFSWordCount {
    //private static final Logger LOGGER = LoggerFactory.getLogger(HDFSWordCount.class);
    private static final Logger LOGGER = Logger.getLogger(HDFSWordCount.class);

    /**
     * main.
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {

        SparkConf conf = new SparkConf().setAppName("HDFSWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(10));
        LOGGER.info("start watching file directory");
        JavaDStream<String> dStream = jssc.textFileStream("hdfs:///user/spark/wordcount");
        JavaDStream<String> dStream1 = dStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String line) throws Exception {
                LOGGER.info("start split word");
                return Arrays.asList(line.split(" ")).iterator();
            }
        });
        JavaPairDStream<String, Integer> dStream2 = dStream1.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                LOGGER.info("start reset word count");
                return new Tuple2<>(word, 1);
            }
        });
        JavaPairDStream<String, Integer> dStream3 = dStream2.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        LOGGER.info("start outputing the streaming");
        dStream3.print();

        jssc.start();
        jssc.awaitTermination();
    }
}
