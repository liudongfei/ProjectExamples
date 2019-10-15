package com.liu.java.thread.pool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池类.
 * @Auther: liudongfei
 * @Date: 2019/9/5 22:50
 * @Description:
 */
public class ThreadPool {
    /**
     * 线程池.
     */
    private ThreadPoolExecutor threadPoolExecutor = null;

    /**
     * <p>
     * 构造线程池
     * </p>
     * 线程池默认情况下使用。默认的最小线程数为2，最大线程数为4.
     */
    public ThreadPool() {
        this.threadPoolExecutor = new ThreadPoolExecutor(2, 4, 3,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                //new ThreadPoolExecutor.DiscardOldestPolicy()
                new BlockingRejectedExecutionHandler());
    }

    /**
     * 构造线程池
     * 线程池默认情况下使用。默认的最小线程数为2.
     * @param corePoolSize -
     *            最小线程数，不能小于2
     * @param maximumPoolSize -
     *            最大线程数，不能小于最小线程数
     *
     */
    public ThreadPool(int corePoolSize, int maximumPoolSize) {
        if (corePoolSize < 2) {
            corePoolSize = 2;
        }

        if (maximumPoolSize < corePoolSize) {
            maximumPoolSize = corePoolSize + 1;
        }

        this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize, 3, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(corePoolSize * 500),
                //new ThreadPoolExecutor.DiscardOldestPolicy()
                new BlockingRejectedExecutionHandler());
    }

    /**
     * 构造器.
     * @param corePoolSize corePoolSize
     * @param maximumPoolSize maximumPoolSize
     * @param keepAliveTime keepAliveTime
     * @param linkedBlockingQueueSize linkedBlockingQueueSize
     */
    public ThreadPool(int corePoolSize, int maximumPoolSize,long keepAliveTime,int linkedBlockingQueueSize) {
        this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(linkedBlockingQueueSize),
                //new ThreadPoolExecutor.DiscardOldestPolicy()
                new BlockingRejectedExecutionHandler());
    }

    /**
     * 设置 池中所保存的线程数，包括空闲线程.
     *
     * @param corePoolSize 最小线程数
     */
    public void setMinimumPoolSize(int corePoolSize) {
        this.threadPoolExecutor.shutdown();
        this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                corePoolSize + 1, 3, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(corePoolSize * 500),
                //new ThreadPoolExecutor.DiscardOldestPolicy()
                new BlockingRejectedExecutionHandler());
    }

    /**
     * 读取线程池的最大线程数.
     */
    public int getMaximumPoolSize() {
        return threadPoolExecutor.getMaximumPoolSize();
    }

    /**
     * 读取线程池的最小线程数.
     *
     */
    public int getMinimumPoolSize() {
        return threadPoolExecutor.getCorePoolSize();
    }

    /**
     * 设置线程池的最大线程数.
     *
     * @param maximumPoolSize
     *            最大线程数
     */
    public void setMaximumPoolSize(int maximumPoolSize) {
        threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
    }

    /**
     * 添加任务.
     *
     * @param command Runnable的实现
     */
    public void addTask(Runnable command) {

        threadPoolExecutor.execute(command);
    }

    /**
     * 读取正在运行的任务数.
     *
     * @return - 正在运行的任务数
     */
    public int getActiveCount() {
        return threadPoolExecutor.getActiveCount();
    }

    /**
     * 已完成的任务数.
     *
     * @return 完成的任务数
     */
    public long getCompletedTaskCount() {
        return threadPoolExecutor.getCompletedTaskCount();
    }

    /**
     * 获取task数量.
     * @return
     */
    public long getTaskCount() {
        return threadPoolExecutor.getTaskCount();
    }

    /**
     * 打印线程池的使用情况.
     */
    public String toString() {
        int activeCount = threadPoolExecutor.getActiveCount();
        int corePoolSize = threadPoolExecutor.getCorePoolSize();
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        long taskCount = threadPoolExecutor.getTaskCount();
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        int largestPoolSize = threadPoolExecutor.getLargestPoolSize();
        int poolSize = threadPoolExecutor.getPoolSize();

        return "可运行最大线程数：" + maximumPoolSize + "\n可运行最小线程数：" + corePoolSize
                + "\n共有" + taskCount + "个任务，有" + activeCount
                + "个在运行\n曾经最大的并发数为" + largestPoolSize + "\n已完成的任务数是："
                + completedTaskCount
                + "   poolSize:" + poolSize;
    }

    /**
     * 线程执行等待队列Blocking时，RejectedExecution行为.
     * @author zhangpeng
     *
     */
    static class BlockingRejectedExecutionHandler implements
            RejectedExecutionHandler {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor threadPoolExecutor) {

            int activeCount = threadPoolExecutor.getActiveCount();
            int corePoolSize = threadPoolExecutor.getCorePoolSize();
            int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
            long taskCount = threadPoolExecutor.getTaskCount();
            long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
            int largestPoolSize = threadPoolExecutor.getLargestPoolSize();
            int poolSize = threadPoolExecutor.getPoolSize();

            String threadPoolExecutorState = "\n   可运行最大线程数：" + maximumPoolSize + "    可运行最小线程数："
                    + corePoolSize
                    + "  共有" + taskCount + "个任务，有" + activeCount
                    + "个在运行    曾经最大的并发数为" + largestPoolSize + "    已完成的任务数是："
                    + completedTaskCount
                    + "   poolSize:" + poolSize;
            System.out.println("ThreadPoolExecutor LinkedBlockingQueue 堵塞!   " + threadPoolExecutorState
                    + r.toString() + " 线程堵塞超时,任务丢失!");
            //          System.out.println("ThreadPoolExecutor LinkedBlockingQueue 堵塞!   " +threadPoolExecutorState);

            //          if (!threadPoolExecutor.isShutdown()) {
            //              try {
            //                  //直接采用丢弃策略     张鹏  2011-3-21
            //                  if(!threadPoolExecutor.getQueue().offer(r, 1,TimeUnit.SECONDS)){
            //                      logger.warn(r.toString()+" 线程堵塞超时,任务丢失!");
            ////                        System.out.println(r.toString()+" 线程堵塞超时,任务丢失!");
            //                  }
            ////                    threadPoolExecutor.getQueue().put(r);
            //              } catch (InterruptedException e1) {
            //                  // TODO 这里需要增加 异常关闭时的 对等待在该方法thread的错误
            //                  e1.printStackTrace();
            //              }
            //          }


        }
    }
}
