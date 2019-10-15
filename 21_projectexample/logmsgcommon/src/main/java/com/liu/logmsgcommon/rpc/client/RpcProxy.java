package com.liu.logmsgcommon.rpc.client;

import com.liu.logmsgcommon.rpc.codec.RpcRequest;
import com.liu.logmsgcommon.rpc.codec.RpcResponse;
import com.liu.logmsgcommon.rpc.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Rpc Client proxy.
 * @Auther: liudongfei
 * @Date: 2019/3/26 10:41
 * @Description:
 */
public class RpcProxy {
    private static final Logger logger = LoggerFactory.getLogger(RpcProxy.class);

    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * create proxy method.
     * @param interfaceClass proxy interface class
     * @param <T> proxy interface
     * @return
     */
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParamTypes(method.getParameterTypes());
                        request.setParameters(args);
                        String serverAddress = null;
                        if (serviceDiscovery != null) {
                            serverAddress = serviceDiscovery.discovery();
                        }
                        String[] split = serverAddress.split(":");
                        RpcClientAndHandler clientAndHandler =
                                new RpcClientAndHandler(split[0], Integer.parseInt(split[1]));
                        RpcResponse response = clientAndHandler.send(request);

                        if (response.isError()) {
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }
                    }
                });
    }

}
