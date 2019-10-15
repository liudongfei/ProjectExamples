package com.liu.logmsgproc.function;

import com.liu.logmsgcommon.bean.LogMsgParserResult;
import org.apache.flink.api.common.functions.RichFilterFunction;

/**
 * translog filter after parser function.
 * @Auther: liudongfei
 * @Date: 2019/4/22 16:55
 * @Description:
 */
public class LogFilterMidleFunction extends RichFilterFunction<LogMsgParserResult> {
    @Override
    public boolean filter(LogMsgParserResult logMsgParserResult) throws Exception {
        return logMsgParserResult.isSucceed();
    }
}
