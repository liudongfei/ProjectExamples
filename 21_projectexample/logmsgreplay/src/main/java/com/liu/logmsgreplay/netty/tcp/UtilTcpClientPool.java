package com.liu.logmsgreplay.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/9 15:38
 * @Description:
 */
public class UtilTcpClientPool {
    private String dstIp;
    private int dstPort;
    private int poolSize = 2;
    // max time wait for a channel return from pool
    private int connectTimeOutInMilliSecondes;

    // max idle time for a channel before close
    private int maxIdleTimeInMilliSecondes;

    private Bootstrap clientBootstrap;

    private LinkedBlockingQueue<ChannelFuture> connetionList;

    private NioEventLoopGroup group = new NioEventLoopGroup(0);

    /**
     * .
     * @param dstIp dstIp
     * @param dstPort dstPort
     * @param poolSize poolSize
     * @throws InterruptedException exception
     */
    public UtilTcpClientPool(String dstIp, int dstPort, int poolSize) throws InterruptedException {
        this.poolSize = poolSize;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
        this.clientBootstrap = new Bootstrap().group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                //.addLast(new LoggingHandler())
                                .addLast(new IdleStateHandler(0,
                                        4,
                                        0,
                                        TimeUnit.SECONDS))
                                .addLast(new UtilTcpClientHandler());
                    }
                });
        init();
    }

    private void init() throws InterruptedException {
        if (connetionList == null) {
            connetionList = new LinkedBlockingQueue<>();
        }

        for (int i = 0 ; i < poolSize; i++) {
            connetionList.add(clientBootstrap.connect(dstIp, dstPort).sync());
        }
    }

    public ChannelFuture getConnection() throws InterruptedException {
        return connetionList.take();
    }

    public void returnConnection(ChannelFuture connection) throws InterruptedException {
        connetionList.put(connection);
    }

    /**
     * close.
     */
    public void close() {
        group.shutdownGracefully();
    }

}
