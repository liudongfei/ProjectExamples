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
 * test system tcp handler.
 * @Auther: liudongfei
 * @Date: 2019/4/7 15:53
 * @Description:
 */
public class TestSystemTcpServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TestSystemTcpServerHandler.class);
    private int lossConnectTime = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                lossConnectTime++;
                logger.info("5 seconds not received message from client:\t{}", ctx.channel().remoteAddress());
                if (lossConnectTime > 2) {
                    logger.info("close this inactive channel:\t{} !", ctx.channel().remoteAddress());
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("received connect from client:\t{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        logger.info("Server received: {} from client:\t", in.toString(CharsetUtil.UTF_8),
                ctx.channel().remoteAddress());
        ctx.writeAndFlush(Unpooled.copiedBuffer(in));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}