package com.liu.logmsgproc;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgParserResult;
import com.liu.logmsgcommon.bean.LogMsgRelateResult;
import com.liu.logmsgcommon.util.ConstantUtil;
import com.liu.logmsgcommon.util.YamlConfigUtil;
import com.liu.logmsgproc.function.LogFilterAfterFunction;
import com.liu.logmsgproc.function.LogFilterBeforeFunction;
import com.liu.logmsgproc.function.LogFilterMidleFunction;
import com.liu.logmsgproc.function.LogParserFunction;
import com.liu.logmsgproc.function.LogRelateFunction;
import com.liu.logmsgproc.kafka.KafkaEventSchema;
import com.liu.logmsgproc.kafka.PausableKafkaConsumer;
import com.liu.logmsgproc.writer.HbaseWriter;
import com.liu.logmsgproc.writer.RedisWriter;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

/**
 * translog proc main.
 * @Auther: liudongfei
 * @Date: 2019/3/13 23:02
 * @Description:
 */
public class LogMsgProcMain {
    /**
     * translog parser main.
     * @param args args
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        //ParameterTool parameterTool = ParameterTool.fromArgs(args);
        Map configMap = YamlConfigUtil.findAndReadConfigFile("LogMsgProc.yaml", true);

        ParameterTool envParam = ParameterTool.fromMap(configMap);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(envParam);
        String topicId = envParam.get(ConstantUtil.TRANSLOG_CONSUME_TOPIC);
        Boolean hbaseEnable = (Boolean) configMap.get(ConstantUtil.HBASE_ENABLE);
        Boolean redisEnable = (Boolean) configMap.get(ConstantUtil.REDIS_ENABLE);
        Boolean consoleEnable = (Boolean) configMap.get(ConstantUtil.CONSOLE_ENABLE);
        //System.out.println(envParam.getProperties());
        PausableKafkaConsumer<KafkaEventBean> pausableKafkaConsumer =
                new PausableKafkaConsumer<>(topicId, new KafkaEventSchema(), envParam.getProperties());
        DataStreamSource<KafkaEventBean> kafkaEventDataStreamSource = env.addSource(pausableKafkaConsumer);

        SingleOutputStreamOperator<KafkaEventBean> logFilterBeforeStream = kafkaEventDataStreamSource
                .filter(new LogFilterBeforeFunction()).setParallelism(4).name("LogBeforeFilter");

        SingleOutputStreamOperator<LogMsgParserResult> logParserStream = logFilterBeforeStream
                .map(new LogParserFunction()).setParallelism(4).name("LogParser");

        SingleOutputStreamOperator<LogMsgParserResult> logFilterMidStream = logParserStream
                .filter(new LogFilterMidleFunction()).setParallelism(4).name("logParserFilter");

        SingleOutputStreamOperator<LogMsgRelateResult> logRelateStream = logFilterMidStream
                .map(new LogRelateFunction()).setParallelism(4).name("logRelate");

        SingleOutputStreamOperator<LogMsgRelateResult> logAfterFilterStream = logRelateStream
                .filter(new LogFilterAfterFunction()).setParallelism(4).name("logAfterFilter");


        if (hbaseEnable) {
            logAfterFilterStream.addSink(new HbaseWriter()).name("HbaseWriter");
        }
        if (redisEnable) {
            logAfterFilterStream.addSink(new RedisWriter()).name("RedisWriter");
        }
        if (consoleEnable) {
            logAfterFilterStream.print();
        }

        env.execute();
    }
}
