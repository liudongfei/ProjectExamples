package com.liu.java.base.web.netty.http.simple;

import com.liu.java.base.web.netty.https.simple.MyHttpsServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * netty的http服务端实现.
 * @Auther: liudongfei
 * @Date: 2019/3/22 16:29
 * @Description:
 */
public class MyHttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyHttpsServer.class);

    private final int port;

    public MyHttpServer(int port) {
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
                            socketChannel.pipeline()
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
        new MyHttpServer(8081).start();
    }
}
