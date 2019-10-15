package com.liu.server;

import com.liu.cdh.common.RpcRequest;
import com.liu.cdh.common.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * rpc server handler.
 * @Auther: liudongfei
 * @Date: 2019/3/25 23:09
 * @Description:
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

    private Map<String, Object> implMap;

    public RpcServerHandler(Map<String, Object> implMap) {
        this.implMap = implMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = new RpcResponse();
        logger.info("rpc server received rpc request: {}", rpcRequest.getRequestId());
        response.setRequestId(rpcRequest.getRequestId());
        try {
            String className = rpcRequest.getClassName();
            Object obj = implMap.get(className);
            Method method = obj.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object res = method.invoke(obj, rpcRequest.getParameters());
            response.setResult(res);
        } catch (Exception e) {
            response.setError(e);
        }
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        logger.info("rpc server sended rpc response:{}", response.getRequestId());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

