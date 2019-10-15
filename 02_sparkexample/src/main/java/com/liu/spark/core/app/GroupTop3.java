package com.liu.spark.core.app;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 分组取topN.
 */
public class GroupTop3 {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("GroupTop3")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

        JavaRDD<String> rdd = sc.textFile("file:///Users/liudongfei/Myworkspace/workspace/java_workspace/"
                + "ProjectExamples/02_sparkexample/data/people.txt");
        JavaPairRDD<String, Integer> rdd1 = rdd.mapToPair(
                (PairFunction<String, String, Integer>) s -> new Tuple2<>(
                        s.split(" ")[0], Integer.valueOf(s.split(" ")[1])));
        JavaPairRDD<String, Iterable<Integer>> rdd2 = rdd1.groupByKey();
        JavaPairRDD<String, Iterable<Integer>> rdd3 = rdd2.mapToPair(
                (PairFunction<Tuple2<String, Iterable<Integer>>, String, Iterable<Integer>>) classScores -> {
                //分组取top3的核心算法，
                Integer[] top3 = new Integer[3];
                String className = classScores._1;
                Iterator<Integer> iterator = classScores._2.iterator();
                while (iterator.hasNext()) {
                    Integer value = iterator.next();
                    for (int i = 0; i < top3.length; i++) {
                        if (top3[i] == null) {
                            top3[i] = value;
                            break;
                        } else if (value > top3[i]) {
                            for (int j = 3 - 1; j > i; j--) {
                                top3[j] = top3[j - 1];
                            }
                            top3[i] = value;
                            break;
                        }
                    }
                }
                return new Tuple2<>(className, Arrays.asList(top3));
            });

        rdd3.foreach((VoidFunction<Tuple2<String, Iterable<Integer>>>) classScores -> {
            System.out.println(classScores._1);
            Iterator<Integer> iterator = classScores._2.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        });

        sc.close();
    }

}
