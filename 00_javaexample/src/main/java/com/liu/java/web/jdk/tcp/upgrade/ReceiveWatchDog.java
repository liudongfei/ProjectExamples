package com.liu.java.web.jdk.tcp.upgrade;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * 接收服务端信息的线程.
 * @Auther: liudongfei
 * @Date: 2019/3/11 13:39
 * @Description:
 */
public class ReceiveWatchDog implements Runnable {
    private MyUpgradeClient myClient;

    public ReceiveWatchDog(MyUpgradeClient myClient) {
        this.myClient = myClient;
    }

    @Override
    public void run() {
        while (myClient.isRunning()) {
            try {
                if (myClient.getSocket().getInputStream().available() > 0) {
                    ObjectInputStream ois = new ObjectInputStream(myClient.getSocket().getInputStream());
                    Object object = ois.readObject();
                    System.out.println("接收：\t" + object);
                    ActionServiceListener objectAction = myClient.getObjectAction(object.getClass());
                    objectAction = objectAction == null ? new DefaultClientActionService() : objectAction;
                    objectAction.doAction(object, myClient);
                } else {
                    Thread.sleep(10);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                myClient.stop();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
