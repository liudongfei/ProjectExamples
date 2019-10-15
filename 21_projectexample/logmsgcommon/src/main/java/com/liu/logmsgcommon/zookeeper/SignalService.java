package com.liu.logmsgcommon.zookeeper;

/**
 * zookeeper signal service interface.
 * @Auther: liudongfei
 * @Date: 2019/3/18 15:14
 * @Description:
 */
public interface SignalService {
    boolean registerSignalListener(String path, SignalListener signalListener);
    boolean unregisterSignalListener(String path, SignalListener signalListener);
}
