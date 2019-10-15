package com.liu.client;

import com.liu.cdh.common.RpcDecoder;
import com.liu.cdh.common.RpcEncoder;
import com.liu.cdh.common.RpcRequest;
import com.liu.cdh.common.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RpcClient handler.
 * @Auther: liudongfei
 * @Date: 2019/3/26 10:41
 * @Description:
 */
public class RpcClientAndHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientAndHandler.class);

    private String serverIp;
    private int serverPort;
    private Object obj = new Object();
    private RpcResponse response;

    public RpcClientAndHandler(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    /**
     * rpc client send request method.
     * @param request request
     * @return response
     * @throws InterruptedException exception
     */
    public RpcResponse send(RpcRequest request) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RpcEncoder(RpcRequest.class))
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    .addLast(RpcClientAndHandler.this);
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.connect(serverIp, serverPort).sync();
            future.channel().writeAndFlush(request).sync();
            LOGGER.info("send request:{} to rpc server", request.getRequestId());
            synchronized (obj) {
                obj.wait();
            }
            if (response != null) {
                future.channel().closeFuture().sync();
            }
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        synchronized (obj) {
            LOGGER.info("received request: {}", rpcResponse.getRequestId());
            this.response = rpcResponse;
            synchronized (obj) {
                obj.notifyAll();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("connecting with rpc server occur error", cause);
        ctx.close();
    }
}
