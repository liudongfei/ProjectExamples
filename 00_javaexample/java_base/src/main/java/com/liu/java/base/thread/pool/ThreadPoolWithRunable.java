package com.liu.java.base.thread.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 不会返回线程的任何结果.
 * @Auther: liudongfei
 * @Date: 2019/3/18 23:32
 * @Description:
 */
public class ThreadPoolWithRunable {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 1; i < 5; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName());
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        pool.shutdown();
    }
}
