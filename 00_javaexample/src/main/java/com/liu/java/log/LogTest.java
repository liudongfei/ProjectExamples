package com.liu.java.log;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * log4j2的日志测试类.
 * @Auther: liudongfei
 * @Date: 2019/3/20 16:48
 * @Description:
 */
public class LogTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogTest.class);

    /**
     * prelog模块日志采集.
     */
    public void preLog() {
        ThreadContext.put("module", "pre");
        LOGGER.info("this is pre info level log");
        LOGGER.warn("this is pre warn level log");
        LOGGER.error("this is pre error level log");
    }

    /**
     * statlog模块日志采集.
     */
    public void statLog() {
        ThreadContext.put("module", "stat");
        LOGGER.info("this is stat info level log");
        LOGGER.warn("this is stat warn level log");
        LOGGER.error("this is stat error level log");
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        LogTest logTest = new LogTest();
        for (int i = 0; i < 10; i++) {
            logTest.preLog();
            logTest.statLog();
        }
    }
}
