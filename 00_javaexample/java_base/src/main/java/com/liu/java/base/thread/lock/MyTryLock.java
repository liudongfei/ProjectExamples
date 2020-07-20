package com.liu.java.base.thread.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * try lock 样例.
 * @Auther: liudongfei
 * @Date: 2019/2/27 21:54
 * @Description:
 */
public class MyTryLock {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        new Thread("thread0") {
            @Override
            public void run() {
                boolean isLock = lock.tryLock();
                System.out.println(this.getName() + isLock);
                if (isLock) {
                    try {
                        System.out.println(this.getName() + "获取了锁");
                        Thread.sleep(5000);
                        System.out.println(this.getName() + "释放了锁");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }

            }
        }.start();
        new Thread("thread1") {
            @Override
            public void run() {
                boolean isLock = lock.tryLock();
                System.out.println(this.getName() + isLock);
                if (isLock) {
                    try {
                        System.out.println(this.getName() + "获取了锁");
                        Thread.sleep(5000);
                        System.out.println(this.getName() + "释放了锁");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }

            }
        }.start();
    }
}
