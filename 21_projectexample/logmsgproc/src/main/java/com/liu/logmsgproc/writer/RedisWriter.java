package com.liu.logmsgproc.writer;

import com.liu.logmsgcommon.bean.LogMsgRelateResult;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 * flink write to redis.
 * @Auther: liudongfei
 * @Date: 2019/4/23 10:00
 * @Description:
 */
public class RedisWriter extends RichSinkFunction<LogMsgRelateResult> {
    private static final Logger logger = LoggerFactory.getLogger(RedisWriter.class);

    private static Jedis jedis;

    @Override
    public void open(Configuration parameters) throws Exception {
        ExecutionConfig.GlobalJobParameters globalJobParameters =
                getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        Map<String, String> globalParamMap = globalJobParameters.toMap();
        String host = globalParamMap.get("redis.host");
        int port = Integer.valueOf(globalParamMap.get("redis.port"));
        jedis = new Jedis(host, port);
        jedis.select(1);
    }

    @Override
    public void close() throws Exception {
        jedis.close();
    }

    @Override
    public void invoke(LogMsgRelateResult logMsgRelateResult) throws Exception {
        String transLinkedId = logMsgRelateResult.getTransLinkedId();
        String logMsgTransType = logMsgRelateResult.getLogMsgTransType();
        String logMsgTransOrg = logMsgRelateResult.getLogMsgTransOrg();
        String arriveTime = logMsgRelateResult.getReqLogMsgParserResult().getSourceMsgBean().getArriveTime();
        String key = transLinkedId + "_" + logMsgTransType + "_" + logMsgTransOrg;
        String val = jedis.get(key);
        if (val != null) {
            if (Long.valueOf(arriveTime) < Long.valueOf(val)) {
                jedis.set(key, arriveTime);
            }
        } else {
            jedis.set(key, arriveTime);
        }
    }
}
