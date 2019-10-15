package com.liu.spark.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/8 17:46
 * @Description: 每隔10秒统计最近60秒内各类商品中点击量最高的三个商品
 */
public class Top3HotProduct {

    /**
     * main.
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Top3HotProduct");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        /**
         * 数据格式：username product category.
         */
        JavaReceiverInputDStream<String> dStream = jssc.socketTextStream("localhost", 9999);

        JavaPairDStream<String, Integer> dStream1 = dStream.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String line) throws Exception {
                return new Tuple2<>(line.split(" ")[2] + "_" + line.split(" ")[1], 1);
            }
        });

        JavaPairDStream<String, Integer> dStream2 =
                dStream1.reduceByKeyAndWindow(new Function2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer v1, Integer v2) throws Exception {
                        return v1 + v2;
                    }
                }, Durations.seconds(60), Durations.seconds(10));
        dStream2.foreachRDD(new VoidFunction<JavaPairRDD<String, Integer>>() {
            @Override
            public void call(JavaPairRDD<String, Integer> rdd) throws Exception {
                SparkSession session = new SparkSession(rdd.context());
                List<StructField> structFields = new ArrayList<>();
                structFields.add(DataTypes.createStructField("category", DataTypes.StringType, true));
                structFields.add(DataTypes.createStructField("product", DataTypes.StringType, true));
                structFields.add(DataTypes.createStructField("count", DataTypes.IntegerType, true));
                StructType structType = DataTypes.createStructType(structFields);
                JavaRDD<Row> rowJavaRDD = rdd.map(new Function<Tuple2<String, Integer>, Row>() {
                    @Override
                    public Row call(Tuple2<String, Integer> tuple) throws Exception {
                        return RowFactory.create(tuple._1.split(" ")[0], tuple._1.split(" ")[1], tuple._2);
                    }
                });
                session.createDataFrame(rowJavaRDD, structType).registerTempTable("product_count");
                session.sql("select category, product, count from"
                        + "(select "
                        + "category,"
                        + " product, "
                        + "count, "
                        + "row_number() over (partition by category order by count desc) rank "
                        + "from product_count) tmp where rank <= 3").show();
            }
        });

        jssc.start();
        jssc.awaitTermination();
        jssc.stop();
    }
}
