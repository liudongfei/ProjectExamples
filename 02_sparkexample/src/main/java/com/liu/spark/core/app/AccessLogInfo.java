package com.liu.spark.core.app;

import java.io.Serializable;

/**
 * 访问日志bean.
 */
public class AccessLogInfo implements Serializable {
    /* 上行流量 */
    private long uptraffic;
    /* 下行流量 */
    private long downtraffic;
    /* 时间戳 */
    private long timestamp;

    public AccessLogInfo() {
    }

    /**
     * 构造器.
     * @param uptraffic uptraffic
     * @param downtraffic downtraffic
     * @param timestamp timestamp
     */
    public AccessLogInfo(long uptraffic, long downtraffic, long timestamp) {
        this.uptraffic = uptraffic;
        this.downtraffic = downtraffic;
        this.timestamp = timestamp;
    }

    public long getUptraffic() {
        return uptraffic;
    }

    public void setUptraffic(long uptraffic) {
        this.uptraffic = uptraffic;
    }

    public long getDowntraffic() {
        return downtraffic;
    }

    public void setDowntraffic(long downtraffic) {
        this.downtraffic = downtraffic;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
