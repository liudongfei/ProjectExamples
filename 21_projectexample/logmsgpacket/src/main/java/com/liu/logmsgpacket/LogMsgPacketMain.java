package com.liu.logmsgpacket;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.rpc.client.RpcProxy;
import com.liu.logmsgcommon.rpc.protocol.UtilTcpClientService;
import com.liu.logmsgcommon.rpc.registry.ServiceDiscovery;
import com.liu.logmsgcommon.util.PropertiesUtil;
import com.liu.logmsgcommon.util.YamlConfigUtil;
import com.liu.logmsgcommon.zookeeper.SignalListener;
import com.liu.logmsgcommon.zookeeper.ZKStateSignalService;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/10 10:18
 * @Description:
 */
public class LogMsgPacketMain {
    private static final Logger logger = LoggerFactory.getLogger(LogMsgPacketMain.class);

    private ExecutorService pool = Executors.newCachedThreadPool();
    private Map<Integer, Runnable> consumerMap = new HashMap<>();
    private ZKStateSignalService zkStateSignalService = null;
    private UtilTcpClientService tcpClientService = null;

    private void init(Map configMap) throws Exception {
        String zookeeperAddress = (String) configMap.get("zookeeper.address");
        String zookeeperRootpath = (String) configMap.get("zookeeper.rootpath");
        String watchPath = (String) configMap.get("topic.num.path");

        zkStateSignalService = ZKStateSignalService.getInstance(zookeeperAddress, zookeeperRootpath);
        zkStateSignalService.registerSignalListener(watchPath, new SignalListener() {
            @Override
            public void onSignal(String path, String value) throws Exception {
                update(value, configMap);
            }
        });
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery(zookeeperAddress);
        RpcProxy rpcProxy = new RpcProxy(serviceDiscovery);
        tcpClientService = rpcProxy.create(UtilTcpClientService.class);

        String initialSignalValue = zkStateSignalService.getInitialSignalValue(watchPath);
        int partitionNum = Integer.parseInt(initialSignalValue);
        for (int partitionId = 0; partitionId < partitionNum; partitionId ++) {
            KafkaConsumer<String, KafkaEventBean> consumer = new KafkaConsumer<>(PropertiesUtil.fromMap(configMap));
            LogMsgConsumerThread consumerThread = new LogMsgConsumerThread(consumer, partitionId, tcpClientService);
            pool.execute(consumerThread);
            consumerMap.put(partitionId, consumerThread);
        }
    }

    private void update(String value, Map configMap) {
        int partitionNum = Integer.parseInt(value);
        for (int partitionId = 0; partitionId < partitionNum; partitionId ++) {
            if (!consumerMap.containsKey(partitionId)) {
                logger.info("add partition " + partitionId);
                KafkaConsumer<Object, Object> consumer = new KafkaConsumer<>(PropertiesUtil.fromMap(configMap));
                LogMsgConsumerThread consumerThread = new LogMsgConsumerThread(consumer, partitionId, tcpClientService);
                pool.execute(consumerThread);
                consumerMap.put(partitionId, consumerThread);
            }
        }

    }

    private void close() {
        pool.shutdown();
    }

    /**
     * main.
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        Map configMap = YamlConfigUtil.findAndReadConfigFile("LogMsgPacket.yaml", true);
        new LogMsgPacketMain().init(configMap);
    }

}
