package com.liu.logmsgcommon.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * logmsg bean.
 * @Auther: liudongfei
 * @Date: 2019/4/12 09:40
 * @Description:
 */
public class LogMsgParserResult implements Serializable {

    private boolean isSucceed = false;
    private boolean isRequest = false;
    private KafkaEventBean sourceMsgBean;
    private String errMessage;
    private int errField;
    private long byteLength;
    private String transType;
    private String bitMap;
    private String traceUrl;
    private Map<Integer, String> headerMap;
    private Map<Integer, String> bodyMap;
    private Map<Integer, String> warnMsgMap = new HashMap<Integer, String>();
    private Map<Integer, String> errorMsgMap = new HashMap<Integer, String>();

    public KafkaEventBean getSourceMsgBean() {
        return sourceMsgBean;
    }

    public void setSourceMsgBean(KafkaEventBean sourceMsgBean) {
        this.sourceMsgBean = sourceMsgBean;
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public void setSucceed(boolean succeed) {
        isSucceed = succeed;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public int getErrField() {
        return errField;
    }

    public long getByteLength() {
        return byteLength;
    }

    public void setByteLength(long byteLength) {
        this.byteLength = byteLength;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getBitMap() {
        return bitMap;
    }

    public void setBitMap(String bitMap) {
        this.bitMap = bitMap;
    }

    public String getTraceUrl() {
        return traceUrl;
    }

    public void setTraceUrl(String traceUrl) {
        this.traceUrl = traceUrl;
    }

    public void setErrField(int errField) {
        this.errField = errField;
    }

    public Map<Integer, String> getWarnMsgMap() {
        return warnMsgMap;
    }

    public void setWarnMsgMap(Map<Integer, String> warnMsgMap) {
        this.warnMsgMap = warnMsgMap;
    }

    public Map<Integer, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(Map<Integer, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }

    @Override
    public String toString() {
        return "LogMsgParserResult{"
                + "isSucceed=" + isSucceed
                + ", isRequest=" + isRequest
                + ", sourceMsgBean=" + sourceMsgBean
                + ", errMessage='" + errMessage + '\''
                + ", errField=" + errField
                + ", byteLength=" + byteLength
                + ", transType='" + transType + '\''
                + ", bitMap='" + bitMap + '\''
                + ", traceUrl='" + traceUrl + '\''
                + ", headerMap=" + headerMap
                + ", bodyMap=" + bodyMap
                + ", warnMsgMap=" + warnMsgMap
                + ", errorMsgMap=" + errorMsgMap
                + '}';
    }

    /**
     * set header domain value.
     * @param domainId domainId
     * @param domainValue domainValue
     */
    public void setHeaderDomainValue(Integer domainId, String domainValue) {
        if (headerMap == null) {
            headerMap = new HashMap<>();
        }
        headerMap.put(domainId, domainValue);
    }

    /**
     * get header domain map.
     * @return
     */
    public Map<Integer, String> getHeaderMap() {
        if (headerMap == null) {
            return new HashMap<>();
        }
        return headerMap;
    }

    /**
     * get header domain value by domainId.
     * @param domainId domainId
     * @return
     */
    public String getHeaderDomainValue(Integer domainId) {
        if (headerMap == null) {
            return null;
        }
        return headerMap.get(domainId);
    }

    /**
     * set body domain value.
     * @param domainId domainId
     * @param domainValue domainValue
     */
    public void setBodyDomainValue(Integer domainId, String domainValue) {
        if (bodyMap == null) {
            bodyMap = new HashMap<>();
        }
        bodyMap.put(domainId, domainValue);
    }

    /**
     * get body map.
     * @return
     */
    public Map<Integer, String> getBodyMap() {
        if (bodyMap == null) {
            return new HashMap<>();
        }
        return bodyMap;
    }

    /**
     * get body domain value.
     * @param domainId domainId
     * @return
     */
    public String getBodyDomainValue(Integer domainId) {
        if (bodyMap == null) {
            return null;
        }
        return bodyMap.get(domainId);
    }

    /**
     * test domian is exist.
     * @param domainId domainId
     * @return
     */
    public boolean isDomainExist(int domainId) {
        if (domainId >= 0 && domainId <= this.getBitMap().length()) {
            return this.getBitMap().charAt(domainId - 1) == '1';
        } else {
            return false;
        }
    }
}
