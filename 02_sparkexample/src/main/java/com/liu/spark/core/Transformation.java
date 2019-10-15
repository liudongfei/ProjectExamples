package com.liu.spark.core;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Rdd的转换操作样例.
 */
public class Transformation implements Serializable {

    /**
     * 用于rdd的持久化操作，当rdd需要被多次使用时，可以使用持久化.
     * @param sparkContext sparkContext
     */
    public void persist(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        //持久化rdd必须在创建rdd之后连续调用cache或者persist
        JavaRDD<String> lines = sc.textFile(
                "file:///Users/liudongfei/Myworkspace/workspace/java_workspace"
                        + "/ProjectExamples/02_sparkexample/data/people.txt").persist(StorageLevel.MEMORY_ONLY());
        long beginTime = System.currentTimeMillis();
        long count = lines.count();
        long endTime = System.currentTimeMillis();
        System.out.println(count);
        System.out.println("cost1:" + (endTime - beginTime));

        beginTime = System.currentTimeMillis();
        count = lines.count();
        System.out.println(count);
        endTime = System.currentTimeMillis();
        System.out.println("cost2" + (endTime - beginTime));
    }

    /**
     * 用于rdd的重分区操作，
     * 一般是配合filter算子使用，主要是filter算子过滤掉了很多数据之后，出现了很多partition布局的状况
     * 样例:  将原来3个班级的学生重分为两个班级.
     * @param sparkContext sparkContext
     */
    public void coalesce(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);

        List<String> studentName = Arrays.asList("张三", "李四", "王五", "赵六", "麻子", "你好");
        JavaRDD<String> stuRdd = sc.parallelize(studentName, 3);

        JavaRDD<String> rdd1 = stuRdd.mapPartitionsWithIndex(
            (Function2<Integer, Iterator<String>, Iterator<String>>) (v1, v2) -> {
                List<String> studentList = new ArrayList<>();
                while (v2.hasNext()) {
                    studentList.add(v1 + "_" + v2.next());
                }
                return studentList.iterator();
            }, true);
        JavaRDD<String> rdd2 = rdd1.coalesce(2);
        JavaRDD<String> rdd3 = rdd2.mapPartitionsWithIndex(
            (Function2<Integer, Iterator<String>, Iterator<String>>) (v1, v2) -> {
                List<String> studentList = new ArrayList<>();
                while (v2.hasNext()) {
                    studentList.add(v1 + "_" + v2.next());
                }
                return studentList.iterator();
            }, true);
        for (String stu: rdd1.collect()) {
            System.out.println(stu);
        }
        for (String stu: rdd3.collect()) {
            System.out.println(stu);
        }
    }

    /**
     * 用于rdd的重分区，可以将分区增加或减少，建议使用场景sparksql从hive读取数据时，是按照hdfs的block进行数据分区的.
     * @param sparkContext sparkContext
     */
    public void repartition(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<String> studentName = Arrays.asList("张三", "李四", "王五", "赵六", "麻子", "你好");
        JavaRDD<String> stuRdd = sc.parallelize(studentName, 3);
        JavaRDD<String> rdd1 = stuRdd.mapPartitionsWithIndex(
            (Function2<Integer, Iterator<String>, Iterator<String>>) (v1, v2) -> {
                List<String> studentList = new ArrayList<>();
                while (v2.hasNext()) {
                    studentList.add(v1 + "_" + v2.next());
                }
                return studentList.iterator();
            }, true);
        JavaRDD<String> rdd2 = rdd1.repartition(4);
        JavaRDD<String> rdd3 = rdd2.mapPartitionsWithIndex(
            (Function2<Integer, Iterator<String>, Iterator<String>>) (v1, v2) -> {
                List<String> studentList = new ArrayList<>();
                while (v2.hasNext()) {
                    studentList.add(v1 + "_" + v2.next());
                }
                return studentList.iterator();
            }, true);
        for (String stu: rdd1.collect()) {
            System.out.println(stu);
        }
        for (String stu: rdd3.collect()) {
            System.out.println(stu);
        }
    }

    /**
     * 用于rdd中所有元素的转换操作.
     * @param sparkContext sparkContext
     */
    public void map(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> rdd = sc.parallelize(numbers);
        JavaRDD<Integer> rdd1 = rdd.map((Function<Integer, Integer>) val -> val * 2);
        rdd1.foreach((VoidFunction<Integer>) integer -> System.out.println(integer));
    }

    /**
     * 用于rdd中按照分区进行元素的转换操作，性能较高.
     * @param sparkContext sparkContext
     */
    public void mapPartitionWithIndex(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<String> studentName = Arrays.asList("张三", "李四", "王五", "赵六");
        JavaRDD<String> studentNameRdd = sc.parallelize(studentName, 2);

        JavaRDD<String> studentNameWithClassRdd = studentNameRdd.mapPartitionsWithIndex(
            (Function2<Integer, Iterator<String>, Iterator<String>>) (v1, v2) -> {
                List<String> studentWithClass = new ArrayList<>();
                while (v2.hasNext()) {
                    String studentName1 = v2.next();
                    studentWithClass.add(v1 + "_" + studentName1);
                }
                return studentWithClass.iterator();
            }, true);
        for (String stu: studentNameWithClassRdd.collect()) {
            System.out.println(stu);
        }
    }

    /**
     * 用于rdd所有中元素的过滤操作.
     * @param sparkContext sparkContext
     */
    public void filer(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> rdd = sc.parallelize(numbers);
        JavaRDD<Integer> rdd1 = rdd.filter((Function<Integer, Boolean>) val -> val % 2 == 0);
        rdd1.foreach((VoidFunction<Integer>) integer -> System.out.println(integer));
    }

    /**
     * 用于rdd中将一个元素拆分成多个元素并展开的操作.
     * @param sparkContext sparkContext
     */
    public void flatMap(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<String> lineList = Arrays.asList("hello me ", "hello you", "hello we");
        JavaRDD<String> rdd = sc.parallelize(lineList);
        JavaRDD<String> rdd1 = rdd.flatMap(
                (FlatMapFunction<String, String>) s -> Arrays.asList(s.split(" ")).iterator());
        rdd1.foreach((VoidFunction<String>) s -> System.out.println(s));
    }

    /**
     * 用于rdd中的元素进行去重操作.
     * @param sparkContext sparkContext
     */
    public void distinct(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<String> userLog = Arrays.asList(
                "zhangsan 2018-10-01",
                "lisi 2018-10-01",
                "wangwu 2018-10-01",
                "lisi 2018-10-01");
        JavaRDD<String> userLogRdd = sc.parallelize(userLog);
        JavaRDD<String> userRdd = userLogRdd.map((Function<String, String>) line -> line.split(" ")[0]);
        JavaRDD<String> result = userRdd.distinct();
        result.foreach((VoidFunction<String>) s -> System.out.println(s));
    }

    /**
     * 用于从rdd中随机获取一定数量的元素样本.
     * @param sparkContext sparkContext
     */
    public void sample(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<String> studentName = Arrays.asList("张三", "李四", "王五", "赵六");
        JavaRDD<String> studentRdd = sc.parallelize(studentName);
        //第三个参数是随机种子，可以不设置（做到完全随机）
        JavaRDD<String> sample = studentRdd.sample(false, 0.5, 4);
        for (String stu: sample.collect()) {
            System.out.println(stu);
        }
        for (String stu: studentRdd.collect()) {
            System.out.println(stu);
        }
    }

    /**
     * 用于pairRdd中按照元素进行分组的操作.
     * @param sparkContext sparkContext
     */
    public void groupByKey(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Tuple2<String, Integer>> scores = Arrays.asList(
                new Tuple2<>("class1", 80),
                new Tuple2<>("class2", 90),
                new Tuple2<>("class1", 65),
                new Tuple2<>("class2", 70));
        JavaPairRDD<String, Integer> rdd = sc.parallelizePairs(scores);
        JavaPairRDD<String, Iterable<Integer>> rdd1 = rdd.groupByKey();
        rdd1.foreach((VoidFunction<Tuple2<String, Iterable<Integer>>>) stringIterableTuple2 -> {
            System.out.println(stringIterableTuple2._1 + ":");
            Iterator<Integer> ite = stringIterableTuple2._2.iterator();
            while (ite.hasNext()) {
                System.out.println(ite.next());
            }
        });
    }

    /**
     * 用于pairRdd根据key分组求和的操作.
     * @param sparkContext sparkContext
     */
    public void reduceByKey(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Tuple2<String, Integer>> scores = Arrays.asList(
                new Tuple2<>("class1", 80),
                new Tuple2<>("class2", 90),
                new Tuple2<>("class1", 65),
                new Tuple2<>("class2", 70));
        JavaPairRDD<String, Integer> rdd = sc.parallelizePairs(scores);
        JavaPairRDD<String, Integer> rdd1 = rdd.reduceByKey(
                (Function2<Integer, Integer, Integer>) (v1, v2) -> v1 + v2);

        rdd1.foreach(
                (VoidFunction<Tuple2<String, Integer>>) tuple2 -> System.out.println(tuple2._1 + "\t" + tuple2._2));
    }

    /**
     * 使用AggragateByKey实现reduceByKey动作.
     * @param sparkContext sparkContext
     */
    public void aggregateByKey(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<String> userLog = Arrays.asList(
                "zhangsan 2018-10-01",
                "lisi 2018-10-01",
                "wangwu 2018-10-01",
                "lisi 2018-10-01");
        JavaRDD<String> lines = sc.parallelize(userLog);
        JavaRDD<String> words = lines.flatMap(
                (FlatMapFunction<String, String>) line -> Arrays.asList(line.split(" ")).iterator());

        JavaPairRDD<String, Integer> pairs = words.mapToPair(
                (PairFunction<String, String, Integer>) word -> new Tuple2<>(word, 1));

        /**
         * aggregateByKey,
         * 第一个参数，是每个key的初始值
         * 第二个参数，如何进行shuffle map side的本地聚合
         * 第三个参数，如何进行shuffle的reduce端的聚合.
         */
        JavaPairRDD<String, Integer> count = pairs.aggregateByKey(
                0,
                (Function2<Integer, Integer, Integer>) (v1, v2) -> v1 + v2,
                (Function2<Integer, Integer, Integer>) (v1, v2) -> v1 + v2
        );
        count.foreach((VoidFunction<Tuple2<String, Integer>>) stringIntegerTuple2 -> System.out.println(
                stringIntegerTuple2._1 + ":\t" + stringIntegerTuple2._2));
    }

    /**
     * 用于pairRdd根据key进行排序的操作.
     * @param sparkContext sparkContext
     */
    public void sortByKey(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Tuple2<Integer, String>> scores = Arrays.asList(
                new Tuple2<>(90, "zhao"),
                new Tuple2<>(80, "qian"),
                new Tuple2<>(70, "sun"),
                new Tuple2<>(60, "li"));
        JavaPairRDD<Integer, String> rdd = sc.parallelizePairs(scores);
        JavaPairRDD<Integer, String> rdd1 = rdd.sortByKey(false);
        rdd1.foreach((VoidFunction<Tuple2<Integer, String>>) tp2 -> System.out.println(tp2._1 + ":\t" + tp2._2));
    }

    /**
     * 用于两个pairRdd根据key进行join的操作,单个Rdd内部不会根据key先做合并.
     * 结果：
     * 4    (li,60)
     * 1    (zhao,90)
     * 3    (sun,70)
     * 2    (qian,80)
     * 2    (qian,30)
     * 2    (zhang,80)
     * 2    (zhang,30)
     * @param sparkContext sparkContext
     */
    public void join(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Tuple2<Integer, String>> studentList = Arrays.asList(
                new Tuple2<>(1, "zhao"),
                new Tuple2<>(2, "qian"),
                new Tuple2<>(3, "sun"),
                new Tuple2<>(4, "li"),
                new Tuple2<>(2, "zhang"));
        List<Tuple2<Integer, Integer>> scoresList = Arrays.asList(
                new Tuple2<>(1, 90),
                new Tuple2<>(2, 80),
                new Tuple2<>(3, 70),
                new Tuple2<>(4, 60),
                new Tuple2<>(2, 30));
        JavaPairRDD<Integer, String> studentrdd = sc.parallelizePairs(studentList);
        JavaPairRDD<Integer, Integer> scorerdd = sc.parallelizePairs(scoresList);
        JavaPairRDD<Integer, Tuple2<String, Integer>> rdd =
                (JavaPairRDD<Integer, Tuple2<String, Integer>>) studentrdd.join(scorerdd);
        rdd.foreach(new VoidFunction<Tuple2<Integer, Tuple2<String, Integer>>>() {
            @Override
            public void call(Tuple2<Integer, Tuple2<String, Integer>> tp2) throws Exception {
                System.out.println(tp2._1 + "\t(" + tp2._2._1 + "," + tp2._2._2 + ")");
            }
        });
    }

    /**
     * 用于两个pairRdd根据key进行合并的操作,单个Rdd内部会根据key先做合并.
     * 结果：
     * 4    ([li],[60])
     * 1    ([zhao],[90])
     * 3    ([sun],[70])
     * 2    ([qian, zhang],[80, 30])
     * @param sparkContext sparkContext
     */
    public void cogroup(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<Tuple2<Integer, String>> studentList = Arrays.asList(
                new Tuple2<>(1, "zhao"),
                new Tuple2<>(2, "qian"),
                new Tuple2<>(3, "sun"),
                new Tuple2<>(4, "li"),
                new Tuple2<>(2, "zhang"));
        List<Tuple2<Integer, Integer>> scoresList = Arrays.asList(
                new Tuple2<>(1, 90),
                new Tuple2<>(2, 80),
                new Tuple2<>(3, 70),
                new Tuple2<>(4, 60),
                new Tuple2<>(2, 30));
        JavaPairRDD<Integer, String> studentrdd = sc.parallelizePairs(studentList);
        JavaPairRDD<Integer, Integer> scorerdd = sc.parallelizePairs(scoresList);
        JavaPairRDD<Integer, Tuple2<Iterable<String>, Iterable<Integer>>> cogroup =
                (JavaPairRDD<Integer, Tuple2<Iterable<String>, Iterable<Integer>>>) studentrdd.cogroup(scorerdd);

        cogroup.foreach(new VoidFunction<Tuple2<Integer, Tuple2<Iterable<String>, Iterable<Integer>>>>() {
            @Override
            public void call(Tuple2<Integer, Tuple2<Iterable<String>, Iterable<Integer>>> tp2) throws Exception {
                System.out.println(tp2._1 + "\t(" + tp2._2._1 + "," + tp2._2._2 + ")");
            }
        });
    }

    /**
     * 用于两个rdd进行笛卡尔积操作.
     * @param sparkContext sparkContext
     */
    public void cartesian(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<String> num1 = Arrays.asList("1", "2", "3", "4");
        List<String> num2 = Arrays.asList("5", "6", "7", "8");
        JavaRDD<String> numRdd1 = sc.parallelize(num1);
        JavaRDD<String> numRdd2 = sc.parallelize(num2);
        JavaPairRDD<String, String> result = numRdd1.cartesian(numRdd2);
        for (Tuple2<String, String> tp2 : result.collect()) {
            System.out.println(tp2);
        }
    }

    /**
     * 用于取两个rdd的元素集合的交集.
     * @param sparkContext sparkContext
     */
    public void intersection(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);

        List<String> student1 = Arrays.asList("zhangsan", "lisi", "wangwu");
        List<String> student2 = Arrays.asList("wangwu", "zhaoliu");
        JavaRDD<String> class1Stu = sc.parallelize(student1);
        JavaRDD<String> class2Stu = sc.parallelize(student2);
        JavaRDD<String> resultRdd = class1Stu.intersection(class2Stu);
        for (String stu: resultRdd.collect()) {
            System.out.println(stu);
        }
    }

    /**
     * 用于对两个rdd的元素进行合并成为一个rdd的操作.
     * @param sparkContext sparkContext
     */
    public void union(SparkContext sparkContext) {
        JavaSparkContext sc = new JavaSparkContext(sparkContext);
        List<String> student1 = Arrays.asList("zhangsan", "lisi", "wangwu");
        List<String> student2 = Arrays.asList("wangwu", "zhaoliu");
        JavaRDD<String> stuRdd1 = sc.parallelize(student1);
        JavaRDD<String> stuRdd2 = sc.parallelize(student2);
        JavaRDD<String> resultRdd = stuRdd1.union(stuRdd2);
        for (String stu: resultRdd.collect()) {
            System.out.println(stu);
        }
    }

    /**
     * 关闭sparkContext.
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
                .appName("Transformation")
                .config("spark.sql.warehouse.dir", "hdfs:///user/hive/warehouse")
                .enableHiveSupport()
                .getOrCreate();
        SparkContext sparkContext = spark.sparkContext();
        Transformation transformation = new Transformation();

        transformation.persist(sparkContext);
        transformation.coalesce(sparkContext);
        transformation.repartition(sparkContext);

        transformation.map(sparkContext);
        transformation.mapPartitionWithIndex(sparkContext);
        transformation.filer(sparkContext);
        transformation.flatMap(sparkContext);
        transformation.distinct(sparkContext);
        transformation.sample(sparkContext);

        transformation.groupByKey(sparkContext);
        transformation.reduceByKey(sparkContext);
        transformation.aggregateByKey(sparkContext);
        transformation.sortByKey(sparkContext);

        transformation.join(sparkContext);
        transformation.cogroup(sparkContext);
        transformation.cartesian(sparkContext);
        transformation.intersection(sparkContext);
        transformation.union(sparkContext);
    }

}
