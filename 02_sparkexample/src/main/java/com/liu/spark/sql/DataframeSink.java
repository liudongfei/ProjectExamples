package com.liu.spark.sql;

import com.liu.spark.sql.bean.Student;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;

/**
 * dataframe的持久化方法.
 */
public class DataframeSink implements Serializable {

    /**
     * 将df持久化到hive表中.
     * @param df df
     */
    public void saveHive(Dataset<Row> df) {
        /**
         * format:
         *      hive, 指代hive的textfile表
         *      orc，指代hive的orc表
         *      parquet，指代hive的parquet表
         */
        df.write().format("hive").mode(SaveMode.Overwrite).saveAsTable("employees.student");
    }

    /**
     * 将df持久化到mysql中.
     * @param df df
     */
    public void saveMysql1(Dataset<Row> df) {
        Properties prop = new Properties();
        prop.put("user", "root");
        prop.put("password", "123456");
        //df.write().mode(SaveMode.Append).format("jdbc").options(options).save();
        df.write().jdbc("jdbc:mysql://mincdh:3306/employees", "student", prop);
    }

    /**
     * 将df持久化到mysql数据库中,这种方式很常用.
     * @param df df
     */
    public void saveMysql2(Dataset<Row> df) {
        df.javaRDD().foreachPartition(new VoidFunction<Iterator<Row>>() {
            @Override
            public void call(Iterator<Row> rowIterator) throws Exception {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = null;
                Statement state = null;
                try {
                    conn = DriverManager.getConnection(
                            "jdbc:mysql://mincdh:3306/employees",
                            "root",
                            "123456");
                    state = conn.createStatement();
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        int age = row.getInt(0);
                        int id = row.getInt(1);
                        String name = row.getString(2);
                        String sql = String.format(
                                "insert into student (id, name, age) values (%d, \"%s\", %d)", id, name, age);
                        System.out.println(sql);
                        state.execute(sql);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.close();
                    }
                }
            }
        });
    }

    /**
     * 将df持久化为csv格式的文本文件.
     * @param df df
     */
    public void saveCsv(Dataset<Row> df) {
        String path = "";
        df.write().csv(path);
    }

    /**
     * 将df持久化为json格式的文本文件.
     * @param df df
     */
    public void saveJson(Dataset<Row> df) {
        String path = "";
        df.write().json(path);
    }

    /**
     * 将df持久化为parquet格式文件.
     * @param df df
     */
    public void saveParquet(Dataset<Row> df) {
        String path = "";
        df.write().parquet(path);
    }


    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("DataframeBuilder")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
        String path = "file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/"
                + "02_sparkexample/data/students.txt";
        JavaRDD<String> rdd = sc.textFile(path);
        JavaRDD<Student> studentJavaRDD = rdd.map((Function<String, Student>) line -> {
            String[] split = line.split(",");
            /**
             * 注意row中的数据的顺序可能和文件中本身的顺序不一样.
             */
            return new Student(Integer.valueOf(split[0]), split[1], Integer.valueOf(split[2]));
        });
        Dataset<Row> df = spark.createDataFrame(studentJavaRDD, Student.class);
        df.show();
        DataframeSink sink = new DataframeSink();
        sink.saveHive(df);
        //sink.saveMysql1(df);
        //sink.saveMysql2(df);
    }
}
