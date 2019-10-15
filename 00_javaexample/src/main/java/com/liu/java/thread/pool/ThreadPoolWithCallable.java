package com.liu.java.thread.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 返回线程的执行结果.
 * @Auther: liudongfei
 * @Date: 2019/3/18 23:34
 * @Description:
 */
public class ThreadPoolWithCallable {
    /**
     * main.
     * @param args args
     * @throws ExecutionException exception
     * @throws InterruptedException exception
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 10; i++) {
            Future<String> submit = pool.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return Thread.currentThread().getName();
                }
            });
            System.out.println(submit.get());//阻塞主线程,直到线程返回结果
        }
        pool.shutdown();
    }
}
