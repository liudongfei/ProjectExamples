package com.liu.flink.streaming.sink;

import com.alibaba.fastjson.JSON;
import com.liu.flink.streaming.datasource.Student;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * sink到mysql.
 * @Auther: liudongfei
 * @Date: 2019/7/6 12:48
 * @Description:
 */
public class MysqlSink extends RichSinkFunction<Student> {
    private Connection conn;
    private PreparedStatement preparedStatement;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.conn = getConnection();
        this.preparedStatement = conn.prepareStatement(
                "insert into student(id, name, password, age) values(?, ?, ?, ?);");
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://mincdh:3306/mydb", "root", "123456");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (conn != null) {
            conn.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
    }

    @Override
    public void invoke(Student student) throws Exception {
        preparedStatement.setInt(1, student.getId());
        preparedStatement.setString(2, student.getName());
        preparedStatement.setString(3, student.getPassword());
        preparedStatement.setInt(4, student.getAge());
        preparedStatement.executeUpdate();
    }

    public static void main(String[] args) {
        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "mincdh:9092");
        prop.setProperty("fetch.min.bytes", "2048");
        prop.setProperty("max.poll.records", "10000");
        prop.setProperty("max.poll.interval.ms", "500");
        prop.setProperty("group.id", "myconsumer");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkKafkaConsumer010<String> source = new FlinkKafkaConsumer010<>("mytopic", new SimpleStringSchema(), prop);
        DataStreamSource<String> datasource = env.addSource(source);
        SingleOutputStreamOperator<Student> studentOperator = datasource.map(new RichMapFunction<String, Student>() {
            @Override
            public Student map(String s) throws Exception {
                return JSON.parseObject(s, Student.class);
            }
        });
        studentOperator.addSink(new MysqlSink());
        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
