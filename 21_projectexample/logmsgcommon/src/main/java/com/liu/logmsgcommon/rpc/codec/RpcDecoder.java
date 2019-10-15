package com.liu.logmsgcommon.rpc.codec;

import com.liu.logmsgcommon.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * RpcDecoder.
 * @Auther: liudongfei
 * @Date: 2019/3/25 23:12
 * @Description:
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genricClass;

    public RpcDecoder(Class<?> genricClass) {
        this.genricClass = genricClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (dataLength < 0) {
            channelHandlerContext.close();
        }
        byte[] bytes = new byte[dataLength];
        byteBuf.readBytes(bytes);
        Object obj = SerializationUtil.deserialize(bytes, genricClass);
        list.add(obj);
    }
}
