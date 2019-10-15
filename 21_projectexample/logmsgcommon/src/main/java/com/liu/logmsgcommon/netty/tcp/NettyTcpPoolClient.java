package com.liu.logmsgcommon.netty.tcp;

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
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.net.InetSocketAddress;


/**
 * 客户端连接池.
 * @Auther: liudongfei
 * @Date: 2019/7/24 13:38
 * @Description:
 */
public class NettyTcpPoolClient {
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
                        new NettyTcpChannelPoolHandler(),
                        2);
            }
        };
    }


    public static void main(String[] args) throws InterruptedException {
        NettyTcpPoolClient nettyTcpPoolClient = new NettyTcpPoolClient();
        nettyTcpPoolClient.build();

        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
        final ByteBuf echoReq = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("Hello Netty.$_", CharsetUtil.UTF_8));

        // depending on when you use addr1 or addr2 you will get different pools.
        for (int i = 0; i < 10; i++) {
            final SimpleChannelPool pool = nettyTcpPoolClient
                    .poolMap.get(new InetSocketAddress("127.0.0.1", 8890));
            Future<Channel> f = pool.acquire();
            f.addListener((FutureListener<Channel>) f1 -> {
                if (f1.isSuccess()) {
                    Channel ch = f1.getNow();
                    ch.writeAndFlush(echoReq);
                    // Release back to pool
                    pool.release(ch);
                }
            });

        }

    }
}
