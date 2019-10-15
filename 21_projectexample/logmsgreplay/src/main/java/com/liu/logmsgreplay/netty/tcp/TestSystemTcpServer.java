package com.liu.logmsgreplay.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * test system tcp server.
 * @Auther: liudongfei
 * @Date: 2019/4/7 15:47
 * @Description:
 */
public class TestSystemTcpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSystemTcpServer.class);
    private final int port ;

    public TestSystemTcpServer(int port) {
        this.port = port;
    }

    /**
     * start netty tcp server.
     * @throws Exception e
     */
    public void start() throws Exception {
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
                            socketChannel.pipeline()
                                    .addLast(new IdleStateHandler(
                                            5,
                                            0,
                                            0,
                                            TimeUnit.SECONDS))
                                    .addLast(new TestSystemTcpServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            LOGGER.info("server started and listen on " + future.channel().localAddress());
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new TestSystemTcpServer(8899).start();
    }
}
