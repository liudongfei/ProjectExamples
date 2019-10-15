package com.liu.logmsgcommon.netty.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * http连接池客户端.
 * @Auther: liudongfei
 * @Date: 2019/7/30 14:46
 * @Description:
 */
public class NettyHttpPoolClient {
    final EventLoopGroup group = new NioEventLoopGroup();
    final Bootstrap bootstrap = new Bootstrap();

    InetSocketAddress addr1 = new InetSocketAddress("127.0.0.1", 8890);
    InetSocketAddress addr2 = new InetSocketAddress("localhost", 8891);

    ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;

    /**
     * 创建连接池.
     */
    public void build() {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress inetSocketAddress) {
                return new FixedChannelPool(
                        bootstrap.remoteAddress(inetSocketAddress),
                        new NettyHttpChannelPoolHandler(),
                        2);
            }
        };
    }


    /**
     * test.
     * @param args args
     * @throws InterruptedException e
     * @throws URISyntaxException e
     */
    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        NettyHttpPoolClient nettyHttpPoolClient = new NettyHttpPoolClient();
        nettyHttpPoolClient.build();

        // depending on when you use addr1 or addr2 you will get different pools.
        for (int i = 0; i < 10; i++) {
            ByteBuf echoReq = Unpooled.wrappedBuffer("Hello Netty http server".getBytes(CharsetUtil.UTF_8));
            URI uri = new URI("/test");
            String msg = "Hello Netty http server" ;
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                    uri.toASCIIString(),
                    echoReq);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
            final SimpleChannelPool pool = nettyHttpPoolClient
                    .poolMap.get(new InetSocketAddress("127.0.0.1", 8088));
            Future<Channel> f = pool.acquire();
            f.addListener((FutureListener<Channel>) f1 -> {
                if (f1.isSuccess()) {
                    Channel ch = f1.getNow();
                    ch.writeAndFlush(request);
                    // Release back to pool
                    pool.release(ch);
                }
            });

        }

    }
}
