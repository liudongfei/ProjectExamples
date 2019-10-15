package com.liu.logmsgreplay.netty.tcp;

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
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/9 15:48
 * @Description:
 */
public class UtilTcpClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(UtilTcpClientHandler.class);

    private static final ByteBuf HEARTBEATSEQUENCE = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("HeartBeat", CharsetUtil.UTF_8));

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client {} success connected to server {}",
                ctx.channel().localAddress(), ctx.channel().remoteAddress());
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client {} disconnected from server {}",
                ctx.channel().localAddress(), ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        logger.info("client:\t{} received message:\t{}", ctx.channel().localAddress(),
                byteBuf.toString(CharsetUtil.UTF_8));
        // TODO
        ctx.channel().remoteAddress();
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
