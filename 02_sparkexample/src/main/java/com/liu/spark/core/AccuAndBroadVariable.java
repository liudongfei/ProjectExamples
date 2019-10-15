package com.liu.spark.core;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.util.LongAccumulator;

import java.util.Arrays;
import java.util.List;

/**
 * 累加器和广播变量的使用样例.
 */
public class AccuAndBroadVariable {

    /**
     * 累加器测试测试用例.
     */
    public void accumulatorTest(SparkContext sparkContext) {
        LongAccumulator longAccumulator = sparkContext.longAccumulator();
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        JavaRDD<Integer> rdd = sc.parallelize(numbers);
        rdd.foreach((VoidFunction<Integer>) integer -> longAccumulator.add(integer));
        System.out.println(longAccumulator.value());
    }

    /**
     * 广播变量测试用例.
     */
    public void broadcastTest(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        Integer factor = 3;
        Broadcast<Integer> broadcast = sc.broadcast(factor);
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> rdd = sc.parallelize(numbers);
        JavaRDD<Integer> rdd1 = rdd.map((Function<Integer, Integer>) integer -> integer * broadcast.value());
        rdd1.foreach((VoidFunction<Integer>) integer -> System.out.println(integer));
    }

    /**
     * 关闭sparkcontext.
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
                .appName("AccuAndBroadVariable")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        SparkContext sparkContext = spark.sparkContext();
        AccuAndBroadVariable variable = new AccuAndBroadVariable();
        variable.accumulatorTest(sparkContext);
        variable.broadcastTest(sparkContext);

        variable.close(sparkContext);
    }
}
