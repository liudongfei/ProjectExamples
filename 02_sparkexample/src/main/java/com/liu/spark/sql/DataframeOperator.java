package com.liu.spark.sql;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;

import static org.apache.spark.sql.functions.col;

/**
 * 操作dataframe的方法.
 */
public class DataframeOperator {

    /**
     * 对df进行列选择操作.
     * @param df df
     */
    public static void select(Dataset<Row> df) {
        Dataset<Row> df1 = df.select(col("emp_no"), col("birth_date"));
        df1.show();
        /**
         * 对于指定的列使用函数
         */
        df.selectExpr("max(emp_no)").show();
        df.selectExpr("length(last_name)").show();
    }

    /**
     * 对dataframe的过滤操作，主要是数值类型的过滤和字符串类型的过滤.
     */
    public static void filter(Dataset<Row> df) {
        Dataset<Row> df1 = df.filter(col("birth_date").equalTo("'1960-09-19'"));
        df1.show();
        System.out.println(df1.count());
        df.filter(col("emp_no").equalTo(10043)).show();
        df.filter(col("emp_no").geq(10043)).show();
        df.filter(col("emp_no").gt(10043)).show();
        df.filter(col("emp_no").lt(10043)).show();
        df.filter(col("emp_no").leq(10043)).show();
        df.filter(col("emp_no").isin(10043, 10020)).show();
        df.filter(col("emp_no").leq(10043).and(col("emp_no").geq(10043))).show();
        df.filter(col("emp_no").geq(10043).or(col("emp_no").leq(10020))).show();
        df.filter(col("emp_no").isNaN()).show();
        df.filter(col("emp_no").isNull()).show();
    }

    /**
     * join操作.
     */
    public static void join() {
        Logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("dataFrameOperator")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        Dataset<Row> df = spark.sql("select * from employees.employees");
        df.show(5);
        Dataset<Row> df3 = spark.sql("select * from employees.salaries");
        df3.show(5);
        System.out.println("*******************");
        /**
         * join的方式，joinType Type of join to perform. Default `inner`. Must be one of:
         *                 `inner`, `cross`, `outer`, `full`, `full_outer`, `left`, `left_outer`,
         *                 `right`, `right_outer`, `left_semi`, `left_anti`.
         */
        Dataset<Row> df4 = df.join(df3, df.col("emp_no").equalTo(df3.col("emp_no")), "inner");
        df4.show(5);
        df.join(df3, "emp_no").show(5);
        spark.sql("select * from employees.employees emp "
                + "inner join employees.salaries sal on emp.emp_no = sal.emp_no").show(5);

        spark.close();
    }

    /**
     * drop操作.
     * @param df df
     */
    public static void drop(Dataset<Row> df) {
        df.drop("first_name").show();
        System.out.println(df.count());
        System.out.println(df.dropDuplicates().count());
        System.out.println(df.dropDuplicates("gender").count());

    }

    /**
     * map操作.
     * @param df df
     */
    public static void map(Dataset<Row> df) {
        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("dataFrameOperator")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        /**
         * 对df进行map操作，需要转换成rdd.
         */
        JavaRDD<Row> rdd = df.toJavaRDD().map(new Function<Row, Row>() {
            @Override
            public Row call(Row row) throws Exception {
                if (row.getString(1).equals("'1960-09-19'")) {
                    return RowFactory.create(row.getInt(0), row.getString(1), "M");
                } else {
                    return row;
                }
            }
        });
        /**
         * 生成dataframe的schema，将rdd转换成dataframe.
         */
        ArrayList<StructField> structFields = new ArrayList<>();
        structFields.add(DataTypes.createStructField("id", DataTypes.IntegerType, true));
        structFields.add(DataTypes.createStructField("birth_date", DataTypes.StringType, true));
        structFields.add(DataTypes.createStructField("first_name", DataTypes.StringType, true));
        StructType structType = DataTypes.createStructType(structFields);
        Dataset<Row> df5 = spark.createDataFrame(rdd, structType);

        df5.filter(col("birth_date").equalTo("'1960-09-19'")).show(100);
        System.out.println(df5.filter(col("birth_date").equalTo("'1960-09-19'")).count());

    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        //Dataset<Row> df1 = createDF1();
        //filter(df1);
        //select(df1);
        //drop(df1);
        //Dataset<Row> df3 = createDF3();
        //df3.filter(col("src").isNull()).show();
        //df3.filter(col("src").isNaN()).show();
        //df3.na().drop().show();

        SparkConf conf = new SparkConf().setMaster("local").setAppName("RowNumberWindowFunction");
        JavaSparkContext sc = new JavaSparkContext(conf);
        HiveContext hiveContext = new HiveContext(sc.sc());
        Dataset<Row> deptEmpSalDF = hiveContext.sql("select "
                + "emp_dept.emp_no, emp_dept.dept_no, sal.salary "
                + "from "
                + "(select distinct(emp.emp_no), dept.dept_no "
                + "from employees.employees emp "
                + "inner join employees.dept_emp dept on emp.emp_no=dept.emp_no limit 10) emp_dept "
                + "inner join employees.salaries sal on emp_dept.emp_no=sal.emp_no");
        deptEmpSalDF.registerTempTable("dept_emp_sal");
        //row_number()的开窗函数的使用方法
        //首先可以在使用select查询时，使用row_number()
        //然后在row_number()函数之后跟上over关键字
        //后面空格接上一个括号，括号内partition by表示根据哪个条件进行分组，也可以跟上order by对组内数据进行排序
        //最后row_number()针对分组内的数据，给一个组内行号
        Dataset<Row> result2 = hiveContext.sql("select  res.emp_no, res.dept_no, res.salary "
                + "from (select emp_no, dept_no, salary, row_number() over (partition by emp_no order by salary desc) "
                + "rank from dept_emp_sal) res where res.rank <=3");
        Dataset<Row> result = hiveContext.sql("select emp_no, dept_no, salary, row_number() "
                + "over (partition by emp_no order by salary desc) rank from dept_emp_sal");
        //result.show();
        result2.show();
        //deptEmpSalDF.printSchema();
        //deptEmpSalDF.show();
        sc.close();


    }


}
