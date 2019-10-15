package com.liu.flink.streaming.datasource;

import org.apache.flink.api.common.io.FileInputFormat;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;

import java.io.IOException;

/**
 * 使用file作为datasource.
 * @Auther: liudongfei
 * @Date: 2019/7/5 16:34
 * @Description:
 */
public class FileDataSource {
    static class MyInputFormat extends FileInputFormat<String> {

        @Override
        public boolean reachedEnd() throws IOException {
            return false;
        }

        @Override
        public String nextRecord(String s) throws IOException {
            return null;
        }
    }
    /**
     * .
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String filePath = "hdfs:///user/flink/input/yarn-parallels-nodemanager-CDH.log";
        //DataStreamSource<String> datasource = env.readTextFile(filePath);
        DataStreamSource<String> datasource = env.readFile(
                new MyInputFormat(), // 自定义输入格式
                filePath, // 输入文件路径
                FileProcessingMode.PROCESS_CONTINUOUSLY, //文件处理模式，该模式下，当文件被修改时，其内容
                //将被重新处理，
                100);
        datasource.print();
        env.execute();
    }
}
