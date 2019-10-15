package com.liu.logmsgproc.function;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgParserResult;
import com.liu.logmsgcommon.zookeeper.ZKStateSignalService;
import com.liu.logmsgproc.bean.LogDomainBean;
import com.liu.logmsgproc.logdefrule.ParserRuleFactory;
import com.liu.logmsgproc.parser.LogParser;
import com.liu.logmsgproc.parser.ParserFactory;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * flink map function to parser translog.
 * @Auther: liudongfei
 * @Date: 2019/4/4 09:32
 * @Description:
 */
public class LogParserFunction extends RichMapFunction<KafkaEventBean, LogMsgParserResult> {
    private static final Logger logger = LoggerFactory.getLogger(LogParserFunction.class);
    private final String watchPath = "/parser_rule";
    private volatile ParserRuleFactory parserRuleSignalListener = null;
    private ZKStateSignalService zkStateSignalService = null;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        ExecutionConfig.GlobalJobParameters globalJobParameters =
                getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        Map<String, String> globalJobParametersMap = globalJobParameters.toMap();
        if (watchPath == null) {
            logger.error("fail to open pausable parser state signal service, no watch path was found!");
            return;
        }
        String zkAddress = globalJobParametersMap.get("zookeeper.address");
        String watchPrex = globalJobParametersMap.get("zookeeper.rootpath");
        String dbUrl = globalJobParametersMap.get("db.url");
        String dbName = globalJobParametersMap.get("db.username");
        String dbPassWord = globalJobParametersMap.get("db.password");

        zkStateSignalService = zkStateSignalService.getInstance(zkAddress, watchPrex);
        logger.info("successfully opened parser state signal service {}", watchPath);
        parserRuleSignalListener = ParserRuleFactory.getInstance(dbUrl, dbName, dbPassWord);
        logger.info("start registering pausable parser state signal listener to path: {}", watchPath);
        zkStateSignalService.registerSignalListener(watchPath, parserRuleSignalListener);
    }

    @Override
    public LogMsgParserResult map(KafkaEventBean kafkaEventBean) throws Exception {
        LogParser parser = ParserFactory.getParser(kafkaEventBean);
        List<LogDomainBean> parserRule = ParserRuleFactory.getParserRule(kafkaEventBean);

        LogMsgParserResult logMsgParserResult = new LogMsgParserResult();
        parser.parse(parserRule, kafkaEventBean, logMsgParserResult);

        logger.info("header:\t" + logMsgParserResult.getHeaderMap());
        logger.info("body:\t" + logMsgParserResult.getBodyMap());
        return logMsgParserResult;
    }

    @Override
    public void close() throws Exception {
        super.close();
        unregisterSignal();
    }

    private void unregisterSignal() {
        if (zkStateSignalService != null) {
            String fullPath = "/" + watchPath;
            logger.info("unregistering pausable parser state signal listener to path: {}", fullPath);
            zkStateSignalService.unregisterSignalListener(fullPath, parserRuleSignalListener);
        }

    }
}
