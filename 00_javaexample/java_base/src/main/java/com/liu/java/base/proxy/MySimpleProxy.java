package com.liu.java.base.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 简单的动态代理样例类.
 * @Auther: liudongfei
 * @Date: 2019/3/21 14:56
 * @Description:
 */
public class MySimpleProxy {
    /**
     * 获取销售代理.
     * @return
     */
    public static SalerInterface getProxySaler() {
        Object instance = Proxy.newProxyInstance(
                SalerInterface.class.getClassLoader(),//接口类的类加载器
                new Class[]{SalerInterface.class},//被代理类的接口类数组
                new InvocationHandler() { //处理接口
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    int price = (int)method.invoke(new SalerImpl(), args);
                    System.out.println("在原价基础上加10元");
                    return price + 10;
                }
            });
        return (SalerInterface) instance;
    }

    /**
     * 获取接口T的代理.
     * @param interfaceclass proxy class classloader
     * @param implementclass proxy class interface
     * @param incrementPrice increament price
     * @param <T> proxy class
     * @return
     */
    public static <T> T getProxy(Class<T> interfaceclass, Class<?> implementclass, int incrementPrice) {
        return (T)Proxy.newProxyInstance(
                interfaceclass.getClassLoader(),
                new Class[]{interfaceclass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        int returnValue = (int)method.invoke(implementclass.newInstance(), args);
                        return returnValue + incrementPrice;
                    }
                });

    }

    /**
     * main.
     * @param args main args
     */
    public static void main(String[] args) {
        //SalerInterface proxySaler = MySimpleProxy.getProxySaler();
        //int price = proxySaler.getPrice("yifu");
        //System.out.println(price);
        SalerInterface  proxy = MySimpleProxy.getProxy(SalerInterface.class, SalerImpl.class, 90);
        System.out.println(proxy.getPrice("yifu"));
    }
}
