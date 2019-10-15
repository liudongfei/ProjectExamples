package com.liu.java.web.jdk.tcp.upgrade;

/**
 * 业务对象操作接口.
 * @Auther: liudongfei
 * @Date: 2019/3/11 13:35
 * @Description:
 */
public interface ActionServiceListener {
    void doAction(Object object, MyUpgradeClient myClient);
    Object doAction(Object object, MyUpgradeServer myServer);
}
