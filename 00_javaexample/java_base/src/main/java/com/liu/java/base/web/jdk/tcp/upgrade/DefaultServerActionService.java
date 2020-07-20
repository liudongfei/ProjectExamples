package com.liu.java.base.web.jdk.tcp.upgrade;

/**
 * 默认服务端业务操作实现类.
 * @Auther: liudongfei
 * @Date: 2019/3/11 13:36
 * @Description:
 */
public class DefaultServerActionService implements ActionServiceListener {
    @Override
    public void doAction(Object object, MyUpgradeClient myClient) {
        System.out.println(object.toString());
    }

    @Override
    public Object doAction(Object object, MyUpgradeServer myServer) {
        System.out.println("处理并返回：\t" + object);
        return object;
    }
}
