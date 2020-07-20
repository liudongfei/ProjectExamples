package com.liu.java.base.thread.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 简单的线程锁样例.
 * @Auther: liudongfei
 * @Date: 2019/2/27 21:48
 * @Description:
 */
public class MyLock {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        new Thread("thread1") {
            @Override
            public void run() {
                try {
                    lock.lock();
                    System.out.println(this.getName() + "获取了锁");
                    Thread.sleep(5000);
                    System.out.println(this.getName() + "释放了锁");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }.start();
        new Thread("thread2") {
            @Override
            public void run() {
                try {
                    lock.lock();
                    System.out.println(this.getName() + "获取了锁");
                    Thread.sleep(5000);
                    System.out.println(this.getName() + "释放了锁");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }.start();
    }
}
