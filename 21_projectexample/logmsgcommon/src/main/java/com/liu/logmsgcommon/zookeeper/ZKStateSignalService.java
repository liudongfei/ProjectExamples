package com.liu.logmsgcommon.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * signalservice impl zksignalservice.
 * @Auther: liudongfei
 * @Date: 2019/3/18 15:43
 * @Description:
 */
public class ZKStateSignalService implements SignalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZKStateSignalService.class);
    private ZKClient zkClient;
    private final ConcurrentMap<String, TreeCache> treeCaches = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<SignalListener>> listenerForMap = new ConcurrentHashMap<>();
    private static volatile ZKStateSignalService instance;
    private static volatile boolean initialized = false;

    /**
     * get zksignalservice sigle mode.
     * @param zkAddress zkAddress
     * @param rootPath rootPath
     * @return
     */
    public static ZKStateSignalService getInstance(String zkAddress, String rootPath) {
        synchronized (ZKStateSignalService.class) {
            if (instance == null) {
                ZKStateSignalService zKsignalService = new ZKStateSignalService();
                zKsignalService.initialize(zkAddress, rootPath);
                instance = zKsignalService;
            }
        }
        return instance;
    }

    private void initialize(String zkAddress, String rootPath) {
        zkClient = new ZKClient(zkAddress, rootPath);
        initialized = true;
    }

    @Override
    public boolean registerSignalListener(String path, SignalListener signalListener) {
        if (!initialized) {
            LOGGER.warn("zookeeper client not initialized");
            return false;
        }
        List<SignalListener> listenerList = listenerForMap.get(path);
        if (listenerList == null) {
            ArrayList<SignalListener> signalListeners = new ArrayList<>();
            listenerForMap.putIfAbsent(path, signalListeners);
            listenerList = signalListeners;
        }
        synchronized (listenerList) {
            listenerList.add(signalListener);
        }
        if (!treeCaches.containsKey(path)) {
            TreeCache treeCache = new TreeCache(zkClient.getFramework(), path);
            TreeCache cache = treeCaches.putIfAbsent(path, treeCache);
            if (cache == null) {
                try {
                    treeCache.start();
                    treeCache.getListenable().addListener(new TreeCacheListener() {
                        @Override
                        public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent)
                                throws Exception {
                            if (treeCacheEvent.getData() != null) {
                                try {
                                    String nodePath = treeCacheEvent.getData().getPath();
                                    String value = new String(treeCacheEvent.getData().getData(), "utf-8");
                                    LOGGER.info("signal listener: {} received events value={}", nodePath, value);
                                    List<SignalListener> signalListeners = listenerForMap.get(nodePath);
                                    if (signalListeners != null) {
                                        for (SignalListener signalListener: signalListeners) {
                                            signalListener.onSignal(nodePath, value);
                                        }
                                    }
                                } catch (Exception e) {
                                    LOGGER.error("error occur receiving cache child event: ", e);
                                }

                            }
                        }
                    });
                    return true;
                } catch (Exception e) {
                    LOGGER.error("error registering tree cache for signal listener", e);
                }
            }

        } else {
            LOGGER.warn("Tree cache is already registered for path={}", path);
        }
        return false;
    }

    @Override
    public boolean unregisterSignalListener(String path, SignalListener signalListener) {
        if (!initialized) {
            LOGGER.warn("zookeeper client not initialized");
            return false;
        }
        listenerForMap.remove(path);
        TreeCache treeCache = treeCaches.remove(path);
        if (treeCache != null) {
            treeCache.close();
            return true;
        } else {
            return false;

        }
    }

    /**
     * get init signal value.
     * @param path path
     * @return
     */
    public String getInitialSignalValue(String path) {
        if (initialized) {
            try {
                byte[] bytes = zkClient.getFramework().getData().forPath(path);
                if (bytes != null) {
                    return new String(bytes, "utf-8");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
