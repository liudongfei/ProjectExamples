package com.liu.java.web.jdk.tcp.upgrade;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 处理客户端连接的线程.
 * @Auther: liudongfei
 * @Date: 2019/3/11 14:40
 * @Description:
 */
public class SocketAction implements Runnable {
    private Socket socket;
    private MyUpgradeServer myServer;
    private boolean run = false;
    private long lastReceiveTime = System.currentTimeMillis();

    /**
     * 构造器.
     * @param socket socket
     * @param myServer socket
     */
    public SocketAction(Socket socket, MyUpgradeServer myServer) {
        this.socket = socket;
        this.myServer = myServer;
        this.run = true;
    }

    @Override
    public void run() {
        while (myServer.isRunning() && run) {
            if (System.currentTimeMillis() - lastReceiveTime > 3000) {
                closeSession();
            } else {
                try {
                    if (socket.getInputStream().available() > 0) {
                        System.out.println("获取输入流");
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Object object = ois.readObject();
                        lastReceiveTime = System.currentTimeMillis();
                        ActionServiceListener objectAction = myServer.getObjectAction(object.getClass());
                        objectAction = objectAction == null ? new DefaultServerActionService() : objectAction;
                        Object res = objectAction.doAction(object, myServer);
                        if (res != null) {
                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(res);
                            oos.flush();
                        }

                    } else {
                        Thread.sleep(10);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    closeSession();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    closeSession();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    closeSession();
                }
            }
        }

    }

    /**
     * 关闭该连接.
     */
    private void closeSession() {
        if (run) {
            run = false;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("关闭：\t" + socket.getRemoteSocketAddress());
    }
}
