package com.liu.zk.upgrade;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * zk集成工具的使用.
 * @Auther: liudongfei
 * @Date: 2019/3/14 17:14
 * @Description:
 */
public class MyCuratorZKClient {
    /**
     * main.
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString("mincdh:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .namespace("curator")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorFramework.start();

        ExecutorService pool = Executors.newFixedThreadPool(2);
        /**
         * NodeCache用来观察ZNode自身，如果Znode节点本身倍创建，更新或者删除，会触发监听器.
         */
        final NodeCache nodeCache = new NodeCache(curatorFramework, "/node2", false);
        nodeCache.start(true);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("node data is changed, new data:\t"
                        + new String(nodeCache.getCurrentData().getData()));
            }
        }, pool);
        /**
         * PathChildrenCache用来观察ZNode子节点并缓存状态，如果ZNode的子节点被创建、更新或删除，会触发监听器.
         */
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, "/", false);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED:" + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED:" + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED:" + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }
        }, pool);
        /**
         * 可以看作时上两种的合体，观察所有节点的所有数据.
         */
        TreeCache treeCache = new TreeCache(curatorFramework, "/");
        treeCache.start();
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                if (treeCacheEvent.getData() != null) {
                    switch (treeCacheEvent.getType()) {
                        case NODE_ADDED:
                            System.out.println("节点增加， path=" + treeCacheEvent.getData().getPath() );
                            break;
                        case NODE_UPDATED:
                            System.out.println("节点更新，path=" + treeCacheEvent.getData().getPath());
                            break;
                        case NODE_REMOVED:
                            System.out.println("节点删除，path=" + treeCacheEvent.getData().getPath());
                            break;
                        default:
                            break;
                    }
                } else {
                    System.out.println("节点数据为空，path=" + treeCacheEvent.getData().getPath());
                }
            }
        });
        curatorFramework.setData().forPath("/", "world".getBytes());
        curatorFramework.create().forPath("/node2", "test".getBytes());
        List<String> strings = curatorFramework.getChildren().forPath("/");
        System.out.println(strings);
        //curatorFramework.delete().deletingChildrenIfNeeded().forPath("/curator");
        Thread.sleep(10 * 1000);
        pool.shutdown();
        curatorFramework.close();

    }
}
