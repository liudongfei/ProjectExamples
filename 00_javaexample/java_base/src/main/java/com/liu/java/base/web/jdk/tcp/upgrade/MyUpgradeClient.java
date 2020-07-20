package com.liu.java.base.web.jdk.tcp.upgrade;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TCP客户端主线程.
 * @Auther: liudongfei
 * @Date: 2019/3/11 13:36
 * @Description:
 */
public class MyUpgradeClient {
    private String serverIp;
    private int serverPort;
    private boolean running = false;
    /* 上次发送消息的时间 */
    private long lastSendTime = 0;
    private Socket socket;
    private ConcurrentHashMap<Class, ActionServiceListener> actionMapping = new ConcurrentHashMap<Class, ActionServiceListener>();

    public MyUpgradeClient(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    /**
     * 启动客户端服务.
     * @throws IOException exception
     */
    public void start() throws IOException {
        if (running) {
            return;
        }
        running = true;
        socket = new Socket(serverIp, serverPort);
        lastSendTime = System.currentTimeMillis();
        Thread keepAliveWatchDog = new Thread(new KeepAliveWatchDog(this));
        Thread receiveWatchDog = new Thread(new ReceiveWatchDog(this));
        keepAliveWatchDog.start();
        receiveWatchDog.start();
    }

    /**
     * 发送消息.
     * @param object object
     * @throws IOException exception
     */
    public void sendObject(Object object) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(object);
        System.out.println("发送：\t" + object);
        oos.flush();
    }

    /**
     * 添加业务处理逻辑类.
     * @param cls cls
     * @param objectAction objectAction
     */
    public void addActionMap(Class<Object> cls, ActionServiceListener objectAction) {
        actionMapping.put(cls, objectAction);
    }

    /**
     * 停止客户端服务.
     */
    public void stop() {
        if (running) {
            running = false;
        }
    }

    public long getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(long lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public boolean isRunning() {
        return running;
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * 获取客户端业务处理类.
     * @param key key
     * @return
     */
    public ActionServiceListener getObjectAction(Class key) {
        return actionMapping.get(key);
    }

    /**
     * main.
     * @param args args.
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        String serverIp = "localhost";
        int serverPort = 4701;
        MyUpgradeClient myClient = new MyUpgradeClient(serverIp, serverPort);
        myClient.start();
    }
}
