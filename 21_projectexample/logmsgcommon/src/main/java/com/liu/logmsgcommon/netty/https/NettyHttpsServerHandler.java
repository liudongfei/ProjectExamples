package com.liu.logmsgcommon.netty.https;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http服务端业务处理类.
 * @Auther: liudongfei
 * @Date: 2019/7/30 14:34
 * @Description:
 */
@ChannelHandler.Sharable
public class NettyHttpsServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyHttpsServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("received connect from client:\t{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String in = msg.content().toString(CharsetUtil.UTF_8);
        logger.info("Server received: {} from client:\t", in, ctx.channel().remoteAddress());
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer("Welcome to Netty httpserver".getBytes()));

        HttpHeaders heads = response.headers();
        AsciiString contentType = HttpHeaderValues.TEXT_PLAIN;
        heads.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
        heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()); // 3
        heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        //ctx.writeAndFlush(Unpooled.copiedBuffer("Welcome to Netty httpserver".getBytes()));
        ctx.writeAndFlush(response);
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
