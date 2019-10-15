package com.liu.logmsgreplay.util.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/8 16:15
 * @Description:
 */
public class UtilHttpChannelPoolHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger logger = LoggerFactory.getLogger(UtilHttpChannelPoolHandler.class);

    private UtilHttpChannelPool channelPool;

    /**
     * .
     * @param channelPool channelPool
     */
    public UtilHttpChannelPoolHandler(UtilHttpChannelPool channelPool) {
        super();
        this.channelPool = channelPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse headers = (HttpResponse) msg;
            UtilHttpResponseFutureUtil.setPendingResponse(ctx.channel(), headers);
        }
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            UtilHttpResponseFutureUtil.setPendingContent(ctx.channel(), httpContent);
            if (httpContent instanceof LastHttpContent) {
                boolean connectionClose = UtilHttpResponseFutureUtil.headerContainConnectionClose(ctx.channel());
                UtilHttpResponseFutureUtil.done(ctx.channel());
                //the maxKeepAliveRequests config will cause server close the channel,
                // and return 'Connection: close' in headers
                if (!connectionClose) {
                    channelPool.returnChannel(ctx.channel());
                }
            }
        }
    }

    /**
     * .
     * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(
     * io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            logger.warn("remove idle channel: {}" , ctx.channel());
            ctx.channel().close();
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    /**.
     * @param channelPool
     *            the channelPool to set
     */
    public void setChannelPool(UtilHttpChannelPool channelPool) {
        this.channelPool = channelPool;
    }
}
