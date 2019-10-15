package com.liu.logmsgproc.function;

import com.liu.logmsgcommon.bean.LogMsgRelateResult;
import org.apache.flink.api.common.functions.RichFilterFunction;

/**
 * translog filter after relate function.
 * @Auther: liudongfei
 * @Date: 2019/4/19 16:26
 * @Description:
 */
public class LogFilterAfterFunction extends RichFilterFunction<LogMsgRelateResult> {
    @Override
    public boolean filter(LogMsgRelateResult logMsgRelateResult) throws Exception {
        if (logMsgRelateResult.isMatched()) {
            return true;
        }
        return false;
    }
}
