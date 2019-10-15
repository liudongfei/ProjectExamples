package com.liu.logmsgproc.parser;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgParserResult;
import com.liu.logmsgproc.bean.LogDomainBean;

import java.util.List;


/**
 * translog parser interface.
 * @Auther: liudongfei
 * @Date: 2019/4/4 09:42
 * @Description:
 */
public interface LogParser {
    public LogMsgParserResult parse(List<LogDomainBean> logDefRule, KafkaEventBean kafkaEventBean,
                                    LogMsgParserResult logMsgParserResult);
}
