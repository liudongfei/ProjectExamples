package com.liu.java.base.web.netty.tcp.simple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * simple netty tcp client handler.
 * @Auther: liudongfei
 * @Date: 2019/3/22 14:10
 * @Description:
 */
public class MyTcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyTcpClientHandler.class);
    private static final char[] HEXES = {
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f'
    };

    @Override//服务器的连接建立时调用
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("client:{} success connected to server:\t{}", ctx.channel().localAddress(),
                ctx.channel().remoteAddress());
    }

    @Override//从服务器收到数据时调用
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        //byte[] bytes = ByteBufUtil.getBytes(byteBuf, 0, 2);
        //System.out.println(Integer.parseInt(bytes2Hex(bytes), 16));

        LOGGER.info("Client received:\t{}", byteBuf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("client:{} disconnected to server:\t{}", ctx.channel().localAddress(),
                ctx.channel().remoteAddress());
    }

    @Override//捕获异常时调用
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * byte数组 转换成16进制小写字符串.
     */
    public static String bytes2Hex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        StringBuilder hex = new StringBuilder();

        for (byte b : bytes) {
            hex.append(HEXES[(b >> 4) & 0x0F]);
            hex.append(HEXES[b & 0x0F]);
        }

        return hex.toString();
    }
}
