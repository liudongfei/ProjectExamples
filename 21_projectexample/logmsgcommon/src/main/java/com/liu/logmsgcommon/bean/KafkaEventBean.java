package com.liu.logmsgcommon.bean;

import java.util.Arrays;

/**
 * kafka message bin.
 * @Auther: liudongfei
 * @Date: 2019/4/3 14:04
 * @Description:
 */
public class KafkaEventBean {
    private String arriveTime;
    private String sourceIP;
    private String sourcePort;
    private String destinationIP;
    private String destinationPort;
    private LogMsgProtocol logMsgProtocol;
    private LogMsgInterface logMsgInterface;
    private LogMsgStruct logMsgStruct;
    private byte[] dataContent;

    public KafkaEventBean() {
    }

    /**
     * full constructor.
     * @param arriveTime arriveTime
     * @param sourceIP sourceIP
     * @param sourcePort sourcePort
     * @param destinationIP destinationIP
     * @param destinationPort destinationPort
     * @param logMsgProtocol logMsgProtocol
     * @param logMsgInterface logMsgInterface
     * @param logMsgStruct logMsgStruct
     * @param dataContent dataContent
     */
    public KafkaEventBean(
            String arriveTime,
            String sourceIP,
            String sourcePort,
            String destinationIP,
            String destinationPort,
            LogMsgProtocol logMsgProtocol,
            LogMsgInterface logMsgInterface,
            LogMsgStruct logMsgStruct,
            byte[] dataContent) {
        this.arriveTime = arriveTime;
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.logMsgProtocol = logMsgProtocol;
        this.logMsgInterface = logMsgInterface;
        this.logMsgStruct = logMsgStruct;
        this.dataContent = dataContent;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(String destinationIP) {
        this.destinationIP = destinationIP;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
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

    public byte[] getDataContent() {
        return dataContent;
    }

    public void setDataContent(byte[] dataContent) {
        this.dataContent = dataContent;
    }

    @Override
    public String toString() {
        return "KafkaEventBean{"
                + "arriveTime='" + arriveTime + '\''
                + ", sourceIP='" + sourceIP + '\''
                + ", sourcePort='" + sourcePort + '\''
                + ", destinationIP='" + destinationIP + '\''
                + ", destinationPort='" + destinationPort + '\''
                + ", logMsgProtocol=" + logMsgProtocol
                + ", logMsgInterface=" + logMsgInterface
                + ", logMsgStruct=" + logMsgStruct
                + ", dataContent=" + Arrays.toString(dataContent)
                + '}';
    }
}
