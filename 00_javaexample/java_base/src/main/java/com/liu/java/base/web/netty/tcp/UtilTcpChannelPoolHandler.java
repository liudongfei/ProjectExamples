package com.liu.java.base.web.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/8 18:21
 * @Description:
 */
public class UtilTcpChannelPoolHandler extends SimpleChannelInboundHandler {
    private static final Logger logger = LoggerFactory.getLogger(UtilTcpChannelPoolHandler.class);

    private UtilTcpChannelPool tcpChannelPool;
    private static final ByteBuf HEARTBEATSEQUENCE = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("HeartBeat", CharsetUtil.UTF_8));

    public UtilTcpChannelPoolHandler(UtilTcpChannelPool tcpChannelPool) {
        this.tcpChannelPool = tcpChannelPool;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                ctx.channel().writeAndFlush(HEARTBEATSEQUENCE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object byteBuffer) throws Exception {
        ByteBuf msg = (ByteBuf) byteBuffer;
        logger.info("0Client received:\t{}", msg.toString(CharsetUtil.UTF_8));
        UtilTcpResPacketFutureUtil.setPendingContent(channelHandlerContext.channel(), msg);
        UtilTcpResPacketFutureUtil.done(channelHandlerContext.channel());
        tcpChannelPool.returnChannel(channelHandlerContext.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object byteBuffer) throws Exception {
        ByteBuf msg = (ByteBuf) byteBuffer;
        logger.info("Client received:\t{}", msg.toString(CharsetUtil.UTF_8));
        UtilTcpResPacketFutureUtil.setPendingContent(channelHandlerContext.channel(), msg);
        UtilTcpResPacketFutureUtil.done(channelHandlerContext.channel());
        tcpChannelPool.returnChannel(channelHandlerContext.channel());
    }
}
