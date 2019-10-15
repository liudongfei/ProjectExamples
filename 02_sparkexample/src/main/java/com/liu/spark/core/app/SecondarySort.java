package com.liu.spark.core.app;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;
import scala.math.Ordered;

import java.io.Serializable;
import java.util.Objects;

/**
 * 二次排序，需要自己定义二次排序对象，指定首要排序的key，和次要排序的key.
 */

public class SecondarySort {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("SecondarySort")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
        JavaRDD<String> rdd = sc.textFile("");
        JavaPairRDD<SecondarySortKey, String> rdd1 = rdd.mapToPair(
                (PairFunction<String, SecondarySortKey, String>) s -> {
                String[] s1 = s.split(" ");
                SecondarySortKey secondarySortKey =
                        new SecondarySort.SecondarySortKey(Integer.valueOf(s1[0]), Integer.valueOf(s1[1]));
                return new Tuple2<>(secondarySortKey, s);
            });
        JavaPairRDD<SecondarySortKey, String> rdd2 = rdd1.sortByKey(false);
        JavaRDD<String> rdd3 = rdd2.map((Function<Tuple2<SecondarySortKey, String>, String>) tp -> tp._2);
        rdd3.foreach((VoidFunction<String>) s -> System.out.println(s));
        sc.close();
    }

    /**
     * 二次排序的key.
     */
    static class SecondarySortKey implements Ordered<SecondarySortKey>, Serializable {
        //定义需要进行排序的第一个key
        private int first;
        //定义需要进行排序的第二个key
        private int second;

        public SecondarySortKey(int first, int second) {
            this.first = first;
            this.second = second;
        }
        //定义比较方法

        @Override
        public int compare(SecondarySortKey other) {
            if (this.first - other.first != 0) {
                return this.first - other.first;
            } else {
                return this.second - other.second;
            }
        }
        //定义小于

        @Override
        public boolean $less(SecondarySortKey other) {
            if (this.first < other.first) {
                return true;
            } else if (this.first == other.second && this.second < other.second) {
                return true;
            }
            return false;
        }
        //定义大于

        @Override
        public boolean $greater(SecondarySortKey other) {
            if (this.first > other.first) {
                return true;
            }  else if (this.first == other.first && this.second > other.second) {
                return true;
            }
            return false;
        }
        //定义小于等于

        @Override
        public boolean $less$eq(SecondarySortKey other) {
            if (this.$less(other)) {
                return true;
            } else if (this.first == other.second && this.second == other.second) {
                return true;
            }
            return false;
        }
        //定义大于等于

        @Override
        public boolean $greater$eq(SecondarySortKey other) {
            if (this.$greater(other)) {
                return true;
            } else if (this.first == other.first && this.second == this.second) {
                return true;
            }
            return false;
        }

        @Override
        public int compareTo(SecondarySortKey other) {
            if (this.first - other.first != 0) {
                return this.first - other.first;
            } else {
                return this.second - other.second;
            }
        }

        public int getFirst() {
            return first;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }
        //定义等于

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SecondarySortKey that = (SecondarySortKey) o;
            return first == that.first && second == that.second;
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }
}
