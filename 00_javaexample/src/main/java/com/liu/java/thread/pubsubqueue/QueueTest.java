package com.liu.java.thread.pubsubqueue;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 队列生产者消费者样例类.
 * @Auther: liudongfei
 * @Date: 2019/3/19 22:02
 * @Description:
 */
public class QueueTest {
    /**
     * main.
     * @param args args
     * @throws InterruptedException exception
     */
    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(20);
        new Thread(new Consumer(blockingQueue)).start();
        Thread.sleep(500);
        new Thread(new Producer(blockingQueue)).start();


    }
}
