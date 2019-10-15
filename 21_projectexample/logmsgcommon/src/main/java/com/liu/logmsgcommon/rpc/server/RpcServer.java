package com.liu.logmsgcommon.rpc.server;

import com.liu.logmsgcommon.rpc.codec.RpcDecoder;
import com.liu.logmsgcommon.rpc.codec.RpcEncoder;
import com.liu.logmsgcommon.rpc.codec.RpcRequest;
import com.liu.logmsgcommon.rpc.codec.RpcResponse;
import com.liu.logmsgcommon.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * rpc server class.
 * @Auther: liudongfei
 * @Date: 2019/3/25 23:04
 * @Description:
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private Map<String, Object> implMap = new HashMap<>();
    private ServiceRegistry serviceRegistry;
    private String serverAddressIp;
    private int serverAddressPort;

    /**
     * rpc server constructor.
     * @param serviceRegistry serviceRegistry
     * @param serverAddressIp server addressip
     * @param serverAddressPort server addressport
     */
    public RpcServer(ServiceRegistry serviceRegistry, String serverAddressIp, int serverAddressPort) {
        this.serviceRegistry = serviceRegistry;
        this.serverAddressIp = serverAddressIp;
        this.serverAddressPort = serverAddressPort;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group, worker)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(serverAddressIp, serverAddressPort))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcDecoder(RpcRequest.class))
                                .addLast(new RpcEncoder(RpcResponse.class))
                                .addLast(new RpcServerHandler(implMap));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = bootstrap.bind().sync();
        LOGGER.info("server started on: {}", future.channel().localAddress());
        if (serviceRegistry != null) {
            serviceRegistry.register(serverAddressIp, serverAddressPort);
        }
        future.channel().closeFuture().sync();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);
        LOGGER.info("server geted {} pair interface impl map", beansWithAnnotation.size());
        for (Object obj : beansWithAnnotation.values()) {
            String value = obj.getClass().getAnnotation(RpcService.class).value().getName();
            implMap.put(value, obj);
        }
    }
}
