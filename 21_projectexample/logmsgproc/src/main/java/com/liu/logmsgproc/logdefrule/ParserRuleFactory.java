package com.liu.logmsgproc.logdefrule;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgInterface;
import com.liu.logmsgcommon.zookeeper.SignalListener;
import com.liu.logmsgproc.bean.LogDomainBean;
import com.liu.es.util.MysqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * parser rule factory, to get parser rule.
 * @Auther: liudongfei
 * @Date: 2019/4/5 14:22
 * @Description:
 */
public class ParserRuleFactory implements SignalListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParserRuleFactory.class);
    private static final ConcurrentMap<LogMsgInterface, List<LogDomainBean>> logDefRuleMap = new ConcurrentHashMap<>();
    private static ParserRuleFactory instance;
    private String dbUrl;
    private String dbUserName;
    private String dbPassword;
    private LogMsgInterface[] ruleNames = new LogMsgInterface[]{LogMsgInterface.POST_ISO8583};
    private static volatile boolean initialized = false;

    /**
     * get instance.
     * @param dbUrl dbUrl
     * @param dbName dbName
     * @param dbPassWord dbPassWord
     * @return
     */
    public static ParserRuleFactory getInstance(String dbUrl, String dbName, String dbPassWord) throws SQLException {
        synchronized (ParserRuleFactory.class) {
            if (instance == null) {
                ParserRuleFactory factory = new ParserRuleFactory();
                factory.init(dbUrl, dbName, dbPassWord);
                instance = factory;
                initialized = true;
            }
        }
        return instance;
    }

    private void init(String dbUrl, String dbName, String dbPassword) throws SQLException {
        this.dbUrl = dbUrl;
        this.dbUserName = dbName;
        this.dbPassword = dbPassword;
        Connection conn = MysqlUtil.getConnection(dbUrl, dbName, dbPassword);
        PreparedStatement state = conn.prepareStatement("select * from mydb.log_rule where rule_interface=?");
        for (LogMsgInterface ruleName : ruleNames) {
            List<LogDomainBean> logDomainBeanList = new ArrayList<>();
            state.setString(1, ruleName.toString());
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                LogDomainBean logDomainBean = new LogDomainBean();
                logDomainBean.setSequence(rs.getInt("squence"));
                logDomainBean.setDomainId(rs.getInt("domain_id"));
                logDomainBean.setDomainName(rs.getString("domain_name"));
                logDomainBean.setDataType(rs.getString("domain_datatype"));
                logDomainBean.setFixedLength(rs.getInt("is_fixed_length") == 0 ? false : true);
                logDomainBean.setLength(rs.getInt("domain_length"));
                logDomainBean.setShouldSubstr(rs.getInt("should_substr") == 0 ? false : true);
                logDomainBean.setActualLength(rs.getInt("domain_actual_length"));
                logDomainBean.setContentFormat(rs.getString("content_format"));
                logDomainBean.setHeaderFormat(rs.getString("header_format"));
                logDomainBean.setOptional(rs.getInt("is_optional") == 0 ? false : true);
                logDomainBean.setExpectedString(rs.getString("expected_value"));
                logDomainBean.setExpectedStringStartWidth(
                        rs.getInt("expected_value_startwith") == 0 ? false : true);
                logDomainBeanList.add(logDomainBean);
            }
            logDefRuleMap.put(ruleName, logDomainBeanList);
        }
        MysqlUtil.close(conn);
    }

    /**
     * get parser rule.
     * @param kafkaEventBean kafkaEventBean
     * @return
     */
    public static List<LogDomainBean> getParserRule(KafkaEventBean kafkaEventBean) {
        LogMsgInterface logInterface = kafkaEventBean.getLogMsgInterface();
        return logDefRuleMap.get(logInterface);
    }

    @Override
    public void onSignal(String path, String value) throws SQLException {
        LOGGER.info("update parser rules from db!");
        init(dbUrl, dbUserName, dbPassword);
    }
}
