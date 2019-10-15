package com.liu.flink.streaming.datasource;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 自定义flink datasource.
 * @Auther: liudongfei
 * @Date: 2019/7/5 22:15
 * @Description:
 */
public class MysqlDataSource extends RichSourceFunction<Student> {
    private Connection conn;
    private PreparedStatement prep;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.conn = getConnection();
        this.prep = conn.prepareStatement("select * from student");
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
        if (prep != null) {
            prep.close();
        }
    }

    @Override
    public void run(SourceContext<Student> sourceContext) throws Exception {
        ResultSet resultSet = prep.executeQuery();
        while (resultSet.next()) {
            Student student = new Student(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("password"),
                    resultSet.getInt("age")
            );
            sourceContext.collect(student);
        }

    }

    @Override
    public void cancel() {
    }

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Student> datasource = env.addSource(new MysqlDataSource());
        datasource.print();
        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
