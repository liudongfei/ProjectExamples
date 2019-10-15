package com.liu.logmsgreplay.util.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/9 11:12
 * @Description:
 */
public class UtilTcpResPacketFutureUtil {
    private static final AttributeKey<Object> DEFAULT_ATTRIBUTE = AttributeKey.valueOf("nettyTcpResPacket");

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

    public static void attributeResponse(Channel channel, UtilTcpResPacketFuture resPacketFuture) {
        channel.attr(DEFAULT_ATTRIBUTE).set(resPacketFuture);
        resPacketFuture.setChannel(channel);
    }

    public static void attributeRoute(Channel channel, InetSocketAddress route) {
        channel.attr(ROUTE_ATTRIBUTE).set(route);
    }

    public static UtilTcpResPacketFuture getResponse(Channel channel) {
        return (UtilTcpResPacketFuture) channel.attr(DEFAULT_ATTRIBUTE).get();
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
     * @param content content
     */
    public static void setPendingContent(Channel channel, ByteBuf content) {
        UtilTcpResPacketFuture resPacketFuture = getResponse(channel);
        UtilTcpResPacketBuilder resPacketBuilder = new UtilTcpResPacketBuilder();
        resPacketBuilder.setSuccess(true);
        resPacketBuilder.addContent(content.retain());
        resPacketFuture.setResPacketBuilder(resPacketBuilder);
    }

    /**
     * .
     * @param channel channel
     * @return
     */
    public static boolean done(Channel channel) {
        UtilTcpResPacketFuture resPacketFuture = getResponse(channel);
        if (null != resPacketFuture) {
            return resPacketFuture.done();
        }

        return true;
    }

    public static boolean cancel(Channel channel, Throwable cause) {
        UtilTcpResPacketFuture resPacketFuture = getResponse(channel);
        return resPacketFuture.cancel(cause);
    }
}
