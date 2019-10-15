package com.liu.logmsgcommon.zookeeper;

/**
 * zookeeper signal listener interface.
 * @Auther: liudongfei
 * @Date: 2019/3/18 15:15
 * @Description:
 */
public interface SignalListener {
    void onSignal(String path, String value) throws Exception;
}
