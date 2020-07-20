package com.liu.java.base.web.netty.http.simple;

import com.liu.java.base.web.netty.https.simple.MyHttpsServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于netty实现的http客户端业务逻辑类.
 * @Auther: liudongfei
 * @Date: 2019/3/22 16:58
 * @Description:
 */
@ChannelHandler.Sharable
public class MyHttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyHttpsServerHandler.class);

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
        System.out.println("client received:" + sb.toString() + "," + ctx.channel().id());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
