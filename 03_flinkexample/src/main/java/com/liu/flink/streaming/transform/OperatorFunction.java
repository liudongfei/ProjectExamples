package com.liu.flink.streaming.transform;

import com.liu.flink.streaming.datasource.MysqlDataSource;
import com.liu.flink.streaming.datasource.Student;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/7/6 15:09
 * @Description:
 */
public class OperatorFunction {
    /**
     * .
     * @param args args
     */
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Student> datasource = env.addSource(new MysqlDataSource());
        // map操作，输入一条记录，根据规则输出一条记录
        datasource.map(new MapFunction<Student, Student>() {
            @Override
            public Student map(Student student) throws Exception {
                return student;
            }
        });
        //flatmap操作，输入一条记录，根据规则生成一条或多条记录
        datasource.flatMap(new FlatMapFunction<Student, Student>() {
            @Override
            public void flatMap(Student student, Collector<Student> collector) throws Exception {
                collector.collect(student);
            }
        });
        // filter操作，根据规则过滤数据流，只输出满足条件的记录
        datasource.filter(new FilterFunction<Student>() {
            @Override
            public boolean filter(Student student) throws Exception {
                return true;
            }
        });

        // keyby操作，基于key对数据流进行逻辑分区，相同的key被分到同一个分区
        KeyedStream<Student, Integer> keyedStream = datasource.keyBy(new KeySelector<Student, Integer>() {
            @Override
            public Integer getKey(Student student) throws Exception {
                return student.getAge();
            }
        });
        //在流上的每一个元素出现时触发一次reduce操作，相当于累加
        keyedStream.reduce(new RichReduceFunction<Student>() {
            @Override
            public Student reduce(Student t, Student t1) throws Exception {
                return new Student(t.getId() + t1.getId(), t.getName() + t1.getName(),
                        t.getPassword() + t1.getPassword(), t.getAge() + t1.getAge());
            }
        });
        // fold操作，累加操作，给定初始值，按照规则累加，返回新的记录的类型和初始值的类型相同
        keyedStream.fold("", new FoldFunction<Student, String>() {
            @Override
            public String fold(String s, Student o) throws Exception {
                return s + o.getName();
            }
        });

        // max 和 maxBy 之间的区别在于max返回流中的最大值，但maxBy返回具有最大值的键，min 和 minBy 同理。
        // 需要结合keyby和window使用
        keyedStream.window(TumblingProcessingTimeWindows.of(Time.milliseconds(50))).max("id");
        keyedStream.maxBy("age");

        // union操作，将两个或多个数据流结合在一起。 这样就可以并行地组合数据流。
        // 如果我们将一个流与自身组合，那么它会输出每个记录两次。
        datasource.union(datasource);

        // join操作，将两个数据流在同一个窗口内通过key join在一起
        datasource.join(datasource).where(new KeySelector<Student, Integer>() {
            @Override
            public Integer getKey(Student student) throws Exception {
                return student.getId();
            }
        }).equalTo(new KeySelector<Student, Integer>() {
            @Override
            public Integer getKey(Student student) throws Exception {
                return student.getId();
            }
            }).window(TumblingProcessingTimeWindows.of(Time.milliseconds(10))).apply(
                    new JoinFunction<Student, Student, Student>() {
                    @Override
                    public Student join(Student student, Student student2) throws Exception {
                        return new Student(student.getId(),
                                student.getName() + student2.getName(),
                                student.getPassword() + student2.getPassword(),
                                student.getAge() + student2.getAge());
                    }
                });

        // split操作，拆分流。根据条件将流拆分为多个
        SplitStream<Student> splitStream = datasource.split(new OutputSelector<Student>() {
            @Override
            public Iterable<String> select(Student student) {
                List<String> output = new ArrayList<String>();
                if (student.getId() % 2 == 0) {
                    output.add("even");
                } else {
                    output.add("odd");
                }
                return output;
            }
        });
        // select操作。选择拆分的流
        splitStream.select("even").print();

        //DataStream<Tuple4<Integer, Double, String, String>> in = // [...]
        //DataStream<Tuple2<String, String>> out = in.project(3,2);
        //(1,10.0,A,B)=> (B,A)
        //(2,20.0,C,D)=> (D,C)

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
