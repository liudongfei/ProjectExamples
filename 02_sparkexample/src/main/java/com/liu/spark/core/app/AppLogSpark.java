package com.liu.spark.core.app;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.List;

/**
 * 移动端app访问流量日志分析,日志格式：时间戳，设备ID，上行流量，下行流量，
 * 要求计算流量为前10的设备，首先根据总上行流量排序，其次根据下行流量排序，最后根据最小时间戳.
 */
public class AppLogSpark {

    /**
     * 将日志中的上行流量、下行流量、时间戳封装为AccessLogInfo.
     * @param accessLogRdd accessLogRdd
     * @return
     */
    public static JavaPairRDD<String, AccessLogInfo> rdd2AccessInfoRdd(JavaRDD<String> accessLogRdd) {
        return accessLogRdd.mapToPair(new PairFunction<String, String, AccessLogInfo>() {
            @Override
            public Tuple2<String, AccessLogInfo> call(String line) throws Exception {
                String deviceId = line.split("\t")[1];
                long uptraffic = Long.valueOf(line.split("\t")[2]);
                long downtraffic = Long.valueOf(line.split("\t")[3]);
                long timestamp = Long.valueOf(line.split("\t")[0]);
                AccessLogInfo accessLogInfo = new AccessLogInfo(uptraffic, downtraffic, timestamp);
                return new Tuple2<>(deviceId, accessLogInfo);
            }
        });
    }

    /**
     * 对各个设备的上行流量和下行流量进行求和，选择每个设备的最早访问时间.
     * @param accessLogInfoJavaPairRDD accessLogInfoJavaPairRDD
     * @return
     */
    public static JavaPairRDD<String, AccessLogInfo> reduceAccessLogInfoRdd(
            JavaPairRDD<String, AccessLogInfo> accessLogInfoJavaPairRDD) {
        return accessLogInfoJavaPairRDD.reduceByKey(new Function2<AccessLogInfo, AccessLogInfo, AccessLogInfo>() {
            @Override
            public AccessLogInfo call(AccessLogInfo accessLogInfo1, AccessLogInfo accessLogInfo2) throws Exception {
                long uptraffic = accessLogInfo1.getUptraffic() + accessLogInfo2.getUptraffic();
                long downtraffic = accessLogInfo1.getDowntraffic() + accessLogInfo2.getDowntraffic();
                long timestamp = accessLogInfo1.getTimestamp() > accessLogInfo2.getTimestamp() ? accessLogInfo2
                        .getTimestamp() : accessLogInfo1.getTimestamp();
                return new AccessLogInfo(uptraffic, downtraffic, timestamp);
            }
        });
    }

    /**
     * 对排序的key进行封装.
     * @param accessLogInfoJavaPairRDD accessLogInfoJavaPairRDD
     * @return
     */
    public static JavaPairRDD<AppSortKey, String> getSortKeyRdd(
            JavaPairRDD<String, AccessLogInfo> accessLogInfoJavaPairRDD) {
        return accessLogInfoJavaPairRDD.mapToPair(
                (PairFunction<Tuple2<String, AccessLogInfo>, AppSortKey, String>) tp2 -> {
                String deviceId = tp2._1;
                AppSortKey appSortKey = new AppSortKey(
                        tp2._2.getUptraffic(), tp2._2.getDowntraffic(), tp2._2.getTimestamp());
                return new Tuple2<>(appSortKey, deviceId);
            });
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("AppLogSpark")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
        JavaRDD<String> accessLog = sc.textFile(
                "file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/"
                        + "02_sparkexample/data/access.log");
        JavaPairRDD<String, AccessLogInfo> rdd1 = rdd2AccessInfoRdd(accessLog);
        JavaPairRDD<String, AccessLogInfo> rdd2 = reduceAccessLogInfoRdd(rdd1);
        JavaPairRDD<AppSortKey, String> sortKeyRdd = getSortKeyRdd(rdd2);
        JavaPairRDD<AppSortKey, String> appSortKeyStringJavaPairRDD = sortKeyRdd.sortByKey(false);

        List<Tuple2<AppSortKey, String>> list = appSortKeyStringJavaPairRDD.take(10);
        for (Tuple2<AppSortKey, String> tp2: list) {
            System.out.println(tp2._2 + "\t" + tp2._1);
        }
        sc.close();
    }
}
