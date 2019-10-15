package com.liu.logmsgcommon.netty.tcp;

import com.liu.logmsgcommon.netty.tcp.factory.NettyTcpClientHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * channel连接池handler.
 * @Auther: liudongfei
 * @Date: 2019/7/24 11:46
 * @Description:
 */
public class NettyTcpChannelPoolHandler implements ChannelPoolHandler {
    private static final Logger logger = LoggerFactory.getLogger(NettyTcpChannelPoolHandler.class);

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
                .addLast(new IdleStateHandler(0,
                        4,
                        0,
                        TimeUnit.SECONDS))
                .addLast(new DelimiterBasedFrameDecoder(1024, delimiter))
                .addLast(new StringDecoder())
                .addLast(new StringEncoder())
                .addLast(new NettyTcpClientHandler());
    }
}
