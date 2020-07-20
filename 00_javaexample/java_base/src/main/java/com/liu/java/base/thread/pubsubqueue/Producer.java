package com.liu.java.base.thread.pubsubqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 消息生产者线程.
 * @Auther: liudongfei
 * @Date: 2019/3/19 21:51
 * @Description:
 */
public class Producer implements Runnable {
    private BlockingQueue<String> blockingQueue;

    public Producer(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        int i = 0;
        while (true) {
            System.out.println("生产者生产第" + i + "条数据");
            try {
                //blockingQueue.put("value" + i);//如果放不进去就会阻塞线程
                //boolean offer = blockingQueue.offer("value" + i);//如果放不进去，返回false
                //如果放不进去，则会等待指定时间再放，再放入不成功则会返回false
                boolean offer = blockingQueue.offer("value" + i, 100, TimeUnit.MILLISECONDS);

                String res = offer ? "生产成功" : "生产失败";
                System.out.println(res);
                i++;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
