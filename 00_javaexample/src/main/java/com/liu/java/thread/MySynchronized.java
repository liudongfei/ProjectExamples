package com.liu.java.thread;

/**
 * 同步修饰，同一时刻只有获得对象锁的线程中的同步代码块可以执行.
 * @Auther: liudongfei
 * @Date: 2019/2/27 20:42
 * @Description:
 */
public class MySynchronized {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        MySynchronized mySynchronized1 = new MySynchronized();
        MySynchronized mySynchronized2 = new MySynchronized();
        new Thread("thread1") {
            @Override
            public void run() {
                synchronized (mySynchronized1) {
                    try {
                        System.out.println(this.getName() + "start");
                        Thread.sleep(5000);
                        System.out.println(this.getName() + "awake");
                        System.out.println(this.getName() + "end");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread("thread2") {
            @Override
            public void run() {
                synchronized (mySynchronized1) {
                    System.out.println(this.getName() + "start");
                    System.out.println(this.getName() + "end");
                }
            }
        }.start();
    }
}
