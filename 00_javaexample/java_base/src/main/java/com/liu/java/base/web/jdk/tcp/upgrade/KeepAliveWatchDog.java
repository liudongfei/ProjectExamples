package com.liu.java.base.web.jdk.tcp.upgrade;

import java.io.IOException;


/**
 * 保持TCP长链接发送心跳包的线程.
 * @Auther: liudongfei
 * @Date: 2019/3/11 13:38
 * @Description:
 */
public class KeepAliveWatchDog implements Runnable {
    private MyUpgradeClient myClient;

    public KeepAliveWatchDog(MyUpgradeClient myClient) {
        this.myClient = myClient;
    }

    /**
     * run.
     */
    public void run() {
        while (myClient.isRunning()) {
            if (System.currentTimeMillis() - myClient.getLastSendTime() > 2000) {
                try {
                    myClient.sendObject(new HeartbeatBean());
                    myClient.setLastSendTime(System.currentTimeMillis());
                } catch (IOException e) {
                    e.printStackTrace();
                    myClient.stop();
                }
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    myClient.stop();
                }
            }
        }
    }
}
