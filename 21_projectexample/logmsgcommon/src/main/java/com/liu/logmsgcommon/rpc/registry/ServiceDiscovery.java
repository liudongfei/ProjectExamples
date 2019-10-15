package com.liu.logmsgcommon.rpc.registry;

import com.liu.logmsgcommon.util.ConstantUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * rpc service discovery class.
 * @Auther: liudongfei
 * @Date: 2019/3/26 09:37
 * @Description:
 */
public class ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private String zookeeperAddress;
    private volatile List<String> serverList = new ArrayList<>();
    private CuratorFramework curatorFramework;

    public ServiceDiscovery(String zookeeperAddress) throws Exception {
        this.zookeeperAddress = zookeeperAddress;
        init();
    }

    /**
     * zk client init.
     * @throws Exception exception
     */
    public void init() throws Exception {
        curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(zookeeperAddress)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .namespace(ConstantUtil.ZK_RPC_ROOT_PATH)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();
        getServerList();
        TreeCache treeCache = new TreeCache(curatorFramework, "/");
        treeCache.start();
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                if (treeCacheEvent.getData() != null) {
                    serverList.clear();
                    getServerList();
                }
            }
        });
    }
    private void getServerList() throws Exception {
        List<String> strings = curatorFramework.getChildren().forPath("/");
        for (String path : strings) {
            String serverStr = new String(curatorFramework.getData().forPath("/" + path));
            serverList.add(serverStr);
        }
        LOGGER.info("serverList updated: {}", serverList);
    }

    /**
     * discovery method.
     * @return
     */
    public String discovery() {
        String address = null;
        int size = serverList.size();
        LOGGER.info("discovery the number of available rpc server is: {}", size);
        if (size > 0) {
            if (size == 1) {
                address =  serverList.get(0);
            } else {
                address = serverList.get(ThreadLocalRandom.current().nextInt(size));
            }
        }
        LOGGER.info("get rpc server: {} from zk", address);
        return address;
    }

}
