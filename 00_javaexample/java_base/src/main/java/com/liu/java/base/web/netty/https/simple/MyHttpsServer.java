package com.liu.java.base.web.netty.https.simple;

import com.liu.java.base.web.netty.http.simple.MyHttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;

/**
 * netty的http服务端实现.
 * @Auther: liudongfei
 * @Date: 2019/3/22 16:29
 * @Description:
 */
public class MyHttpsServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyHttpsServer.class);
    private final int port;

    public MyHttpsServer(int port) {
        this.port = port;
    }

    /**
     * 启动http服务端.
     * @throws InterruptedException exception
     */
    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup(30);
        try {
            bootstrap.group(group, group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            SSLEngine sslEngine = MySSLContextFactory.getSslContext().createSSLEngine();
                            sslEngine.setUseClientMode(false);
                            sslEngine.setNeedClientAuth(false);
                            socketChannel.pipeline()
                                    .addLast("ssl", new SslHandler(sslEngine))
                                    .addLast("codec", new HttpServerCodec())
                                    .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                    .addLast("handler", new MyHttpServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)//客户端请求队列的长度
                    //.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                    .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3);//

            ChannelFuture future = bootstrap.bind().sync();
            LOGGER.info("server started and listen on:\t{}", future.channel().localAddress());
            future.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MyHttpsServer(8082).start();
    }
}
