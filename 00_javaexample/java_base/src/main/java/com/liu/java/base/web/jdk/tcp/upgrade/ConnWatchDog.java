package com.liu.java.base.web.jdk.tcp.upgrade;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 监控客户端连接线程.
 * @Auther: liudongfei
 * @Date: 2019/3/11 14:42
 * @Description:
 */
public class ConnWatchDog implements Runnable {

    private MyUpgradeServer myServer;

    public ConnWatchDog(MyUpgradeServer myServer) {
        this.myServer = myServer;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(myServer.getServerPort(), 5);
            System.out.println("等待连接。。。");
            while (myServer.isRunning()) {
                Socket socket = serverSocket.accept();
                System.out.println("收到新连接。。。");
                new Thread(new SocketAction(socket, myServer)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            myServer.stop();
        }
    }
}
