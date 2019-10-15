package com.liu.logmsgproc.parser;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgParserResult;
import com.liu.logmsgcommon.util.ByteUtil;
import com.liu.logmsgproc.bean.LogDomainBean;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * iso 8583 translog parser.
 * @Auther: liudongfei
 * @Date: 2019/4/19 14:36
 * @Description:
 */
public class ISO8583Parser implements LogParser {
    public static final Logger LOGGER = LoggerFactory.getLogger(ISO8583Parser.class);

    private static final List<String> requestTypes = new ArrayList<>();

    static {
        requestTypes.add("0800");
        requestTypes.add("0200");
    }

    /**
     * translog parser.
     * @param logDefRules logDefRules
     * @param kafkaEventBean kafkaEventBean
     * @param parseResult parseResult
     * @return
     */
    @Override
    public LogMsgParserResult parse(
            List<LogDomainBean> logDefRules, KafkaEventBean kafkaEventBean, LogMsgParserResult parseResult) {
        byte[]logMsg = kafkaEventBean.getDataContent();
        int logMsgPos = 0;
        LogDomainBean logDomainBean = null;
        parseResult.setSourceMsgBean(kafkaEventBean);
        parseResult.setSucceed(true);
        try {
            for (int i = 0; i < logDefRules.size(); i++) {
                logDomainBean = logDefRules.get(i);
                int domainSequence = logDomainBean.getSequence();
                int domainId = logDomainBean.getDomainId();
                int domainLength = logDomainBean.getLength();
                String domainName = logDomainBean.getDomainName();

                //如果是报文域，那么要看该域是否在位图中，如果不在则应该跳过,我们自定义sequence >= 100表示报文域， 从域2算起
                if (domainSequence >= 100 && !parseResult.isDomainExist(domainId)) {
                    continue;
                }
                if (ByteUtil.isOutOfBound(logMsg, logMsgPos, domainLength)) {
                    if (logDomainBean.isOptional()) { // 如果域可选跳过
                        continue;
                    } else {
                        parseResult.getErrorMsgMap().put(domainSequence,
                                String.format("log parse to %d(%s) indexoutofbound", domainSequence, domainName));
                        parseResult.setSucceed(false);
                        return parseResult;
                    }
                }
                //截取报文片段
                byte[] domainMsgBys = new byte[domainLength];
                System.arraycopy(logMsg, logMsgPos, domainMsgBys, 0, domainLength);

                if (domainName.equals("位图")) {
                    logMsgPos = logMsgPos + domainLength;
                    String bitmap = bitmapToDomains(domainMsgBys);
                    parseResult.setBitMap(bitmap);
                    String validateRes = validateBitmap(logDefRules, bitmap);
                    if (!StringUtils.isEmpty(validateRes)) {
                        LOGGER.warn("logdefrule not contains the domains:{} ,please check config",
                                validateRes.substring(0,validateRes.length() - 1));
                    }
                    setDomainValue(parseResult, domainSequence, bitmap);
                    continue;
                }
                if (domainName.equals("消息类型")) {
                    String sMessageType = ByteUtil.bytes2HexStr(domainMsgBys);
                    parseResult.setTransType(sMessageType);
                    if (requestTypes.contains(sMessageType)) {
                        parseResult.setRequest(true);
                    }
                    setDomainValue(parseResult, domainSequence, sMessageType);
                    logMsgPos = logMsgPos + domainLength;
                    continue;
                }
                String domainMsgHex = "";
                // 考虑如果是optional的原因可能不往前走， 需要保存
                boolean bFarward = true;
                int forwardLength = 0;
                if (!logDomainBean.isFixedLength()) {
                    // calc header length
                    String sHeadText = ByteUtil.bytes2HexStr(domainMsgBys);
                    int headerLength = Integer.parseInt(sHeadText);
                    int aculLength = headerLength;
                    if ("bcd".equals(logDomainBean.getContentFormat())) {
                        aculLength = headerLength % 2 == 0 ? headerLength / 2 : headerLength / 2 + 1;
                    }
                    logMsgPos = logMsgPos + domainLength;
                    byte[] domainContentBys = new byte[aculLength];
                    System.arraycopy(logMsg, logMsgPos, domainContentBys, 0, aculLength);
                    domainMsgHex = ByteUtil.bytes2HexStr(domainContentBys);
                    logMsgPos = logMsgPos - domainLength;
                    forwardLength = domainLength + aculLength;
                } else {
                    domainMsgHex = ByteUtil.bytes2HexStr(domainMsgBys);
                    forwardLength = domainLength;
                }
                //如果需要截取一些字符
                if (logDomainBean.isShouldSubstr() && domainLength < logDomainBean.getActualLength() ) {
                    domainMsgHex = domainMsgHex.substring(0, logDomainBean.getActualLength());
                }
                //判断是否为期望值， 如果不是期望值且为optional， 那么跳过， 如果不是期望值且不是optional, 那么本报文停止解析
                if (logDomainBean.getExpectedString() != null && !logDomainBean.getExpectedString().equals("")) {
                    boolean isExpectedString = false;
                    String [] splitters = logDomainBean.getExpectedString().split("\\|");
                    for (String splitter : splitters) {
                        splitter = splitter.trim();
                        if (logDomainBean.isExpectedStringStartWidth()) {
                            if (new String(domainMsgBys).startsWith(splitter)) {
                                isExpectedString = true;
                                break;
                            } else {
                                isExpectedString = false;
                            }
                        } else {
                            if (new String(domainMsgBys).equals(splitter)) {
                                isExpectedString = true;
                                break;
                            } else {
                                isExpectedString = false;
                            }
                        }
                    }
                    if (!isExpectedString) {
                        if (logDomainBean.isOptional()) {
                            parseResult.getWarnMsgMap().put(domainSequence,
                                    String.format("parse to %d(%s) value is%s，not expect value(%s)", domainSequence,
                                            domainName, domainMsgHex, logDomainBean.getExpectedString()));
                            bFarward = false;
                        } else {
                            parseResult.getErrorMsgMap().put(domainSequence,
                                    String.format("parse to %d(%s) value is%s， not expect value(%s)", domainSequence,
                                            domainName, domainMsgHex, logDomainBean.getExpectedString()));
                            continue;
                        }
                    } else {
                        bFarward = true;
                    }
                } else {
                    bFarward = true;
                }
                if (bFarward) {
                    logMsgPos = logMsgPos + forwardLength;
                    setDomainValue(parseResult, domainSequence, domainMsgHex);
                }
            }
        } catch (Exception ex) {
            parseResult.setSucceed(false);
            if (logDomainBean != null) {
                StringBuffer exSb = new StringBuffer();
                for (StackTraceElement st: ex.getStackTrace()) {
                    exSb.append(st.toString()).append(System.getProperty( "line.separator"));
                }
                if (logDomainBean.getSequence() < 11) {
                    parseResult.setErrField(-1);
                    parseResult.getErrorMsgMap().put(logDomainBean.getSequence(),
                            String.format("parse to Head{}({}) occur error: {}",
                                    logDomainBean.getSequence(), logDomainBean.getDomainName(), exSb.toString()));
                } else {
                    parseResult.setErrField(logDomainBean.getDomainId());
                    parseResult.getErrorMsgMap().put(logDomainBean.getDomainId(),
                            String.format("parse to F{}({}) occur error: {}",
                                    logDomainBean.getDomainId(), logDomainBean.getDomainName(), exSb.toString()));
                }
            }
        } finally {
            if (logMsgPos < logMsg.length) {
                parseResult.setSucceed(false);
                LOGGER.error("this log parse not reach the last char,please check:current position{},log length{}。",
                        logMsgPos, logMsg.length);
            } else if (logMsgPos > logMsg.length) {
                parseResult.setSucceed(false);
                LOGGER.error("this log parse over the last char,please check:current position{},log length{}",
                        logMsgPos, logMsg.length);
            }
        }
        return parseResult;
    }

    private void setDomainValue(LogMsgParserResult parseResult, int domainSequence, String domainValue) {
        if (domainSequence >= 100) {
            parseResult.setBodyDomainValue(domainSequence, domainValue);
        } else {
            parseResult.setHeaderDomainValue(domainSequence, domainValue);
        }
    }

    /**
     * get bitmap binary string.
     * @param hexBitmap hexBitmap
     * @return
     */
    public static String bitmapToDomains(byte[] hexBitmap) {
        StringBuffer bf = new StringBuffer();
        if (hexBitmap == null) {
            return bf.toString();
        }
        for (int i = 0; i < hexBitmap.length; i++) {
            String binary = StringUtils.leftPad(Integer.toBinaryString(hexBitmap[i] & 0xff), 8, '0');
            bf.append(binary);
        }
        return bf.toString();
    }

    /**
     * validate logdefrule is or not correct.
     * @param logDefRule logDefRule
     * @param bitmap bitmap
     * @return
     */
    public static String validateBitmap(List<LogDomainBean> logDefRule, String bitmap) {
        boolean[] arr = new boolean[129];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = false;
        }
        for (LogDomainBean transLogDefItem : logDefRule) {
            if (transLogDefItem.getDomainId() >= 0) {
                arr[transLogDefItem.getDomainId()] = true;
            }
        }
        String ret = "";
        for (int i = 0; i < bitmap.length(); i++) {
            if (bitmap.charAt(i) == '1' && !arr[i + 1]) {
                ret += "" + i + ",";
            }
        }
        return ret;
    }


}
