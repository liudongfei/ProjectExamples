package com.liu.java.web.jdk.tcp.upgrade;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 保持TCP连接存活的心跳包.
 * @Auther: liudongfei
 * @Date: 2019/3/11 11:30
 * @Description:
 */
public class HeartbeatBean implements Serializable {
    @Override
    public String toString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "HeartbeatBean{}";
    }
}
