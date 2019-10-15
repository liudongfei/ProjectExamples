package com.liu.cdh.common.rpc;

import com.liu.cdh.common.rpc.impl.People;
import com.liu.cdh.common.rpc.interf.ActionInterf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * hadoopRPC框架服务端.
 * @Auther: liudongfei
 * @Date: 2019/3/29 10:00
 * @Description:
 */
public class MyHadoopRpcServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyHadoopRpcServer.class);

    /**
     * main.
     * @param args args
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        RPC.Builder builder = new RPC.Builder(new Configuration());
        RPC.Server server = builder.setBindAddress("localhost")
                .setPort(8899)
                .setProtocol(ActionInterf.class)
                .setInstance(new People())
                .build();
        LOGGER.info("hadoop rpc server started and listened on:\t{}", server.getListenerAddress());
        server.start();
    }
}
