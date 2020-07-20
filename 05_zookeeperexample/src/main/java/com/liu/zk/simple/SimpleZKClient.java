package com.liu.zk.simple;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * 原声的zk客户端.
 * @Auther: liudongfei
 * @Date: 2019/1/16 21:17
 * @Description:
 */
public class SimpleZKClient {
    private static final String connString = "mincdh:2181";
    private static final int sessionTimeout = 2000;
    private ZooKeeper zkClient = null;

    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //收到事件通知后的回调函数
                System.out.println(watchedEvent.getPath() + ":" + watchedEvent.getType());
            }
        });
    }

    /**
     * 创建持久化节点并设定值.
     * @throws KeeperException e
     * @throws InterruptedException e
     */
    @Test
    public void createZnode() throws KeeperException, InterruptedException {
        zkClient.create("/zktest", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * 获取节点的数据并监控，如果数据改变，就会进行回调.
     * @throws KeeperException e
     * @throws InterruptedException e
     */
    @Test
    public void getData() throws KeeperException, InterruptedException {
        byte[] data = zkClient.getData("/zktest", true, null);
        System.out.println(new String(data));
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 判断节点是否存在.
     * @throws KeeperException e
     * @throws InterruptedException e
     */
    @Test
    public void existzkNode() throws KeeperException, InterruptedException {
        Stat stat = zkClient.exists("/zktest", false);
        System.out.println(stat == null ? "not exist" : " exist");
    }

    /**
     * 获取指定节点的子节点.
     * @throws KeeperException e
     * @throws InterruptedException e
     */
    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        List<String> childrens = zkClient.getChildren("/", true);
        for (String child : childrens) {
            System.out.println(child);
        }
    }

    /**
     * 为指定节点赋值.
     * @throws KeeperException e
     * @throws InterruptedException e
     */
    @Test
    public void setData() throws KeeperException, InterruptedException {
        zkClient.setData("/zktest", "test2".getBytes(), -1);
    }

    /**
     * 删除指定节点.
     * @throws KeeperException e
     * @throws InterruptedException e
     */
    @Test
    public void deletezkNode() throws KeeperException, InterruptedException {
        zkClient.delete("/zktest", -1);
    }
}
