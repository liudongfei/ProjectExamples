package com.liu.logmsgcommon.netty.tcp.factory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * tcp客户端handler.
 * @Auther: liudongfei
 * @Date: 2019/7/24 11:49
 * @Description:
 */
@ChannelHandler.Sharable
public class NettyTcpClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyTcpClientHandler.class);

    private static final ByteBuf HEARTBEATSEQUENCE = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("HeartBeat.$_", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                logger.info("client:{}  send heartbeat to server:{}", ctx.channel().localAddress(),
                        ctx.channel().remoteAddress());
                ctx.channel().writeAndFlush(HEARTBEATSEQUENCE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client:{} success connected to server:{}", ctx.channel().localAddress(),
                ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client:{} success disconnected to server:{}", ctx.channel().localAddress(),
                ctx.channel().remoteAddress());
        // TODO
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String byteBuf = (String) msg;
        logger.info("client:\t{} received message:\t{}", ctx.channel().localAddress(), byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
