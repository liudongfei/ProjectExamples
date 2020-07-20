package com.liu.flink.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.CsvInputFormat;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.Path;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取HDFS上的文件,进行wordcount并写入HDFS.
 * @Auther: liudongfei
 * @Date: 2019/1/11 14:15
 * @Description:
 */
public class HdfsFileBatchProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HdfsFileBatchProcessor.class);

    /**
     * main.
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().disableSysoutLogging();
        LOGGER.info("reading hfds file ...");
        DataSource<String> dataSource = env.readTextFile("hdfs:///user/flink/input/yarn-parallels-nodemanager-CDH.log");
        FlatMapOperator<String, Tuple2<String, Integer>> tuple2FlatMapOperator = dataSource.flatMap(
                new FlatMapFunction<String, Tuple2<String, Integer>>() {
                @Override
                public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                    String[] words = s.split(" ");
                    for (String word : words) {
                        collector.collect(new Tuple2<>(word, 1));
                    }
                }
            });
        tuple2FlatMapOperator.groupBy(0).sum(1).print();
        //env.execute(); //批处理不用调用这个方法
    }
}
