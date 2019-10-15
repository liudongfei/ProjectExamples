package com.liu.java.thread.pubsubqueue;

import java.util.concurrent.BlockingQueue;

/**
 * 消费者线程.
 * @Auther: liudongfei
 * @Date: 2019/3/19 21:50
 * @Description:
 */
public class Consumer implements Runnable {
    /* 消费者队列 */
    private BlockingQueue<String> blockingQueue ;

    public Consumer(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        while (true) {
            try {
                String value = blockingQueue.take();//消费不到数据就会阻塞线程
                //String value = blockingQueue.poll();//消费不到数据就会返回null
                //String value = blockingQueue.poll(100, TimeUnit.MILLISECONDS);
                System.out.println(name + "消费：\t" + value);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
