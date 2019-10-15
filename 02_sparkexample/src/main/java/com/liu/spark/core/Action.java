package com.liu.spark.core;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * spark Rdd的Action操作样例类.
 */
public class Action {

    /**
     * 用于rdd中元素之间的合并.
     */
    public void reduce(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        JavaRDD<Integer> rdd = sc.parallelize(numbers);
        Integer rdd2 = rdd.reduce((Function2<Integer, Integer, Integer>) (integer, integer2) -> integer + integer2);
        System.out.print(rdd2);
    }

    /**
     * 用于将rdd中的元素转变成一个集合.
     */
    public void collect(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> rdd = sc.parallelize(numbers);
        JavaRDD<Integer> rdd1 = rdd.map((Function<Integer, Integer>) integer -> integer * 2);
        List<Integer> collect = rdd1.collect();
        System.out.println(collect);
    }

    /**
     * 用于rdd中的元素数量计算.
     */
    public void count(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> rdd = sc.parallelize(numbers);
        long count = rdd.count();
        System.out.println(count);
    }

    /**
     * 用于从rdd中取指定数量的元素集合.
     */
    public void take(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> rdd = sc.parallelize(numbers);
        JavaRDD<Integer> rdd1 = rdd.map((Function<Integer, Integer>) integer -> integer * 2);
        List<Integer> collect = rdd1.take(3);
        System.out.println(collect);
    }

    /**
     * 用于将rdd中的元素持久化为文本文件.
     */
    public void saveAsTextFile(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> rdd = sc.parallelize(numbers);
        JavaRDD<Integer> rdd1 = rdd.map((Function<Integer, Integer>) integer -> integer * 2);
        rdd1.saveAsTextFile("hdfs:///tmp/test");
    }

    /**
     * 用于pairRdd中根据key计数使用返回map.
     */
    public void countByKey(SparkContext sparkContext) {

        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Tuple2<String, Integer>> scores = new ArrayList<>();
        scores.add(new Tuple2<>("class1", 80));
        scores.add(new Tuple2<>("class2", 90));
        scores.add(new Tuple2<>("class1", 65));
        scores.add(new Tuple2<>("class2", 70));
        JavaPairRDD<String, Integer> rdd = sc.parallelizePairs(scores);
        Map<String, Long> res = rdd.countByKey();
        for (Map.Entry<String, Long> r : res.entrySet()) {
            System.out.println(r.getKey() + "\t" + r.getValue());
        }
    }

    /**
     * 关闭session.
     * @param sparkContext sparkContext
     */
    public void close(SparkContext sparkContext) {
        if (sparkContext != null) {
            sparkContext.stop();
        }
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("Action")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        SparkContext sparkContext = spark.sparkContext();

        Action action = new Action();
        action.reduce(sparkContext);
        action.collect(sparkContext);
        action.count(sparkContext);
        action.take(sparkContext);
        //action.saveAsTextFile(sparkContext);
        action.countByKey(sparkContext);
    }
}
