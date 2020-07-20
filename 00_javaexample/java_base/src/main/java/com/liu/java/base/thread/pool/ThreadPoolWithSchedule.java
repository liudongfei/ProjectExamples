package com.liu.java.base.thread.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 调度线程池例子.
 * @Auther: liudongfei
 * @Date: 2019/3/19 00:01
 * @Description:
 */
public class ThreadPoolWithSchedule {
    /**
     * main.
     * @param args args
     * @throws InterruptedException exception
     */
    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);
        for (int i = 0; i < 5; i++) {
            pool.schedule(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
                }
            }, 3, TimeUnit.SECONDS);
            Thread.sleep(1000);
        }
        pool.shutdown();
    }
}
