package com.liu.java.web.netty.tcp.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 基于netty实现的tcp客户端.
 * @Auther: liudongfei
 * @Date: 2019/3/22 14:10
 * @Description:
 */
public class MyTcpClient {
    private final Logger logger = LoggerFactory.getLogger(MyTcpClient.class);
    private final String host;
    private final int port;
    private ChannelFuture future;

    public MyTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 启动客户端服务.
     * @throws Exception exception
     */
    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group)
                            .channel(NioSocketChannel.class)
                            .remoteAddress(new InetSocketAddress(host, port))
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    socketChannel.pipeline()
                                            .addLast(new LengthFieldPrepender(2,
                                                    false))
                                            .addLast(new LengthFieldBasedFrameDecoder(
                                                    Integer.MAX_VALUE, 0,
                                                    2, 0,2))
                                            //.addLast(new LengthFieldBasedFrameDecoder(
                                            //Integer.MAX_VALUE, 0, 4))
                                            .addLast(new MyTcpClientHandler());
                                }
                            }).option(ChannelOption.SO_KEEPALIVE, true);
                    future = bootstrap.connect().sync();
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    group.shutdownGracefully();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 发送消息.
     * @param msg msg
     */
    public void sendMsg(String msg) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg.getBytes(CharsetUtil.UTF_8));
        future.channel().writeAndFlush(byteBuf);
    }

    public void sendMsg(byte[] msg) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg);
        future.channel().writeAndFlush(byteBuf);
    }

    /**
     * main.
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {

        MyTcpClient client = new MyTcpClient("localhost", 9091);
        client.start();
        Thread.sleep(5000);
        for (int i = 0 ; i < 10 ; i++) {
            client.sendMsg("hello" + i);
            //client.sendMsg(hex2Bytes(msg));
            Thread.sleep(1000);
        }
        Thread.sleep(1000000);
    }

    /**
     * 16进制字符串 转换为对应的 byte数组.
     */
    public static byte[] hex2Bytes(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        char[] hexChars = hex.toCharArray();
        byte[] bytes = new byte[hexChars.length / 2];   // 如果 hex 中的字符不是偶数个, 则忽略最后一个

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt("" + hexChars[i * 2] + hexChars[i * 2 + 1], 16);
        }

        return bytes;
    }
}
