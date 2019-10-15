package com.liu.logmsgcommon.netty.http;

import com.liu.logmsgcommon.netty.http.factory.NettyHttpClientHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/7/30 14:47
 * @Description:
 */
public class NettyHttpChannelPoolHandler implements ChannelPoolHandler {
    private static final Logger logger = LoggerFactory.getLogger(NettyHttpChannelPoolHandler.class);

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
