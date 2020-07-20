package com.liu.java.base.web.netty.https.simple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 简单的netty实现的https 客户端业务处理handler.
 * @Auther: liudongfei
 * @Date: 2019/3/22 16:58
 * @Description:
 */
@ChannelHandler.Sharable
public class MyHttpsClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyHttpsServer.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("client connected to :\t{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("client:\t {} disconnected to server", ctx.channel().remoteAddress() );
    }

    @Override//该方法会不断被触发，在channel的生命周期内不断循环
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse fullHttpResponse) throws Exception {


        ByteBuf buf = fullHttpResponse.content();
        StringBuffer sb = new StringBuffer();
        while (buf.isReadable()) {
            sb.append((char) buf.readByte());
        }
        LOGGER.info("client received:\t{}, {}", sb.toString(), ctx.channel().id());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

