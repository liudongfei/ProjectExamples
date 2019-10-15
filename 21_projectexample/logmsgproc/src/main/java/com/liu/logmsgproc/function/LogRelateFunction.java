package com.liu.logmsgproc.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liu.logmsgcommon.bean.LogMsgParserResult;
import com.liu.logmsgcommon.bean.LogMsgRelateResult;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Map;

/**
 * tranlog relate function.
 * @Auther: liudongfei
 * @Date: 2019/4/19 16:11
 * @Description:
 */
public class LogRelateFunction extends RichMapFunction<LogMsgParserResult, LogMsgRelateResult> {
    private static final Logger logger = LoggerFactory.getLogger(LogRelateFunction.class);
    private static Jedis jedis;
    private static ObjectMapper objectMapper;

    @Override
    public void open(Configuration parameters) throws Exception {
        ExecutionConfig.GlobalJobParameters globalJobParameters =
                getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        Map<String, String> globalParamMap = globalJobParameters.toMap();
        String host = globalParamMap.get("redis.host");
        int port = Integer.valueOf(globalParamMap.get("redis.port"));
        jedis = new Jedis(host, port);
        objectMapper = new ObjectMapper();
        // get connection
    }

    @Override
    public void close() throws Exception {
        jedis.close();
        // close connetion
    }

    @Override
    public LogMsgRelateResult map(LogMsgParserResult logMsgParserResult) throws Exception {
        LogMsgRelateResult logMsgRelateResult = new LogMsgRelateResult();
        String matchFactor = getReqAndResMatchFactor(logMsgParserResult);
        LogMsgParserResult matchedLogMsgParserResult = null;
        if (matchFactor != null) {
            matchedLogMsgParserResult = tryMatchByMatchFactor(matchFactor);
        }
        if (matchedLogMsgParserResult == null && logMsgParserResult != null && matchFactor != null) {
            putLog2CachePool(matchFactor, logMsgParserResult);
            return logMsgRelateResult;
        }
        logMsgRelateResult.setTransLinkedId(logMsgParserResult.getHeaderDomainValue(2)); // TODO
        logMsgRelateResult.setTransMatchId(matchFactor); // TODO
        logMsgRelateResult.setLogMsgInterface(logMsgParserResult.getSourceMsgBean().getLogMsgInterface()); // TODO
        logMsgRelateResult.setLogMsgProtocol(logMsgParserResult.getSourceMsgBean().getLogMsgProtocol()); // TODO
        logMsgRelateResult.setLogMsgStruct(logMsgParserResult.getSourceMsgBean().getLogMsgStruct()); // TODO
        logMsgRelateResult.setLogMsgTransOrg("*"); // TODO
        String transTypes = "";
        if (logMsgParserResult.isRequest() && !matchedLogMsgParserResult.isRequest()) {
            logMsgRelateResult.setReqLogMsgParserResult(logMsgParserResult);
            logMsgRelateResult.setResLogMsgParserResult(matchedLogMsgParserResult);
            transTypes = logMsgParserResult.getTransType() + "/" + matchedLogMsgParserResult.getTransType();
            logMsgRelateResult.setMatched(true);
        } else if (!logMsgParserResult.isRequest() && matchedLogMsgParserResult.isRequest()) {
            logMsgRelateResult.setReqLogMsgParserResult(matchedLogMsgParserResult);
            logMsgRelateResult.setResLogMsgParserResult(logMsgParserResult);
            transTypes = matchedLogMsgParserResult.getTransType() + "/" + logMsgParserResult.getTransType();
            logMsgRelateResult.setMatched(true);
        }
        logMsgRelateResult.setLogMsgTransType(transTypes); // TODO
        logger.info(logMsgParserResult.toString());
        return logMsgRelateResult;
    }

    private void putLog2CachePool(String key, LogMsgParserResult logMsgParserResult) throws JsonProcessingException {
        String value = objectMapper.writeValueAsString(logMsgParserResult);
        jedis.set(key, value);
        // TODO
    }

    private LogMsgParserResult tryMatchByMatchFactor(String matchFactor) throws IOException {
        // TODO
        String value = jedis.get(matchFactor);
        if (value == null) {
            return null;
        }
        LogMsgParserResult logMsgParserResult = objectMapper.readValue(value, LogMsgParserResult.class);
        return logMsgParserResult;
    }

    private String getReqAndResMatchFactor(LogMsgParserResult logMsgParserResult) {
        return logMsgParserResult.getBodyDomainValue(103);
        // TODO
    }
}
