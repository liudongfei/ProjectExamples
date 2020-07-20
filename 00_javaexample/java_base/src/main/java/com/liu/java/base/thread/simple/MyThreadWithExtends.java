package com.liu.java.base.thread.simple;

/**
 * 继承Thread抽象类定义线程.
 * @Auther: liudongfei
 * @Date: 2019/2/26 23:36
 * @Description:
 */
public class MyThreadWithExtends extends Thread {
    @Override
    public void run() {
        String tName = Thread.currentThread().getName();
        System.out.println(tName + "线程的run方法被调用");
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        String tName = Thread.currentThread().getName();
        System.out.println(tName + "线程的run方法被调用");
        MyThreadWithExtends thread = new MyThreadWithExtends();
        thread.start();
    }
}
