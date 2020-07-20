package com.liu.java.base.thread.lock;

import java.util.concurrent.locks.ReentrantLock;


/**
 * interrupter lock样例.
 * @Auther: liudongfei
 * @Date: 2019/2/27 22:40
 * @Description:
 */
public class Myinterrupter {
    private ReentrantLock lock = new ReentrantLock();

    /**
     * insert.
     * @param thread thread
     * @throws InterruptedException exception
     */
    public void insert(Thread thread) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println(thread.getName() + "释放锁");
        }



    }


    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        Myinterrupter myinterrupter = new Myinterrupter();
        MyThread myThread1 = new MyThread(myinterrupter);
        MyThread myThread2 = new MyThread(myinterrupter);
        myThread1.start();
        myThread2.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myThread2.interrupt();//只能中断正在等待的线程
        System.out.println("=========");


    }

}

class MyThread extends Thread {
    private Myinterrupter myinterrupter = null;

    public MyThread(Myinterrupter myinterrupter) {
     this.myinterrupter = myinterrupter;
    }

    @Override
    public void run() {
        try {
            myinterrupter.insert(Thread.currentThread());
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + "被中断");
        }
    }
}
