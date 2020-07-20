package com.liu.java.base.web.netty.tcp;

import io.netty.channel.Channel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 工具的TCP响应Future.
 * @Auther: liudongfei
 * @Date: 2019/4/9 11:06
 * @Description:
 */
public class UtilTcpResPacketFuture {
    private final CountDownLatch latch = new CountDownLatch(1);

    private volatile boolean isDone = false;

    private volatile boolean isCancel = false;

    private final AtomicBoolean isProcessed = new AtomicBoolean(false);
    private volatile UtilTcpResPacketBuilder resPacketBuilder;

    private volatile Channel channel;

    /**
     * .
     * @param cause cause
     * @return
     */
    public boolean cancel(Throwable cause) {
        if (isProcessed.getAndSet(true)) {
            return false;
        }

        resPacketBuilder = new UtilTcpResPacketBuilder();
        resPacketBuilder.setSuccess(false);
        resPacketBuilder.setCause(cause);
        isCancel = true;
        latch.countDown();
        return true;
    }

    public UtilTcpResPacket get() throws InterruptedException, ExecutionException {
        latch.await();
        return resPacketBuilder.build();
    }


    /**
     * .
     * @param timeout timeout
     * @param unit unit
     * @return .
     * @throws TimeoutException exception
     * @throws InterruptedException exception
     */
    public UtilTcpResPacket get(long timeout, TimeUnit unit) throws TimeoutException,
            InterruptedException {
        if (!latch.await(timeout, unit)) {
            throw new TimeoutException();
        }
        return resPacketBuilder.build();
    }

    /**
     * .
     * @return
     */
    public boolean done() {
        if (isProcessed.getAndSet(true)) {
            return false;
        }
        isDone = true;
        latch.countDown();
        return true;
    }

    public boolean isCancelled() {
        return isCancel;
    }

    public boolean isDone() {
        return isDone;
    }

    /**
     * Getter method for property <tt>channel</tt>.
     *
     * @return property value of channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Setter method for property <tt>channel</tt>.
     *
     * @param channel value to be assigned to property channel
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    /**
     * Getter method for property <tt>responseBuilder</tt>.
     *
     * @return property value of responseBuilder
     */
    public UtilTcpResPacketBuilder getResPacketBuilder() {
        return resPacketBuilder;
    }

    /**
     * Setter method for property <tt>responseBuilder</tt>.
     *
     * @param resPacketBuilder value to be assigned to property responseBuilder
     */
    public void setResPacketBuilder(UtilTcpResPacketBuilder resPacketBuilder) {
        this.resPacketBuilder = resPacketBuilder;
    }
}
