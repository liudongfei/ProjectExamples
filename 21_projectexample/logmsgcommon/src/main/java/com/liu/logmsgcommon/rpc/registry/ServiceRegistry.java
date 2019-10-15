package com.liu.logmsgcommon.rpc.registry;

import com.liu.logmsgcommon.util.ConstantUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc service registry class.
 * @Auther: liudongfei
 * @Date: 2019/3/26 09:37
 * @Description:
 */
public class ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    private final String zookeeperAddress;

    private CuratorFramework curatorFramework;

    public ServiceRegistry(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
        init();
    }

    /**
     * zk client init.
     */
    public void init() {
        curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(zookeeperAddress)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .namespace(ConstantUtil.ZK_RPC_ROOT_PATH)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();
    }

    /**
     * regist method.
     * @param serverAddressIp server address ip
     * @param serverAddressPort server address port
     * @throws Exception exception
     */
    public void register(String serverAddressIp, int serverAddressPort) throws Exception {
        String content = serverAddressIp + ":" + serverAddressPort;
        curatorFramework.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/server", content.getBytes());
        logger.info("register server: {} to zookeeper", content);
    }
}
