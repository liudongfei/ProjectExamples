package com.liu.logmsgcommon.netty.tcp.factory;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * tcp服务端的handler.
 * @Auther: liudongfei
 * @Date: 2019/7/24 11:37
 * @Description:
 */
@ChannelHandler.Sharable
public class NettyTcpServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyTcpServerHandler.class);
    //static AtomicInteger count = new AtomicInteger(1);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("received connect from client:\t{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String in = (String) msg;
        logger.info("Server received: {} from client:\t", in,
                ctx.channel().remoteAddress());


        ctx.writeAndFlush(Unpooled.copiedBuffer("Welcome to Netty.$_".getBytes()));
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
