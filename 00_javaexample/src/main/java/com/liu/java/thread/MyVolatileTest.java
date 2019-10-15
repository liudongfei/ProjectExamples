package com.liu.java.thread;

/**
 * volatile 使用样例类.
 * @Auther: liudongfei
 * @Date: 2019/3/21 15:38
 * @Description:
 */
public class MyVolatileTest {
    /**
     * 将变量标记为存储在内存中，而不是从cpu缓存中读取
     * volatile不能保证多个线程同时读写一个变量的安全性要求，
     * 只能用于一个线程写，多个线程读的场景下.
     */
    public static volatile int num = 0;

    /**
     * main.
     * @param args args
     * @throws InterruptedException exception
     */
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        num++;
                    }
                }
            }).start();
        }
        Thread.sleep(20000);
        System.out.println(num);
    }
}
