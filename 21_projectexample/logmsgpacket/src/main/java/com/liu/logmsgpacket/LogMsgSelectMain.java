package com.liu.logmsgpacket;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgRelateResult;
import com.liu.logmsgcommon.util.PropertiesUtil;
import com.liu.logmsgcommon.util.YamlConfigUtil;
import com.liu.logmsgcommon.zookeeper.SignalListener;
import com.liu.logmsgcommon.zookeeper.ZKStateSignalService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * main.
 * @Auther: liudongfei
 * @Date: 2019/4/11 15:19
 * @Description:
 */
public class LogMsgSelectMain {
    private volatile int partitionNum = 0;
    private KafkaProducer<String, KafkaEventBean> producer;
    private String topicId;
    private static LogMsgSelectMain instance;
    private static volatile boolean initialized = false;
    private ZKStateSignalService zkStateSignalService = null;

    /**
     * get instance.
     * @param configMap configMap
     * @return
     */
    public static LogMsgSelectMain getInstance(Map configMap) {
        if (initialized) {
            return instance;
        }
        instance = new LogMsgSelectMain();
        instance.init(configMap);
        initialized = true;
        return instance;
    }

    private void init(Map configMap) {
        String zookeeperAddress = (String) configMap.get("zookeeper.address");
        String zookeeperRootpath = (String) configMap.get("zookeeper.rootpath");
        String watchPath = (String) configMap.get("topic.num.path");

        topicId = (String) configMap.get("translog.packet.topic");
        zkStateSignalService = ZKStateSignalService.getInstance(zookeeperAddress, zookeeperRootpath);
        zkStateSignalService.registerSignalListener(watchPath, new SignalListener() {
            @Override
            public void onSignal(String path, String value) throws Exception {
                LogMsgSelectMain.this.partitionNum = Integer.parseInt(value);
            }
        });
        producer = new KafkaProducer<>(PropertiesUtil.fromMap(configMap));
        String initialSignalValue = zkStateSignalService.getInitialSignalValue(watchPath);
        this.partitionNum = Integer.parseInt(initialSignalValue);
    }

    public void sendLogMsg(KafkaEventBean kafkaEventBean) {
        producer.send(new ProducerRecord<>(
                topicId, ThreadLocalRandom.current().nextInt(partitionNum), null, kafkaEventBean));
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) throws Exception {
        // load config
        Map configMap = YamlConfigUtil.findAndReadConfigFile("LogMsgPacket.yaml", true);

        LogMsgHiveReader logMsgHiveReader = LogMsgHiveReader.getInstance(configMap);
        LogMsgHbaseReader logMsgHbaseReader = LogMsgHbaseReader.getInstance(configMap);
        LogMsgSelectMain logMsgPacketMain = LogMsgSelectMain.getInstance(configMap);

        List<String> translinkList = logMsgHiveReader.getTranslinkList("select * from db_hive.log_translink");
        for (String translink : translinkList) {
            List<LogMsgRelateResult> logMsgList = logMsgHbaseReader.getLogMsg(translink);
            for (LogMsgRelateResult logMsgRelateResult : logMsgList) {
                System.out.println(logMsgRelateResult.getReqLogMsgParserResult().getSourceMsgBean());
                logMsgPacketMain.sendLogMsg(logMsgRelateResult.getReqLogMsgParserResult().getSourceMsgBean());
            }
        }
    }
}
