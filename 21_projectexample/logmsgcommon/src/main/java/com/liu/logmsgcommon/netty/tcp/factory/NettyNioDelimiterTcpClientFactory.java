package com.liu.logmsgcommon.netty.tcp.factory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * tcp客户端工厂类.
 *
 * @Auther: liudongfei
 * @Date: 2019/7/24 16:28
 * @Description:
 */
public class NettyNioDelimiterTcpClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(NettyNioDelimiterTcpClientFactory.class);

    private int defaultPerPoolSize = 2;
    private int maxPerPoolSize;
    private int threadNum;
    private int defaultThreadNum = 5;
    private String delimiter = "\n";
    private Integer maxMessageLen = 1024;

    private EventLoopGroup group = null;
    private ChannelPoolMap<InetSocketAddress, SimpleChannelPool> channelPoolMap;

    private static NettyNioDelimiterTcpClientFactory instance;

    /**
     * 工厂类的初始化.
     * @param handler 业务处理handler
     * @param maxPerPoolSize 每个连接池的最大连接数
     * @param threadNum 处理线程的数量
     * @param delimiter 分隔符
     * @param maxMessageLen 消息最大长度
     */
    public static NettyNioDelimiterTcpClientFactory buildFactory(ChannelInboundHandler handler,
            Integer maxPerPoolSize, Integer threadNum, String delimiter, Integer maxMessageLen) {
        synchronized (NettyNioDelimiterTcpClientFactory.class) {
            if (instance == null) {
                NettyNioDelimiterTcpClientFactory factory = new NettyNioDelimiterTcpClientFactory();
                factory.init(handler, maxPerPoolSize, threadNum, delimiter, maxMessageLen);
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
     * @param delimiter 分隔符
     * @param maxMessageLen 消息最大长度
     */
    private void init(ChannelInboundHandler handler, Integer maxPerPoolSize,
                      Integer threadNum, String delimiter, Integer maxMessageLen) {
        if (handler == null) {
            throw new NullPointerException("Param ChannelInboundHandler is null");
        } if (maxPerPoolSize == null) {
            maxPerPoolSize = defaultPerPoolSize;
        } if (threadNum == null) {
            threadNum = defaultThreadNum;
        } if (delimiter == null) {
            delimiter = "\n";
        } if (maxMessageLen == null) {
            maxMessageLen = 1024;
        }

        this.maxPerPoolSize = maxPerPoolSize;
        this.threadNum = threadNum;
        this.group = new NioEventLoopGroup(defaultThreadNum);

        ChannelPoolHandler poolHandler =
                new NettyNioDelimiterTcpClientFactory.NettyTcpChannelPoolHandler(handler, delimiter, maxMessageLen);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

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

    public String getDelimiter() {
        return delimiter;
    }

    public Integer getMaxMessageLen() {
        return maxMessageLen;
    }

    class NettyTcpChannelPoolHandler implements ChannelPoolHandler {
        ChannelInboundHandler handler;
        ByteBuf delimiter;
        Integer maxMessageLen;

        public NettyTcpChannelPoolHandler(ChannelInboundHandler handler, String delimiter, Integer maxMessageLen) {
            this.handler = handler;
            this.delimiter = Unpooled.copiedBuffer(delimiter.getBytes());
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


            logger.info("channelCreated. Channel ID: " + ch.id());
            SocketChannel channel = (SocketChannel) ch;
            channel.config().setKeepAlive(true);
            channel.config().setTcpNoDelay(true);

            channel.pipeline()
                    .addLast(new IdleStateHandler(0,
                            4,
                            0,
                            TimeUnit.SECONDS))
                    .addLast(new DelimiterBasedFrameDecoder(maxMessageLen, delimiter))
                    .addLast(new StringDecoder())
                    .addLast(new StringEncoder())
                    .addLast(handler);
        }
    }

    /**
     * 使用样例.
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        ByteBuf echoReq = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("Hello Netty.$_", CharsetUtil.UTF_8));
        InetSocketAddress key = new InetSocketAddress("127.0.0.1", 8890);
        NettyTcpClientHandler handler = new NettyTcpClientHandler();
        NettyNioDelimiterTcpClientFactory.buildFactory(handler,
                null, null, "$_", 1024);

        for (int i = 0; i < 10; i++) {
            Future<Channel> f = NettyNioDelimiterTcpClientFactory.getChannel(key);
            f.addListener((FutureListener<Channel>) f1 -> {
                if (f1.isSuccess()) {
                    Channel ch = f1.getNow();
                    ch.writeAndFlush(echoReq);
                    NettyNioDelimiterTcpClientFactory.releaseChannel(key, ch);
                }
            });
        }

        //Thread.sleep(5000);
        //NettyNioDelimiterTcpClientFactory.close();
    }

}
