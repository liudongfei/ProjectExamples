package com.liu.java.base.web.netty.tcp.upgrade;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * simple netty tcp server handler.
 * @Auther: liudongfei
 * @Date: 2019/3/22 14:11
 * @Description:
 */
@ChannelHandler.Sharable
public class MyTcpServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyTcpServerHandler.class);

    @Override//每个信息入站时都会调用
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        LOGGER.info("Server received:\t {}", in.toString(CharsetUtil.UTF_8));
        //ctx.writeAndFlush(in);
    }

    @Override//通知处理器最后的channelread时当前批处理的最后一条消息时调用
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //每次写入数据后空的buf
        //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("received connect from client:\t{}", ctx.channel().remoteAddress());
        MyTcpServer.getChannelQueue().add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("disconnect from client:\t{}", ctx.channel().remoteAddress());
        MyTcpServer.getChannelQueue().remove(ctx.channel());
    }

    @Override//读操作时，捕获到异常时调用
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
