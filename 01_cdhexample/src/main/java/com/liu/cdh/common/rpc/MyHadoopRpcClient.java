package com.liu.cdh.common.rpc;

import com.liu.cdh.common.rpc.interf.ActionInterf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * hadoop RPC框架客户端.
 * @Auther: liudongfei
 * @Date: 2019/3/29 10:05
 * @Description:
 */
public class MyHadoopRpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyHadoopRpcClient.class);

    /**
     * main.
     * @param args args
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        ActionInterf proxy = RPC.getProxy(ActionInterf.class, 10L,
                new InetSocketAddress("localhost", 8899), new Configuration());
        LOGGER.info("client sending hadoop rpc request ...");
        String res = proxy.hello("liu");
        LOGGER.info("client get hadoop rpc response:\t{}", res);

    }
}
