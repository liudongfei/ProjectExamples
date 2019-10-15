package com.liu.spark.sql;

import com.liu.spark.sql.bean.Student;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 创建dataframe的方法.
 */
public class DataframeBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataframeBuilder.class);

    /**
     * 从hive数据库中读取数据创建df.
     * @param spark spark
     */
    public void createDfByHive(SparkSession spark) {
        LOGGER.info("reading data from hive ...");
        Dataset<Row> df = spark.sql("select * from employees.employees");
        df.printSchema();
        System.out.println(df.count());
        df.show();
    }

    /**
     * 从jdbc中读取数据创建df.
     * @param spark spark
     */
    public void createDfByJdbc(SparkSession spark) {
        Properties prop = new Properties();
        prop.put("user", "root");
        prop.put("password", "123456");
        Dataset<Row> employees = spark.read()
                .jdbc("jdbc:mysql://mincdh:3306/employees", "employees", prop);
        employees.show();
    }

    /**
     * 从csv格式的文本文件读取数据创建df.
     * @param spark spark
     */
    public void createDfByCsv(SparkSession spark) {
        String path = "file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/"
                + "02_sparkexample/data/cars.csv";
        Dataset<Row> df = spark.read()
                .option("header", true)
                .option("delimiter", ",")
                .csv(path);
        df.show();
    }

    /**
     * 从json格式的文本文件读取数据创建df.
     * @param spark spark
     */
    public void createDfByJson(SparkSession spark) {
        String path = "file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/"
                + "02_sparkexample/data/employee.json";
        Dataset<Row> df = spark.read().json(path);
        df.show();
    }

    /**
     * 从parquet格式的文件中读取数据创建df.
     * @param spark spark
     */
    public void createDfByParquet(SparkSession spark) {
        String path = "hdfs:///user/spark/parquet/student.parquet";
        Dataset<Row> df = spark.read().parquet(path);
        df.show();


    }

    /**
     * 从集合中创建df.
     * @param spark spark
     */
    public void createDfByCollection(SparkSession spark) {
        List<Row> rows = new ArrayList<>();
        rows.add(RowFactory.create("4", "5", "股东"));
        rows.add(RowFactory.create("9", "11", "股东"));
        rows.add(RowFactory.create("12", "13", "股东"));
        rows.add(RowFactory.create("16", "12", "股东"));
        rows.add(RowFactory.create(null, "17", "股东"));
        List<StructField> structFields = new ArrayList<>();
        structFields.add(DataTypes.createStructField("src", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("dst", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("rs", DataTypes.StringType, true));
        StructType structType = DataTypes.createStructType(structFields);
        Dataset<Row> df = spark.createDataFrame(rows, structType);
        df.show();
        df.printSchema();
    }

    /**
     * 使用rdd通过对象反射的方式创建df.
     * @param spark spark
     */
    public void createDfByRddWithRef(SparkSession spark) {
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
    }

    /**
     * 使用rdd通过动态编程的方式创建df.
     * @param spark spark
     */
    public void createDfByRddWithPro(SparkSession spark) {
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
        String path = "file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/"
                + "02_sparkexample/data/students.txt";
        JavaRDD<String> stringJavaRDD = sc.textFile(path);
        JavaRDD<Row> studentRdd = stringJavaRDD.map((Function<String, Row>) line -> RowFactory.create(
                Integer.valueOf(line.split(",")[0]),
                line.split(",")[1], Integer.valueOf(line.split(",")[2])));
        List<StructField> structFields = new ArrayList<>();
        structFields.add(DataTypes.createStructField("id", DataTypes.IntegerType, true));
        structFields.add(DataTypes.createStructField("name", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("age", DataTypes.IntegerType, true));
        StructType structType = DataTypes.createStructType(structFields);
        Dataset<Row> df = spark.createDataFrame(studentRdd, structType);
        df.show();
    }

    /**
     * 关闭spark连接.
     * @param spark spark
     */
    public void close(SparkSession spark) {
        if (spark != null) {
            spark.stop();
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
                .appName("DataframeBuilder")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        DataframeBuilder builder = new DataframeBuilder();
        //builder.createDfByHive(spark);
        //builder.createDfByJdbc(spark);
        //builder.createDfByCsv(spark);
        //builder.createDfByJson(spark);
        builder.createDfByParquet(spark);
        //builder.createDfByCollection(spark);
        //builder.createDfByRddWithRef(spark);
        //builder.createDfByRddWithPro(spark);
        builder.close(spark);
    }

}
