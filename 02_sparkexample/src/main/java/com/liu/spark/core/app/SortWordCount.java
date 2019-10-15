package com.liu.spark.core.app;

import org.apache.spark.Dependency;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;
import scala.collection.Seq;

import java.util.Arrays;

/**
 * 对单词统计进行排序.
 */
public class SortWordCount {

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("SortWordCount")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
        JavaRDD<String> rdd = sc.textFile(
                "file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/"
                        + "02_sparkexample/data/people.txt");
        JavaRDD<String> rdd1 = rdd.flatMap(
                (FlatMapFunction<String, String>) s -> Arrays.asList(s.split(" ")).iterator());
        JavaPairRDD<String, Integer> pairRDD = rdd1.mapToPair(
                (PairFunction<String, String, Integer>) s -> new Tuple2<>(s, 1));
        JavaPairRDD<String, Integer> res = pairRDD.reduceByKey(
                (Function2<Integer, Integer, Integer>) (integer, integer2) -> integer + integer2);
        JavaPairRDD<Integer, String> rdd3 = res.mapToPair(
                (PairFunction<Tuple2<String, Integer>, Integer, String>) tp -> new Tuple2<>(tp._2, tp._1));
        JavaPairRDD<Integer, String> rdd4 = rdd3.sortByKey(false);
        JavaPairRDD<String, Integer> rdd5 = rdd4.mapToPair(
                (PairFunction<Tuple2<Integer, String>, String, Integer>) tp -> new Tuple2<>(tp._2, tp._1));
        rdd5.foreach((VoidFunction<Tuple2<String, Integer>>) tp -> System.out.println(tp._1 + "\t" + tp._2));
        Seq<Dependency<?>> dependencies = rdd5.rdd().dependencies();
        System.out.println(dependencies);
        sc.close();
    }
}
