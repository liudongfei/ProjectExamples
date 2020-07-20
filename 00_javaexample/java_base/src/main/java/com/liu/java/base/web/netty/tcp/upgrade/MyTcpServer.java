package com.liu.java.base.web.netty.tcp.upgrade;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 简单的netty服务端样例
 * 服务端在连接建立时主动发起请求.
 * @Auther: liudongfei
 * @Date: 2019/3/22 14:10
 * @Description:
 */
public class MyTcpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyTcpServer.class);

    private static BlockingQueue<Channel> channelQueue = new ArrayBlockingQueue<>(200);

    private final int port ;

    public MyTcpServer(int port) {
        this.port = port;
    }

    /**
     * 启动netty服务.
     * @throws Exception e
     */
    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup(5);
        NioEventLoopGroup cgroup = new NioEventLoopGroup(100);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group, cgroup)
                    .channel(NioServerSocketChannel.class)//指定使用NIO类型的channel
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    // 参数1，数据长度的长度；参数2，长度是否包含长度位自身
                                    .addLast(new LengthFieldPrepender(2,
                                            false))
                                    // 参数1，数据包最大长度；参数2，长度位的偏移量；
                                    // 参数3，长度调整值；参数4，跳过几个字节之后的才是长度域(需要处理的内容)
                                    .addLast(new LengthFieldBasedFrameDecoder(
                                            Integer.MAX_VALUE, 0,
                                            2, 0,2))
                                    //.addLast(new LineBasedFrameDecoder(4096))
                                    .addLast(new MyTcpServerHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture future = bootstrap.bind().sync();//绑定服务器，调用sync阻塞等待绑定完成
            LOGGER.info("server started and listen on {}", future.channel().localAddress());
            future.channel().closeFuture().sync();//等待channel关闭，调用sync阻塞等待channel关闭
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static BlockingQueue getChannelQueue() {
        return channelQueue;
    }

    /**
     * main.
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    Channel channel = null;
                    try {
                        channel = channelQueue.take();
                        String msg = "hello" + i;
                        channel.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes(CharsetUtil.UTF_8)));
                        channelQueue.add(channel);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        new MyTcpServer(9091).start();

    }
}
