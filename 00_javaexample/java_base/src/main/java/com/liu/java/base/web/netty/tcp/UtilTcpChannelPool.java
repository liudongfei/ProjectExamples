package com.liu.java.base.web.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/8 17:28
 * @Description:
 */
public class UtilTcpChannelPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilTcpChannelPool.class);
    // channel pools per route
    private ConcurrentMap<String, LinkedBlockingQueue<Channel>> routeToPoolChannels;

    // max number of channels allow to be created per route
    private ConcurrentMap<String, Semaphore> maxPerRoute;

    // max time wait for a channel return from pool
    private int connectTimeOutInMilliSecondes;

    // max idle time for a channel before close
    private int maxIdleTimeInMilliSecondes;

    private AdditionalTcpChannelInitializer additionalChannelInitializer;

    /**
     * value is false indicates that when there is not any channel in pool and no new
     * channel allowed to be create based on maxPerRoute, a new channel will be forced
     * to create.Otherwise, a <code>TimeoutException</code> will be thrown
     * */
    private boolean forbidForceConnect;

    // default max number of channels allow to be created per route
    private static final int DEFAULT_MAX_PER_ROUTE = 10;

    private EventLoopGroup group;

    private final Bootstrap clientBootstrap;

    private static final String COLON = ":";

    /**
     * .
     * @param maxPerRoute maxPerRoute
     * @param connectTimeOutInMilliSecondes connectTimeOutInMilliSecondes
     * @param maxIdleTimeInMilliSecondes maxIdleTimeInMilliSecondes
     * @param forbidForceConnect forbidForceConnect
     * @param additionalChannelInitializer additionalChannelInitializer
     * @param options options
     * @param customGroup customGroup
     */
    public UtilTcpChannelPool(Map<String, Integer> maxPerRoute, int connectTimeOutInMilliSecondes,
                              int maxIdleTimeInMilliSecondes, boolean forbidForceConnect,
                              AdditionalTcpChannelInitializer additionalChannelInitializer,
                              Map<ChannelOption, Object> options, EventLoopGroup customGroup) {
        this.additionalChannelInitializer = additionalChannelInitializer;
        this.maxIdleTimeInMilliSecondes = maxIdleTimeInMilliSecondes;
        this.connectTimeOutInMilliSecondes = connectTimeOutInMilliSecondes;
        this.maxPerRoute = new ConcurrentHashMap<>();
        this.routeToPoolChannels = new ConcurrentHashMap<>();
        this.group = null == customGroup ? new NioEventLoopGroup() : customGroup;
        this.forbidForceConnect = forbidForceConnect;

        this.clientBootstrap = new Bootstrap();
        clientBootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("log", new LoggingHandler());
                        if (null != UtilTcpChannelPool.this.additionalChannelInitializer) {
                            UtilTcpChannelPool.this.additionalChannelInitializer.initChannel(ch);
                        }

                        ch.pipeline().addLast(IdleStateHandler.class.getSimpleName(),
                                new IdleStateHandler(0, 5 * 1000,
                                        UtilTcpChannelPool.this.maxIdleTimeInMilliSecondes, TimeUnit.MILLISECONDS));

                        ch.pipeline().addLast(UtilTcpChannelPoolHandler.class.getSimpleName(),
                                new UtilTcpChannelPoolHandler(UtilTcpChannelPool.this));
                    }
                });
        if (null != options) {
            for (Map.Entry<ChannelOption, Object> entry : options.entrySet()) {
                clientBootstrap.option(entry.getKey(), entry.getValue());
            }
        }

        if (null != maxPerRoute) {
            for (Map.Entry<String, Integer> entry : maxPerRoute.entrySet()) {
                this.maxPerRoute.put(entry.getKey(), new Semaphore(entry.getValue()));
            }
        }
    }

    /**
     * send tcp packet to server specified by the route. The channel used to
     * send the request is obtained according to the follow rules
     * <p>
     * 1. poll the first valid channel from pool without waiting. If no valid
     * channel exists, then go to step 2.
     * 2. create a new channel and return. If failed to create a new channel, then go to step 3.
     * Note: the new channel created in this step will be returned to the pool
     * 3. poll the first valid channel from pool within specified waiting time. If no valid
     * channel exists and the value of forbidForceConnect is false, then throw <code>TimeoutException</code>.
     * Otherwise,go to step 4.
     * 4. create a new channel and return. Note: the new channel created in this step will not be returned to the pool.
     * </p>
     *
     * @param route
     *            target server
     * @param reqPacket
     *            reqPacket
     * @return .
     * @throws InterruptedException InterruptedException
     * @throws TimeoutException exception
     * @throws IOException exception
     * @throws Exception exception
     */
    public UtilTcpResPacketFuture sendPacket(InetSocketAddress route, final UtilTcpReqPacket reqPacket)
            throws InterruptedException, IOException {
        final UtilTcpResPacketFuture resPacketFuture = new UtilTcpResPacketFuture();
        if (sendReqPacketUsePooledChannel(route, reqPacket, resPacketFuture, false)) {
            return resPacketFuture;
        }

        if (sendReqPacketUseNewChannel(route, reqPacket, resPacketFuture, forbidForceConnect)) {
            return resPacketFuture;
        }

        if (sendReqPacketUsePooledChannel(route, reqPacket, resPacketFuture, true)) {
            return resPacketFuture;
        }

        throw new IOException("send request failed");
    }

    private boolean sendReqPacketUseNewChannel(InetSocketAddress route, UtilTcpReqPacket reqPacket,
                                               UtilTcpResPacketFuture resPacketFuture, boolean forceConnect) {
        ChannelFuture future = createChannelFuture(route, forceConnect);
        if (null != future) {
            UtilTcpResPacketFutureUtil.attributeResponse(future.channel(), resPacketFuture);
            UtilTcpResPacketFutureUtil.attributeRoute(future.channel(), route);
            future.addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    future1.channel().closeFuture().addListener((ChannelFutureListener) future11 -> {
                        if (future11 != null) {
                            if (future11.cause() != null) {
                                LOGGER.error(future11.channel() + " closed, exception: " + future11.cause());
                                removeChannel(future11.channel(), future11.cause());
                            }

                        }
                    });
                    future1.channel().writeAndFlush(reqPacket.getContent())
                            .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    LOGGER.info("send msg with new channel");
                } else {
                    LOGGER.error(future1.channel() + " connect failed, exception: " + future1.cause());

                    UtilTcpResPacketFutureUtil.cancel(future1.channel(), future1.cause());
                    if (!UtilTcpResPacketFutureUtil.getForceConnect(future1.channel())) {
                        releaseCreatePerRoute(future1.channel());
                    }
                }
            });
            return true;
        }
        return false;
    }

    private boolean sendReqPacketUsePooledChannel(InetSocketAddress route, UtilTcpReqPacket reqPacket,
                                                  UtilTcpResPacketFuture resPacketFuture, boolean isWaiting)
            throws InterruptedException {
        LinkedBlockingQueue<Channel> poolChannels = getPoolChannels(getKey(route));
        LOGGER.error("poolChannels size============\t{}\t===============", poolChannels.size());
        Channel channel = poolChannels.poll();

        while (null != channel && !channel.isActive()) {
            channel = poolChannels.poll();
        }

        if (null == channel || !channel.isActive()) {
            if (!isWaiting) {
                return false;
            }
            channel = poolChannels.poll(connectTimeOutInMilliSecondes, TimeUnit.MILLISECONDS);
            if (null == channel || !channel.isActive()) {
                LOGGER.warn("obtain channel from pool timeout");
                return false;
            }
        }

        LOGGER.info(channel + " reuse");
        UtilTcpResPacketFutureUtil.attributeResponse(channel, resPacketFuture);
        channel.writeAndFlush(reqPacket.getContent()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        LOGGER.info("send msg with reuse channel");
        return true;
    }

    /**
     * return the specified channel to pool.
     * @param channel channel
     */
    public void returnChannel(Channel channel) {
        if (UtilTcpResPacketFutureUtil.getForceConnect(channel)) {
            LOGGER.info(channel + "forceConnect");
            return;
        }
        InetSocketAddress route = (InetSocketAddress) channel.remoteAddress();
        String key = getKey(route);
        LinkedBlockingQueue<Channel> poolChannels = routeToPoolChannels.get(key);

        if (null != channel && channel.isActive()) {
            if (poolChannels.offer(channel)) {
                LOGGER.info(channel + "returned");
            } else {
                LOGGER.info(channel + "returned failed!!!!");
            }
        }
    }

    /**
     * close all channels in the pool and shut down the eventLoopGroup.
     * @throws InterruptedException InterruptedException
     */
    public void close() throws InterruptedException {
        ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        for (LinkedBlockingQueue<Channel> queue : routeToPoolChannels.values()) {
            for (Channel channel : queue) {
                removeChannel(channel, null);
                channelGroup.add(channel);
            }
        }
        channelGroup.close().sync();
        group.shutdownGracefully();
    }

    /**
     * remove the specified channel from the pool,cancel the responseFuture
     * and release semaphore for the route.
     *
     * @param channel channel
     */
    private void removeChannel(Channel channel, Throwable cause) {

        InetSocketAddress route = (InetSocketAddress) channel.remoteAddress();
        String key = getKey(route);

        UtilTcpResPacketFutureUtil.cancel(channel, cause);

        if (!UtilTcpResPacketFutureUtil.getForceConnect(channel)) {
            LinkedBlockingQueue<Channel> poolChannels = routeToPoolChannels.get(key);
            if (poolChannels.remove(channel)) {
                LOGGER.info(channel + " removed");
            }
            getAllowCreatePerRoute(key).release();
        }
    }

    public void releaseCreatePerRoute(Channel channel) {
        InetSocketAddress route = UtilTcpResPacketFutureUtil.getRoute(channel);
        getAllowCreatePerRoute(getKey(route)).release();
    }

    private Semaphore getAllowCreatePerRoute(String key) {
        Semaphore allowCreate = maxPerRoute.get(key);
        if (null == allowCreate) {
            Semaphore newAllowCreate = new Semaphore(DEFAULT_MAX_PER_ROUTE);
            allowCreate = maxPerRoute.putIfAbsent(key, newAllowCreate);
            if (null == allowCreate) {
                allowCreate = newAllowCreate;
            }
        }

        return allowCreate;
    }

    private LinkedBlockingQueue<Channel> getPoolChannels(String route) {
        LinkedBlockingQueue<Channel> oldPoolChannels = routeToPoolChannels.get(route);
        if (null == oldPoolChannels) {
            LinkedBlockingQueue<Channel> newPoolChannels = new LinkedBlockingQueue<Channel>();
            oldPoolChannels = routeToPoolChannels.putIfAbsent(route, newPoolChannels);
            if (null == oldPoolChannels) {
                oldPoolChannels = newPoolChannels;
            }
        }
        return oldPoolChannels;
    }

    private String getKey(InetSocketAddress route) {
        return route.getHostName() + COLON + route.getPort();
    }

    private ChannelFuture createChannelFuture(InetSocketAddress route, boolean forceConnect) {
        String key = getKey(route);

        Semaphore allowCreate = getAllowCreatePerRoute(key);
        if (allowCreate.tryAcquire()) {
            try {
                ChannelFuture connectFuture = clientBootstrap.connect(route.getHostName(), route.getPort());
                LOGGER.info("create new channel:\t{}", connectFuture.channel());
                return connectFuture;
            } catch (Exception e) {
                LOGGER.error("connect failed", e);
                allowCreate.release();
            }
        }
        if (forceConnect) {
            ChannelFuture connectFuture = clientBootstrap.connect(route.getHostName(), route.getPort());
            if (null != connectFuture) {
                UtilTcpResPacketFutureUtil.attributeForceConnect(connectFuture.channel(), forceConnect);
            }
            return connectFuture;
        }
        return null;
    }
}
