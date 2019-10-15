package com.liu.flink.streaming;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 迭代流数据样例.
 * @Auther: liudongfei
 * @Date: 2018/12/13 14:03
 * @Description:
 */
public class IterativeStreaExample {
    /**
     * 迭代流数据样例.
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Long> someIntegers = env.generateSequence(0, 1000);

        IterativeStream<Long> iteration = someIntegers.iterate();

        DataStream<Long> minusOne = iteration.map(val -> val - 1);

        DataStream<Long> stillGreaterThanZero = minusOne.filter(val -> (val > 0));

        iteration.closeWith(stillGreaterThanZero).print();

        DataStream<Long> lessThanZero = minusOne.filter(val -> val <= 0);
        env.execute();
    }
}
