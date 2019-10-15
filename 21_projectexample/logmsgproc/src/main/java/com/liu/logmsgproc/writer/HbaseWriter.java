package com.liu.logmsgproc.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liu.logmsgcommon.bean.LogMsgParserResult;
import com.liu.logmsgcommon.bean.LogMsgRelateResult;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;


/**
 * flink write to hbase.
 * @Auther: liudongfei
 * @Date: 2019/4/4 16:01
 * @Description:
 */
public class HbaseWriter extends RichSinkFunction<LogMsgRelateResult> {
    private org.apache.hadoop.conf.Configuration conf = null;
    private Connection conn = null;
    private Table table = null;
    private static ObjectMapper objectMapper = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        conf = HBaseConfiguration.create();
        conf.set("hbase.rootdir", "hdfs://centos-7:9000/hbase");
        conf.set("hbase.zookeeper.quorum", "centos-7:2181");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conn = ConnectionFactory.createConnection(conf);
        Admin admin = conn.getAdmin();
        TableName tableName = TableName.valueOf("logmsg");
        synchronized (tableName) {
            if (!admin.tableExists(tableName)) {
                HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
                hTableDescriptor.addFamily(new HColumnDescriptor("info"));
                admin.createTable(hTableDescriptor);
            }
            table = conn.getTable(tableName);
        }
        objectMapper = new ObjectMapper();
    }

    @Override
    public void close() throws Exception {
        super.close();
        table.close();
        conn.close();
    }

    @Override
    public void invoke(LogMsgRelateResult logMsgRelateResult) throws Exception {
        String transLinkedId = logMsgRelateResult.getTransLinkedId();
        String arriveTime = logMsgRelateResult.getReqLogMsgParserResult().getSourceMsgBean().getArriveTime();
        String logMsgInterface = logMsgRelateResult.getLogMsgInterface().toString();
        String logMsgProtocol = logMsgRelateResult.getLogMsgProtocol().toString();
        String logMsgStruct = logMsgRelateResult.getLogMsgStruct().toString();
        LogMsgParserResult reqLogMsgParserResult = logMsgRelateResult.getReqLogMsgParserResult();
        LogMsgParserResult resLogMsgParserResult = logMsgRelateResult.getResLogMsgParserResult();
        String reqLogMsgParserStr = objectMapper.writeValueAsString(reqLogMsgParserResult);
        String resLogMsgParserStr = objectMapper.writeValueAsString(resLogMsgParserResult);

        String rowkey = transLinkedId + "_" + arriveTime;
        Put put = new Put(Bytes.toBytes(rowkey));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("logMsgInterface"), Bytes.toBytes(logMsgInterface));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("logMsgProtocol"), Bytes.toBytes(logMsgProtocol));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("logMsgStruct"), Bytes.toBytes(logMsgStruct));
        put.addColumn(
                Bytes.toBytes("info"), Bytes.toBytes("reqLogMsgParserResult"), Bytes.toBytes(reqLogMsgParserStr));
        put.addColumn(
                Bytes.toBytes("info"), Bytes.toBytes("resLogMsgParserResult"), Bytes.toBytes(resLogMsgParserStr));
        table.put(put);
    }
}
