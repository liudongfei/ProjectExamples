package com.liu.logmsgpacket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liu.logmsgcommon.bean.LogMsgInterface;
import com.liu.logmsgcommon.bean.LogMsgParserResult;
import com.liu.logmsgcommon.bean.LogMsgProtocol;
import com.liu.logmsgcommon.bean.LogMsgRelateResult;
import com.liu.logmsgcommon.bean.LogMsgStruct;
import com.liu.logmsgcommon.util.YamlConfigUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *logmsg hbase reader.
 * @Auther: liudongfei
 * @Date: 2019/4/24 11:34
 * @Description:
 */
public class LogMsgHbaseReader {
    private Connection conn = null;
    private Table table = null;
    private ObjectMapper objectMapper = null;
    private static LogMsgHbaseReader instance;
    private static boolean initialized;

    /**
     * get instance.
     * @param configMap configMap
     * @return
     */
    public static LogMsgHbaseReader getInstance(Map configMap) throws Exception {
        if (initialized) {
            return instance;
        }
        instance = new LogMsgHbaseReader();
        instance.init(configMap);
        initialized = true;
        return instance;
    }

    private void init(Map configMap) throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        conn = ConnectionFactory.createConnection(configuration);
        String tableName = (String) configMap.get("hbase.logmsg.table");
        TableName name = TableName.valueOf(tableName);
        table = conn.getTable(name);
        objectMapper = new ObjectMapper();
    }

    /**
     * get logmsg.
     * @param translinkId translinkId
     * @return list
     * @throws IOException e
     */
    public List<LogMsgRelateResult> getLogMsg(String translinkId) throws IOException {
        List<LogMsgRelateResult> logMsgRelateResultList = new ArrayList<>();
        Scan scan = new Scan();
        RowFilter filter = new RowFilter(
                CompareFilter.CompareOp.EQUAL, new RegexStringComparator(translinkId + ".*"));
        scan.setFilter(filter);
        ResultScanner resultScanner = table.getScanner(scan);
        scan.setCacheBlocks(false);
        scan.setCaching(100);
        Result res = null;
        while (null != (res = resultScanner.next())) {
                LogMsgRelateResult logMsgRelateResult = new LogMsgRelateResult();
            for (Cell cell : res.rawCells()) {
                String rowKey = Bytes.toString(CellUtil.cloneRow(cell));
                String[] split = rowKey.split("_");
                logMsgRelateResult.setTransLinkedId(split[0]);
                if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("logMsgInterface")) {
                    logMsgRelateResult.setLogMsgInterface(
                            LogMsgInterface.valueOf(Bytes.toString(CellUtil.cloneValue(cell))));
                }
                if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("logMsgProtocol")) {
                    logMsgRelateResult.setLogMsgProtocol(
                            LogMsgProtocol.valueOf(Bytes.toString(CellUtil.cloneValue(cell))));
                }
                if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("logMsgStruct")) {
                    logMsgRelateResult.setLogMsgStruct(
                            LogMsgStruct.valueOf(Bytes.toString(CellUtil.cloneValue(cell))));
                }
                if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("reqLogMsgParserResult")) {
                    LogMsgParserResult reqLogMsgParserResult =
                            objectMapper.readValue(Bytes.toString(CellUtil.cloneValue(cell)), LogMsgParserResult.class);
                    logMsgRelateResult.setReqLogMsgParserResult(reqLogMsgParserResult);
                }
                if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals("resLogMsgParserResult")) {
                    LogMsgParserResult resLogMsgParserResult =
                            objectMapper.readValue(Bytes.toString(CellUtil.cloneValue(cell)), LogMsgParserResult.class);
                    logMsgRelateResult.setResLogMsgParserResult(resLogMsgParserResult);
                }
            }
            logMsgRelateResultList.add(logMsgRelateResult);
        }

        IOUtils.closeStream(table);
        return logMsgRelateResultList;
    }
}
