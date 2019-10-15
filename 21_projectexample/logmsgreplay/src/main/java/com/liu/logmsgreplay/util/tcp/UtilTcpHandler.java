package com.liu.logmsgreplay.util.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * simple netty tcp handler.
 * @Auther: liudongfei
 * @Date: 2019/4/8 10:06
 * @Description:
 */
public class UtilTcpHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilTcpHandler.class);
    private static final ByteBuf HEARTBEATSEQUENCE = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                ctx.channel().writeAndFlush(HEARTBEATSEQUENCE.duplicate());
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("client:\t{} succeed connect to server:\t{}",
                ctx.channel().localAddress(), ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        LOGGER.info("Client received: " + byteBuf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.warn("client:\t{} loss connect from server:\t{}",
                ctx.channel().localAddress(), ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        LOGGER.error("client:\t{} get error from server:\t{}, trace:\t{}",
                ctx.channel().localAddress(), ctx.channel().remoteAddress(), cause.toString());
        ctx.close();
    }
}
