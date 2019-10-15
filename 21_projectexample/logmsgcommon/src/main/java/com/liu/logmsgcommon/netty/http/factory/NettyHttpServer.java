package com.liu.logmsgcommon.netty.http.factory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * http服务端.
 * @Auther: liudongfei
 * @Date: 2019/7/30 14:34
 * @Description:
 */
public class NettyHttpServer {
    public static final Logger logger = LoggerFactory.getLogger(NettyHttpServer.class);

    public int port;

    public final ChannelHandler channelHandler;
    private NioEventLoopGroup group = null;
    private NioEventLoopGroup worker = null;

    /**
     * 构造器.
     * @param port 端口
     * @param channelHandler 业务逻辑类
     * @param pgThreadNum 处理连接请求的线程数
     * @param cgThreadNum 处理所有连接读写请求的线程数
     */
    public NettyHttpServer(int port, ChannelHandler channelHandler, Integer pgThreadNum, Integer cgThreadNum) {
        this.port = port;
        this.channelHandler = channelHandler;
        this.group  = new NioEventLoopGroup(pgThreadNum);
        this.worker = new NioEventLoopGroup(cgThreadNum);
    }

    /**
     * 启动http服务端.
     * @throws InterruptedException e
     */
    public void start() throws InterruptedException {
        Thread httpServer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(group, worker)
                            .channel(NioServerSocketChannel.class)
                            .localAddress(new InetSocketAddress(port))
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    socketChannel.pipeline()
                                            .addLast("codec", new HttpServerCodec())
                                            .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                            .addLast(channelHandler);
                                }
                            });
                    ChannelFuture future = bootstrap.bind().sync();

                    logger.info("http server started and listen on " + future.channel().localAddress());
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        group.shutdownGracefully().sync();
                        worker.shutdownGracefully().sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        httpServer.setDaemon(true);
        httpServer.start();

    }

    /**
     * 关闭httpServer.
     */
    public void close() {
        group.shutdownGracefully();
        worker.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {
        NettyHttpServer nettyHttpServer = new NettyHttpServer(8088, new NettyHttpServerHandler(), 2, 5);
        nettyHttpServer.start();
        Thread.sleep(5000);
        nettyHttpServer.close();
    }
}
