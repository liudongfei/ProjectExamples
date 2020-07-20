package com.liu.java.base.thread.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 线程读写锁样例.
 * @Auther: liudongfei
 * @Date: 2019/2/27 23:40
 * @Description:
 */
public class MyReadWriteLock {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        MyReadWriteLock myReadWriteLock = new MyReadWriteLock();
        new Thread("thread0") {
            @Override
            public void run() {
                myReadWriteLock.get();
                myReadWriteLock.write();
            }
        }.start();
        new Thread("thread1") {
            @Override
            public void run() {
                myReadWriteLock.get();
                myReadWriteLock.write();
            }
        }.start();
    }

    /**
     * get method.
     */
    public void get() {
        lock.readLock().lock();
        try {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start <= 1) {
                System.out.println(Thread.currentThread().getName() + "进行读操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName() + "释放锁");
            lock.readLock().unlock();
        }
    }

    /**
     * write method.
     */
    public void write() {
        lock.writeLock().lock();
        try {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start <= 1) {
                System.out.println(Thread.currentThread().getName() + "进行写操作");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
            System.out.println(Thread.currentThread().getName() + "释放锁");
        }
    }
}
