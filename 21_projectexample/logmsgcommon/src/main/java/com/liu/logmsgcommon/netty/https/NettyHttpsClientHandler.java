package com.liu.logmsgcommon.netty.https;

import com.liu.logmsgcommon.netty.tcp.factory.NettyTcpClientHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http客户端的业务处理类.
 * @Auther: liudongfei
 * @Date: 2019/7/30 14:51
 * @Description:
 */
@ChannelHandler.Sharable
public class NettyHttpsClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyTcpClientHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client:{} success connected to server:{}",
                ctx.channel().localAddress(), ctx.channel().remoteAddress());
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
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        String byteBuf = msg.content().toString(CharsetUtil.UTF_8);
        logger.info("client:\t{} received message:\t{}", ctx.channel().localAddress(), byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
