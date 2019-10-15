package com.liu.spark.streaming.upgrade;

import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/9 14:33
 * @Description: 新闻网站关键指标实时统计
 */
public class NewsRealtimeStatSpark {

    /**
     * main.
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NewsRealtimeStatSpark");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("metadata.broker.list", "localhost:9092");
        HashSet<String> topics = new HashSet<>();
        topics.add("news-access");
        JavaPairInputDStream<String, String> newsLog = KafkaUtils.createDirectStream(
                jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topics
        );
        //过滤出浏览页面的记录
        JavaPairDStream<String, String> viewLog = newsLog.filter(new Function<Tuple2<String, String>, Boolean>() {
            @Override
            public Boolean call(Tuple2<String, String> log) throws Exception {
                if ("view".equals(log._2.split(" ")[5])) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        //统计第一个指标页面pv，每10秒内页面被访问的次数
        calculatePagePv(viewLog);
        //统计第二个指标页面uv，每10秒内页面被访问的用户数量
        calculatePageUv(viewLog);
        //统计第三个指标注册用户数，每10秒内的注册用户数量
        calculateRegisterNum(newsLog);
        //统计第四个指标用户跳出数量，每10秒内的用户跳出数量
        calculateUserJumpNum(viewLog);
        //统计第五个指标板块Pv，每10秒内各个板块被访问的次数
        calculateSectionPv(viewLog);

        jssc.start();
        jssc.awaitTermination();
        jssc.stop();

    }

    /**
     * .
     * @param viewLog viewLog
     */
    public static void calculatePagePv(JavaPairDStream<String, String> viewLog) {
        viewLog.mapToPair(new PairFunction<Tuple2<String, String>, Long, Long>() {
            @Override
            public Tuple2<Long, Long> call(Tuple2<String, String> tuple) throws Exception {
                Long pageId = Long.valueOf(tuple._2.split(" ")[3]);
                return new Tuple2<>(pageId, 1L);
            }
        }).reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        }).print();
    }

    /**
     * .
     * @param viewLog viewLog
     */
    public static void calculatePageUv(JavaPairDStream<String, String> viewLog) {
        viewLog.map(new Function<Tuple2<String, String>, String>() {
            @Override
            public String call(Tuple2<String, String> log) throws Exception {
                return log._2.split(" ")[3] + "_" + log._2.split(" ")[2];
            }
        }).transform(new Function<JavaRDD<String>, JavaRDD<String>>() {
            @Override
            public JavaRDD<String> call(JavaRDD<String> rdd) throws Exception {
                return rdd.distinct();
            }
        }).mapToPair(new PairFunction<String, Long, Long>() {
            @Override
            public Tuple2<Long, Long> call(String pageIdAndUserId) throws Exception {
                Long pageId = Long.valueOf(pageIdAndUserId.split("_")[0]);
                return new Tuple2<>(pageId, 1L);
            }
        }).reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        }).print();
    }

    /**
     * .
     * @param newsLog newlog
     */
    public static void calculateRegisterNum(JavaPairInputDStream<String, String> newsLog) {
        newsLog.filter(new Function<Tuple2<String, String>, Boolean>() {
            @Override
            public Boolean call(Tuple2<String, String> log) throws Exception {
                if ("register".equals(log._2.split(" ")[5])) {
                    return true;
                } else {
                    return false;
                }
            }
        }).count().print();
    }

    /**
     * 用户跳出，指在一段时间内该用户只出现了一次.
     * @param viewLog viewLog
     */
    public static void calculateUserJumpNum(JavaPairDStream<String, String> viewLog) {
        viewLog.mapToPair(new PairFunction<Tuple2<String, String>, Long, Long>() {
            @Override
            public Tuple2<Long, Long> call(Tuple2<String, String> log) throws Exception {
                String userIdStr = "null".equals(log._2.split(" ")[2]) ? "-1" : log._2.split(" ")[2];
                Long userId = Long.valueOf(userIdStr);
                return new Tuple2<>(userId, 1L);
            }
        }).reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        }).filter(new Function<Tuple2<Long, Long>, Boolean>() {
            @Override
            public Boolean call(Tuple2<Long, Long> tuple) throws Exception {
                if (tuple._2 == 1) {
                    return true;
                } else {
                    return false;
                }
            }
        }).count().print();
    }

    /**
     * .
     * @param viewLog viewLog
     */
    public static void calculateSectionPv(JavaPairDStream<String, String> viewLog) {
        viewLog.mapToPair(new PairFunction<Tuple2<String, String>, String, Long>() {
            @Override
            public Tuple2<String, Long> call(Tuple2<String, String> log) throws Exception {

                return new Tuple2<>(log._2.split(" ")[4], 1L);
            }
        }).reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long v1, Long v2) throws Exception {
                return v1 + v2;
            }
        }).print();
    }

}
