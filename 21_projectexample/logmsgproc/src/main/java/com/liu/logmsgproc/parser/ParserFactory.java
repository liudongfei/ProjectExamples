package com.liu.logmsgproc.parser;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgStruct;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * parser factory.
 * @Auther: liudongfei
 * @Date: 2019/4/4 23:34
 * @Description:
 */
public class ParserFactory {
    private static final ConcurrentMap<LogMsgStruct, LogParser> logParserMap = new ConcurrentHashMap<>();

    static {
        synchronized (ParserFactory.class) {
            logParserMap.put(LogMsgStruct.ISO8583, new ISO8583Parser());
            logParserMap.put(LogMsgStruct.JSON, new HttpJsonParser());
            logParserMap.put(LogMsgStruct.XML, new HttpXmlParser());
            // TODO add log parser
        }
    }

    public static LogParser getParser(KafkaEventBean kafkaEventBean) {
        LogMsgStruct logParser = kafkaEventBean.getLogMsgStruct();
        return logParserMap.get(logParser);
    }
}
