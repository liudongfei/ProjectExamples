package com.liu.logmsgcommon.netty.https;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import javax.net.ssl.SSLEngine;

/**
 * http服务端.
 * @Auther: liudongfei
 * @Date: 2019/7/30 14:34
 * @Description:
 */
public class NettyHttpsServer {
    public static final Logger logger = LoggerFactory.getLogger(NettyHttpsServer.class);

    public int port;

    public ChannelHandler channelHandler;

    public NettyHttpsServer(int port, ChannelHandler channelHandler) {
        this.port = port;
        this.channelHandler = channelHandler;
    }

    /**
     * 启动http服务端.
     * @throws InterruptedException e
     */
    public void start() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group, worker)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            SSLEngine sslEngine = SSLContextFactory.getSslContext().createSSLEngine();
                            sslEngine.setUseClientMode(false);
                            sslEngine.setNeedClientAuth(false);
                            socketChannel.pipeline()
                                    .addLast("ssl", new SslHandler(sslEngine))
                                    .addLast("codec", new HttpServerCodec())
                                    .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                    .addLast(channelHandler);
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            logger.info("http server started and listen on " + future.channel().localAddress());
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyHttpsServer(8088, new NettyHttpsServerHandler()).start();
    }
}
