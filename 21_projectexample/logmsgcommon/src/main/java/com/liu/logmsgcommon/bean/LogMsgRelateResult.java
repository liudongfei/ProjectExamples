package com.liu.logmsgcommon.bean;

import java.io.Serializable;

/**
 * trans log msg bean.
 * @Auther: liudongfei
 * @Date: 2019/4/11 15:32
 * @Description:
 */
public class LogMsgRelateResult implements Serializable {

    private String transLinkedId;
    private String transMatchId;
    private boolean isMatched = false;
    private String logMsgTransType;
    private String logMsgTransOrg;
    private LogMsgProtocol logMsgProtocol;
    private LogMsgInterface logMsgInterface;
    private LogMsgStruct logMsgStruct;
    private LogMsgParserResult reqLogMsgParserResult;
    private LogMsgParserResult resLogMsgParserResult;
    private long responseDelayMiliSec;

    public String getTransLinkedId() {
        return transLinkedId;
    }

    public void setTransLinkedId(String transLinkedId) {
        this.transLinkedId = transLinkedId;
    }

    public String getTransMatchId() {
        return transMatchId;
    }

    public void setTransMatchId(String transMatchId) {
        this.transMatchId = transMatchId;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public String getLogMsgTransType() {
        return logMsgTransType;
    }

    public void setLogMsgTransType(String logMsgTransType) {
        this.logMsgTransType = logMsgTransType;
    }

    public String getLogMsgTransOrg() {
        return logMsgTransOrg;
    }

    public void setLogMsgTransOrg(String logMsgTransOrg) {
        this.logMsgTransOrg = logMsgTransOrg;
    }

    public LogMsgProtocol getLogMsgProtocol() {
        return logMsgProtocol;
    }

    public void setLogMsgProtocol(LogMsgProtocol logMsgProtocol) {
        this.logMsgProtocol = logMsgProtocol;
    }

    public LogMsgInterface getLogMsgInterface() {
        return logMsgInterface;
    }

    public void setLogMsgInterface(LogMsgInterface logMsgInterface) {
        this.logMsgInterface = logMsgInterface;
    }

    public LogMsgStruct getLogMsgStruct() {
        return logMsgStruct;
    }

    public void setLogMsgStruct(LogMsgStruct logMsgStruct) {
        this.logMsgStruct = logMsgStruct;
    }

    public LogMsgParserResult getReqLogMsgParserResult() {
        return reqLogMsgParserResult;
    }

    public void setReqLogMsgParserResult(LogMsgParserResult reqLogMsgParserResult) {
        this.reqLogMsgParserResult = reqLogMsgParserResult;
    }

    public LogMsgParserResult getResLogMsgParserResult() {
        return resLogMsgParserResult;
    }

    public void setResLogMsgParserResult(LogMsgParserResult resLogMsgParserResult) {
        this.resLogMsgParserResult = resLogMsgParserResult;
    }

    public long getResponseDelayMiliSec() {
        return responseDelayMiliSec;
    }

    public void setResponseDelayMiliSec(long responseDelayMiliSec) {
        this.responseDelayMiliSec = responseDelayMiliSec;
    }

    @Override
    public String toString() {
        return "LogMsgRelateResult{"
                + "transLinkedId='" + transLinkedId + '\''
                + ", transMatchId='" + transMatchId + '\''
                + ", isMatched=" + isMatched
                + ", logMsgTransType='" + logMsgTransType + '\''
                + ", logMsgTransOrg='" + logMsgTransOrg + '\''
                + ", logMsgProtocol=" + logMsgProtocol
                + ", logMsgInterface=" + logMsgInterface
                + ", logMsgStruct=" + logMsgStruct
                + ", reqLogMsgParserResult=" + reqLogMsgParserResult
                + ", resLogMsgParserResult=" + resLogMsgParserResult
                + ", responseDelayMiliSec=" + responseDelayMiliSec
                + '}';
    }
}
