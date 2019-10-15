package com.liu.logmsgcollect;

import com.liu.logmsgcommon.bean.KafkaEventBean;
import com.liu.logmsgcommon.bean.LogMsgInterface;
import com.liu.logmsgcommon.bean.LogMsgProtocol;
import com.liu.logmsgcommon.bean.LogMsgStruct;
import com.liu.logmsgcommon.util.ByteUtil;
import com.liu.logmsgcommon.util.ConstantUtil;
import com.liu.logmsgcommon.util.PropertiesUtil;
import com.liu.logmsgcommon.util.YamlConfigUtil;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.impl.TcpPacketImpl;
import io.pkts.protocol.Protocol;
import org.apache.commons.codec.binary.Hex;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * produce translog from pacap.
 * @Auther: liudongfei
 * @Date: 2019/4/3 09:36
 * @Description:
 */
public class PacapTransLogProducer {
    private static final Logger logger = LoggerFactory.getLogger(PacapTransLogProducer.class);

    private static List<KafkaEventBean> kafkaEventBeans = new ArrayList<>();

    /**
     * getpacketDataMap.
     * @param fileName fileName
     */
    public void getPacketDataMap(String fileName) throws IOException {
        Map<Long, StringBuffer> packetDataMap = new HashMap<>();
        Pcap pcap = Pcap.openStream(fileName);
        pcap.loop(packet -> {
            if (packet.hasProtocol(Protocol.TCP)) {
                TcpPacketImpl packet1 = (TcpPacketImpl) packet.getPacket(Protocol.TCP);
                Buffer payload = packet.getPacket(Protocol.TCP).getPayload();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                Date date = new Date(packet.getArrivalTime() / 1000);
                if (payload != null) {
                    byte[] bytes = payload.getArray();
                    System.out.println(new String(bytes));
                    if (bytes.length > 0) {
                        if (packetDataMap.containsKey(packet1.getAcknowledgementNumber())) {
                            packetDataMap.get(packet1.getAcknowledgementNumber()).append(Hex.encodeHexString(bytes));
                        } else {
                            StringBuffer val = new StringBuffer()
                                    .append(formatter.format(date) + "\t")
                                    .append(packet1.getSourceIP() + "\t")
                                    .append(packet1.getSourcePort() + "\t")
                                    .append(packet1.getDestinationIP() + "\t")
                                    .append(packet1.getDestinationPort() + "\t")
                                    .append(Hex.encodeHexString(bytes));
                            packetDataMap.put(packet1.getAcknowledgementNumber(), val);

                        }
                    }
                }
            }
            return true;
        });
        for (StringBuffer sb : packetDataMap.values()) {
            String[] transLogInfo = sb.toString().split("\t");
            if (transLogInfo.length == 6) {
                KafkaEventBean kafkaEventBean = new KafkaEventBean();
                kafkaEventBean.setArriveTime(transLogInfo[0]);
                kafkaEventBean.setSourceIP(transLogInfo[1]);
                kafkaEventBean.setSourcePort(transLogInfo[2]);
                kafkaEventBean.setDestinationIP(transLogInfo[3]);
                kafkaEventBean.setDestinationPort(transLogInfo[4]);
                kafkaEventBean.setDataContent(ByteUtil.hexStr2Bytes(transLogInfo[5]));
                kafkaEventBean.setLogMsgInterface(getInterfaceFromLog(fileName));
                kafkaEventBean.setLogMsgStruct(getParserFromLog(fileName));
                kafkaEventBean.setLogMsgProtocol(getProtocolFromLog(fileName));
                logger.info(kafkaEventBean.toString());
                if (kafkaEventBean.getDataContent().length > 10) {
                    kafkaEventBeans.add(kafkaEventBean);
                }
            }
        }
    }

    private LogMsgProtocol getProtocolFromLog(String fileName) {
        if (fileName.contains("8583")) {
            return LogMsgProtocol.TCP;
        } else if (fileName.contains("httpjson")) {
            return LogMsgProtocol.HTTP;
        } else if (fileName.contains("httpxml")) {
            return LogMsgProtocol.HTTP;
        }
        return null;
    }

    /**
     * produce translog message.
     * @param configMap configMap
     * @throws IOException exception
     */
    public void produceTransLogMsg(Map configMap) throws IOException {

        KafkaProducer producer = new KafkaProducer<>(PropertiesUtil.fromMap(configMap));
        String topic = (String) configMap.get(ConstantUtil.TRANSLOG_PRODUCE_TOPIC);
        while (true) {
            int index = ThreadLocalRandom.current().nextInt(kafkaEventBeans.size());
            producer.send(new ProducerRecord(topic, kafkaEventBeans.get(index)), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        logger.info("message send succed");
                    } else {
                        logger.info("message send failed");
                    }
                }
            });
        }

    }

    private LogMsgStruct getParserFromLog(String fileName) {
        if (fileName.contains("8583")) {
            return LogMsgStruct.ISO8583;
        } else if (fileName.contains("json")) {
            return LogMsgStruct.JSON;
        } else if (fileName.contains("xml")) {
            return LogMsgStruct.XML;
        }
        return null;
    }

    private LogMsgInterface getInterfaceFromLog(String fileName) {
        if (fileName.contains("8583")) {
            return LogMsgInterface.POST_ISO8583;
        } else if (fileName.contains("httpjson")) {
            return LogMsgInterface.TSM_HTTPJSON;
        } else if (fileName.contains("httpxml")) {
            return LogMsgInterface.TSM_HTTPXML;
        }
        return null;
    }

    /**
     * start producer logmsg.
     * @param filePath filePath
     * @param configMap configMap
     * @throws IOException e
     */
    public void start(String filePath, Map configMap) throws IOException {
        File file = new File(filePath);

        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains(".out")) {
                    return true;
                }
                return false;
            }
        });
        for (File f : files) {
            getPacketDataMap(f.getAbsolutePath());
        }
        produceTransLogMsg(configMap);
    }

    /**
     * main.
     * @param args args
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        String configPath = "LogMsgCollect.yaml";
        String fileName = "/Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/data/pcap/";
        Map configMap = YamlConfigUtil.findAndReadConfigFile(configPath, true);
        PacapTransLogProducer transLogProducer = new PacapTransLogProducer();
        transLogProducer.start(fileName, configMap);
    }

}
