package com.liu.logmsgcommon.netty.tcp.factory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


/**
 * tcp服务端.
 * @Auther: liudongfei
 * @Date: 2019/7/24 11:06
 * @Description:
 */
public class NettyTcpServer {
    public static final Logger logger = LoggerFactory.getLogger(NettyTcpServer.class);

    private int port;

    private  ByteBuf delimiter;

    private final ChannelInboundHandler handler;

    private NioEventLoopGroup group = null;
    private NioEventLoopGroup worker = null;

    /**
     * 构造器.
     * @param port 端口
     * @param delimiter 消息分割符
     * @param handler 业务员处理handler
     * @param pgThreadNum 处理连接请求的线程数
     * @param cgThreadNum 处理所有连接读写请求的线程数
     */
    public NettyTcpServer(int port, String delimiter, ChannelInboundHandler handler,
                          Integer pgThreadNum, Integer cgThreadNum) {
        this.port = port;
        if (delimiter != null) {
            this.delimiter = Unpooled.copiedBuffer(delimiter.getBytes());
        }
        this.handler = handler;
        this.group = new NioEventLoopGroup(pgThreadNum);
        this.worker = new NioEventLoopGroup(cgThreadNum);
    }

    /**
     * 启动tcp服务端.
     * @throws Exception e
     */
    public void start() throws Exception {
        Thread tcpServer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    if (delimiter != null) {
                        bootstrap.group(group, worker)
                                .channel(NioServerSocketChannel.class)
                                .localAddress(new InetSocketAddress(port))
                                .childHandler(new ChannelInitializer<SocketChannel>() {
                                    @Override
                                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                                        socketChannel.pipeline()
                                                .addLast(new DelimiterBasedFrameDecoder(1024, delimiter))
                                                .addLast(new StringDecoder())
                                                .addLast(new StringEncoder())
                                                .addLast(handler);
                                    }
                                });
                    } else {
                        bootstrap.group(group, worker)
                                .channel(NioServerSocketChannel.class)
                                .localAddress(new InetSocketAddress(port))
                                .childHandler(new ChannelInitializer<SocketChannel>() {
                                    @Override
                                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                                        socketChannel.pipeline()
                                                .addLast(new StringDecoder())
                                                .addLast(new StringEncoder())
                                                .addLast(handler);
                                    }
                                });
                    }
                    ChannelFuture future = bootstrap.bind().sync();
                    logger.info("tcp server started and listen on " + future.channel().localAddress());
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
        // 设置为守护线程
        tcpServer.setDaemon(true);
        tcpServer.start();

    }

    /**
     * 关闭tcpServer.
     */
    public void close() {
        group.shutdownGracefully();
        worker.shutdownGracefully();
    }


    /**
     * 测试样例.
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        int port = 8890;
        NettyTcpServer nettyTcpServer = new NettyTcpServer(port, "$_",
                new NettyTcpServerHandler(), 2, 5);

        nettyTcpServer.start();
        Thread.sleep(4000);
        nettyTcpServer.close();

        //String s1 = "\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd";
        //String s2 = new String(s1.getBytes("GB2312"));
        //System.out.println(s2);

    }
}
