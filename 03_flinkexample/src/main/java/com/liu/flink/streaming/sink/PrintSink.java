package com.liu.flink.streaming.sink;

import com.liu.flink.streaming.datasource.MysqlDataSource;
import com.liu.flink.streaming.datasource.Student;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;

/**
 * 输出到打印sink.
 * @Auther: liudongfei
 * @Date: 2019/7/5 23:00
 * @Description:
 */
public class PrintSink {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Student> datasource = env.addSource(new MysqlDataSource());
        datasource.addSink(new PrintSinkFunction<>());
        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
