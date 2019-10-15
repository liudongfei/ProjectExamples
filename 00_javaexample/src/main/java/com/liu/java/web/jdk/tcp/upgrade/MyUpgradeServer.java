package com.liu.java.web.jdk.tcp.upgrade;

import java.util.concurrent.ConcurrentHashMap;

/**
 * TCP服务端主线程.
 * @Auther: liudongfei
 * @Date: 2019/3/11 13:39
 * @Description:
 */
public class MyUpgradeServer {
    private int serverPort = 0;
    private volatile boolean running = false;
    private ConcurrentHashMap<Class, ActionServiceListener> actionMapping = new ConcurrentHashMap<Class, ActionServiceListener>();

    public MyUpgradeServer(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * 启动服务.
     */
    public void start() {
        if (running) {
            return;
        }
        running = true;
        Thread connWatchDog = new Thread(new ConnWatchDog(this));
        connWatchDog.start();
    }

    /**
     * 停止服务.
     */
    public void stop() {
        if (running) {
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public ActionServiceListener getObjectAction(Class key) {
        return actionMapping.get(key);
    }

    public int getServerPort() {
        return serverPort;
    }

    public static void main(String[] args) {
        MyUpgradeServer myServer = new MyUpgradeServer(4701);
        myServer.start();
    }
}
