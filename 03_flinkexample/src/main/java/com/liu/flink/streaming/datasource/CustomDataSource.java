package com.liu.flink.streaming.datasource;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * 自定义datasource.
 * @Auther: liudongfei
 * @Date: 2019/7/5 17:23
 * @Description:
 */
public class CustomDataSource {
    /**
     * .
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> datasource = env.addSource(new SourceFunction<String>() {

            private volatile boolean isRunning = true;

            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                while (isRunning) {
                    sourceContext.collect(String.valueOf(Math.floor(Math.random() * 100)));
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        });
        datasource.print();
        env.execute();
    }
}
