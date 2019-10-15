package com.liu.java.thread.simple;

/**
 * 实现Runnable接口定义线程.
 * @Auther: liudongfei
 * @Date: 2019/2/26 23:38
 * @Description:
 */
public class MyThreadWithImpliment implements Runnable {
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
        System.out.println(tName + "线程开始运行");
        Thread thread = new Thread(new MyThreadWithImpliment());
        thread.start();
    }
}
