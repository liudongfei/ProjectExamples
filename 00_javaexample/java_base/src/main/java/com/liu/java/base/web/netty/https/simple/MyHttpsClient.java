package com.liu.java.base.web.netty.https.simple;

import com.liu.java.base.web.netty.http.simple.MyHttpClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;


/**
 * netty的https客户端实现，创建5个https的链接，
 * 轮训方式向各个链接发送请求.
 * @Auther: liudongfei
 * @Date: 2019/3/22 16:58
 * @Description:
 */
public class MyHttpsClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyHttpsClient.class);
    private final String host;
    private final int port;

    public MyHttpsClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * main.
     * @param args args
     * @throws InterruptedException e
     * @throws URISyntaxException e
     */
    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        //new MyHttpClient("localhost", 8899).start();
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup(20);
        //MyHttpClientHandler myHandler = new MyHttpClientHandler();

        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("codec", new HttpClientCodec())
                                    .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                    .addLast("handler", new MyHttpClientHandler());
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            HashMap<Integer, ChannelFuture> channelFutureHashMap = new HashMap<>();

            // 创建5个客户端链接
            for (int i = 0; i < 1; i++) {
                ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8081)).sync();
                channelFutureHashMap.put(i, future);
            }
            // 轮询的方式向5个客户端链接发送请求
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                URI uri = new URI("/");
                String msg = "hello world," + i;
                ChannelFuture future = channelFutureHashMap.get(i % channelFutureHashMap.size());
                DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                        HttpMethod.GET,
                        uri.toASCIIString(),
                        Unpooled.wrappedBuffer(msg.getBytes(CharsetUtil.UTF_8)));
                //request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                request.headers().set(HttpHeaderNames.HOST, "localhost");
                request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());

                future.channel().write(request);
                future.channel().flush();
                System.out.println("client request: " + msg);
                //channelFuture.addListener(ChannelFutureListener.CLOSE);
                //future.channel().close();
            }

            Thread.sleep(10000);
            for (ChannelFuture channelFuture : channelFutureHashMap.values()) {
                channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isDone()) {
                            System.out.println("done");
                        } else {
                            System.out.println("not done");
                        }
                    }
                });
                channelFuture.channel().close();
            }
            //future.channel().close();
            //future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
