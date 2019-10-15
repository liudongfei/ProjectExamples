package com.liu.logmsgreplay.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.net.InetSocketAddress;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/3/22 16:29
 * @Description:
 */
public class TestSystemHttpServer {
    private final int port;

    public TestSystemHttpServer(int port) {
        this.port = port;
    }

    /**
     * start netty http server.
     * @throws InterruptedException exception
     */
    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("codec", new HttpServerCodec())
                                    .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                    .addLast("handler", new TestSystemHttpServerHandler());
                        }
                    })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

            ChannelFuture future = bootstrap.bind().sync();
            System.out.println("server started and listen on " + future.channel().localAddress());
            future.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new TestSystemHttpServer(8899).start();
    }
}
