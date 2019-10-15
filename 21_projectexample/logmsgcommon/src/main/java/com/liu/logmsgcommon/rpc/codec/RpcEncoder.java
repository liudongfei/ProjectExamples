package com.liu.logmsgcommon.rpc.codec;

import com.liu.logmsgcommon.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Rpc encoder.
 * @Auther: liudongfei
 * @Date: 2019/3/25 23:12
 * @Description:
 */
public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genricClass;

    public RpcEncoder(Class<?> genricClass) {
        this.genricClass = genricClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) {
        if (genricClass.isInstance(obj)) {
            byte[] bytes = SerializationUtil.serialize(obj);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
    }
}
