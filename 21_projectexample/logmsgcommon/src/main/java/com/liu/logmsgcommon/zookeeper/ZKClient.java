package com.liu.logmsgcommon.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * zkclient.
 * @Auther: liudongfei
 * @Date: 2019/3/18 15:49
 * @Description:
 */
public class ZKClient {
    private CuratorFramework framework;

    /**
     * constructor.
     * @param zkAddress zkAddress
     * @param rootPath rootPath
     */
    public ZKClient(String zkAddress, String rootPath) {
        framework = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace(rootPath)
                .build();
        framework.start();
    }

    public CuratorFramework getFramework() {
        return framework;
    }

    public void close() {
        framework.close();
    }
}
