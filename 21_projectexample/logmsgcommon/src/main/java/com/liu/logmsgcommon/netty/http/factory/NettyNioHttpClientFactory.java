package com.liu.logmsgcommon.netty.http.factory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * http客户端工厂类.
 * @Auther: liudongfei
 * @Date: 2019/7/30 09:35
 * @Description:
 */
public class NettyNioHttpClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(NettyNioHttpClientFactory.class);

    private int defaultPerPoolSize = 2;
    private int maxPerPoolSize;
    private int threadNum;
    private int defaultThreadNum = 5;
    private Integer maxMessageLen = 1024;

    private EventLoopGroup group = null;
    private ChannelPoolMap<InetSocketAddress, SimpleChannelPool> channelPoolMap;

    private static NettyNioHttpClientFactory instance;
    /**
     * 工厂类的初始化.
     * @param handler 业务处理handler
     * @param maxPerPoolSize 每个连接池的最大连接数
     * @param threadNum 处理线程的数量
     * @param maxMessageLen 消息最大长度
     */

    public static NettyNioHttpClientFactory buildFactory(ChannelInboundHandler handler,
        Integer maxPerPoolSize, Integer threadNum,  Integer maxMessageLen) {
        synchronized (NettyNioHttpClientFactory.class) {
            if (instance == null) {
                NettyNioHttpClientFactory factory = new NettyNioHttpClientFactory();
                factory.init(handler, maxPerPoolSize, threadNum, maxMessageLen);
                instance = factory;
            }
        }
        return instance;
    }

    /**
     * 工厂类的初始化.
     * @param handler 业务处理handler
     * @param maxPerPoolSize 每个连接池的最大连接数
     * @param threadNum 处理线程的数量
     * @param maxMessageLen 消息最大长度
     */
    private void init(ChannelInboundHandler handler, Integer maxPerPoolSize,
                      Integer threadNum, Integer maxMessageLen) {
        if (handler == null) {
            throw new NullPointerException("Param ChannelInboundHandler is null");
        } if (maxPerPoolSize == null) {
            maxPerPoolSize = defaultPerPoolSize;
        } if (threadNum == null) {
            threadNum = defaultThreadNum;
        } if (maxMessageLen == null) {
            maxMessageLen = 1024;
        }

        this.maxPerPoolSize = maxPerPoolSize;
        this.threadNum = threadNum;
        this.group = new NioEventLoopGroup(defaultThreadNum);

        ChannelPoolHandler poolHandler =
                new NettyNioHttpClientFactory.NettyHttpChannelPoolHandler(handler, maxMessageLen);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);

        this.channelPoolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress inetSocketAddress) {
                return new FixedChannelPool(
                        bootstrap.remoteAddress(inetSocketAddress), poolHandler, defaultPerPoolSize);
            }
        };
    }

    /**
     * 获取指定连接的连接池.
     * @param key 目标地址key
     * @return
     */
    public static SimpleChannelPool getChannelPool(InetSocketAddress key) {
        if (instance == null) {
            throw new NullPointerException("NettyNioDelimiterTcpClientFactory is not initialized.");
        }
        return instance.channelPoolMap.get(key);
    }

    /**
     * 从pool获取指定连接的channel.
     * @param key 目标地址key
     * @return
     */
    public static Future<Channel> getChannel(InetSocketAddress key) {
        if (instance == null) {
            throw new NullPointerException("NettyNioDelimiterTcpClientFactory is not initialized.");
        }
        return instance.channelPoolMap.get(key).acquire();
    }

    /**
     * 将channel放回到指定的channel中.
     * @param key 目标地址key
     * @param channel channel连接
     */
    public static void releaseChannel(InetSocketAddress key, Channel channel) {
        if (instance == null) {
            throw new NullPointerException("NettyNioDelimiterTcpClientFactory is not initialized.");
        }
        instance.channelPoolMap.get(key).release(channel);
    }

    /**
     * 关闭指定的连接池.
     * @param key 目标地址key
     */
    public static void close(InetSocketAddress key) {
        instance.channelPoolMap.get(key).close();
    }

    public static void close() throws InterruptedException {
        instance.group.shutdownGracefully().sync();
    }

    public int getMaxPerPoolSize() {
        return maxPerPoolSize;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public Integer getMaxMessageLen() {
        return maxMessageLen;
    }

    class NettyHttpChannelPoolHandler implements ChannelPoolHandler {
        ChannelInboundHandler handler;
        Integer maxMessageLen;

        public NettyHttpChannelPoolHandler(ChannelInboundHandler handler, Integer maxMessageLen) {
            this.handler = handler;
            this.maxMessageLen = maxMessageLen;
        }

        @Override
        public void channelReleased(Channel channel) throws Exception {
            logger.info("channelReleased. Channel ID: " + channel.id());
        }

        @Override
        public void channelAcquired(Channel channel) throws Exception {
            logger.info("channelAcquired. Channel ID: " + channel.id());
        }

        @Override
        public void channelCreated(Channel ch) throws Exception {

            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
            logger.info("channelCreated. Channel ID: " + ch.id());
            SocketChannel channel = (SocketChannel) ch;
            channel.config().setKeepAlive(true);
            channel.config().setTcpNoDelay(true);

            channel.pipeline()
                    .addLast("codec", new HttpClientCodec())
                    .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                    .addLast(new NettyHttpClientHandler());
        }
    }

    /**
     * test.
     * @param args args
     * @throws URISyntaxException e
     */
    public static void main(String[] args) throws URISyntaxException {
        InetSocketAddress key = new InetSocketAddress("127.0.0.1", 8088);
        NettyHttpClientHandler handler = new NettyHttpClientHandler();
        NettyNioHttpClientFactory.buildFactory(handler, null, null, 1024);

        for (int i = 0; i < 10; i++) {
            ByteBuf echoReq = Unpooled.wrappedBuffer("Hello Netty Http Server".getBytes(CharsetUtil.UTF_8));
            URI uri = new URI("/test");
            String msg = "Hello Netty Http Server" ;
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                    uri.toASCIIString(),
                    echoReq);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
            Future<Channel> f = NettyNioHttpClientFactory.getChannel(key);
            f.addListener((FutureListener<Channel>) f1 -> {
                if (f1.isSuccess()) {
                    Channel ch = f1.getNow();
                    ch.writeAndFlush(request);
                    // Release back to pool
                    NettyNioHttpClientFactory.releaseChannel(key, ch);
                }
            });

        }
    }

}
