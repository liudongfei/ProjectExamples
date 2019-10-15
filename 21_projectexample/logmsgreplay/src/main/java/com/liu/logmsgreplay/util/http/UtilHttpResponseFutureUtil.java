package com.liu.logmsgreplay.util.http;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/8 16:19
 * @Description:
 */
public class UtilHttpResponseFutureUtil {
    private static final AttributeKey<Object> DEFAULT_ATTRIBUTE = AttributeKey.valueOf("nettyHttpResponse");

    private static final AttributeKey<Object> ROUTE_ATTRIBUTE = AttributeKey.valueOf("route");

    private static final AttributeKey<Object> FORCE_CONNECT_ATTRIBUTE = AttributeKey.valueOf("forceConnect");

    /**
     * .
     * @param channel channel
     * @param forceConnect forceConnect
     */
    public static void attributeForceConnect(Channel channel, boolean forceConnect) {
        if (forceConnect) {
            channel.attr(FORCE_CONNECT_ATTRIBUTE).set(true);
        }
    }

    public static void attributeResponse(Channel channel, UtilHttpResponseFuture responseFuture) {
        channel.attr(DEFAULT_ATTRIBUTE).set(responseFuture);
        responseFuture.setChannel(channel);
    }

    public static void attributeRoute(Channel channel, InetSocketAddress route) {
        channel.attr(ROUTE_ATTRIBUTE).set(route);
    }

    public static UtilHttpResponseFuture getResponse(Channel channel) {
        return (UtilHttpResponseFuture) channel.attr(DEFAULT_ATTRIBUTE).get();
    }

    public static InetSocketAddress getRoute(Channel channel) {
        return (InetSocketAddress) channel.attr(ROUTE_ATTRIBUTE).get();
    }

    /**
     * .
     * @param channel channel
     * @return
     */
    public static boolean getForceConnect(Channel channel) {
        Object forceConnect = channel.attr(FORCE_CONNECT_ATTRIBUTE).get();
        if (null == forceConnect) {
            return false;
        }
        return true;
    }

    /**
     * .
     * @param channel channel
     * @param pendingResponse pendingResponse
     */
    public static void setPendingResponse(Channel channel, HttpResponse pendingResponse) {
        UtilHttpResponseFuture responseFuture = getResponse(channel);
        UtilHttpResponseBuilder responseBuilder = new UtilHttpResponseBuilder();
        responseBuilder.setSuccess(true);
        responseBuilder.setPendingResponse(pendingResponse);
        responseFuture.setResponseBuilder(responseBuilder);
    }

    /**
     * .
     * @param channel channel
     * @return
     */
    public static boolean headerContainConnectionClose(Channel channel) {
        UtilHttpResponseFuture responseFuture = getResponse(channel);
        return HttpHeaders.Values.CLOSE.equalsIgnoreCase(responseFuture.getResponseBuilder()
                .getPendingResponse().headers().get(HttpHeaders.Names.CONNECTION));
    }

    /**
     * .
     * @param channel channel
     * @param httpContent httpContent
     */
    public static void setPendingContent(Channel channel, HttpContent httpContent) {
        UtilHttpResponseFuture responseFuture = getResponse(channel);
        UtilHttpResponseBuilder responseBuilder = responseFuture.getResponseBuilder();
        responseBuilder.addContent(httpContent.content().retain());
    }

    /**
     * .
     * @param channel channel
     * @return
     */
    public static boolean done(Channel channel) {
        UtilHttpResponseFuture responseFuture = getResponse(channel);
        if (null != responseFuture) {
            return responseFuture.done();
        }

        return true;
    }

    public static boolean cancel(Channel channel, Throwable cause) {
        UtilHttpResponseFuture responseFuture = getResponse(channel);
        return responseFuture.cancel(cause);
    }
}
