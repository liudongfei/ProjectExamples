package com.liu.logmsgproc.function;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgInterface;
import org.apache.flink.api.common.functions.RichFilterFunction;

/**
 * translog filter before paser function.
 * @Auther: liudongfei
 * @Date: 2019/4/19 14:05
 * @Description:
 */
public class LogFilterBeforeFunction extends RichFilterFunction<KafkaEventBean> {
    @Override
    public boolean filter(KafkaEventBean kafkaEventBean) throws Exception {
        if (kafkaEventBean.getDataContent().length < 4) {
            return false;
        }
        if (kafkaEventBean.getLogMsgInterface().equals(LogMsgInterface.TSM_HTTPJSON)) {
            return true;
        }
        return false;
    }
}
