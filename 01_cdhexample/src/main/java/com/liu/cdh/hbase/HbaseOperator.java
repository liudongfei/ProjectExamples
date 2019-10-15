package com.liu.cdh.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;

/**
 * hbase操作样例.
 * @Auther: liudongfei
 * @Date: 2018/12/2 22:34
 * @Description:
 */
public class HbaseOperator {
    /**
     * 获取table对象.
     * @param tableName tableName
     * @return
     */
    public static HTable getTableByTableName(String tableName) {
        Configuration configuration = HBaseConfiguration.create();
        HTable hTable = null;
        try {
            hTable = new HTable(configuration, tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hTable;
    }

    /**
     * hbase的get操作.
     * @throws IOException exception
     */
    public static void getData() throws IOException {
        HTable hTable = getTableByTableName("user");
        Get get = new Get(Bytes.toBytes("10010"));
        get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"));
        Result result = hTable.get(get);
        for (Cell cell : result.rawCells()) {
            System.out.println(Bytes.toString(CellUtil.cloneRow(cell)) + "\t->\t"
                    + Bytes.toString(CellUtil.cloneFamily(cell)) + ":"
                    + Bytes.toString(CellUtil.cloneQualifier(cell)) + "\t"
                    + Bytes.toString(CellUtil.cloneValue(cell)));
        }
        IOUtils.closeStream(hTable);
    }

    /**
     * hbase的put操作.
     * @throws IOException exception
     */
    public static void putData() throws IOException {
        HTable hTable = getTableByTableName("user");
        Put put = new Put(Bytes.toBytes("10010"));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("address"), Bytes.toBytes("age"));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes("sun"));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes(23));
        hTable.put(put);
        IOUtils.closeStream(hTable);
    }

    /**
     * hbase的删除操作.
     * @throws IOException exception
     */
    public static void deleteData() throws IOException {
        HTable hTable = getTableByTableName("user");
        Delete delete = new Delete(Bytes.toBytes("10003"));
        delete.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"));
        delete.addColumn(Bytes.toBytes("info"), Bytes.toBytes("address"));
        delete.addColumns(Bytes.toBytes("info"), Bytes.toBytes("age"));
        hTable.delete(delete);
        IOUtils.closeStream(hTable);
    }

    /**
     * 全表扫描数据.
     * @throws IOException exception
     */
    public static void scanData() throws IOException {
        HTable hTable = getTableByTableName("user");
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"));
        scan.setStartRow(Bytes.toBytes("10002"));
        scan.setStopRow(Bytes.toBytes("10011"));//包头不包尾
        Filter filter = new PrefixFilter(Bytes.toBytes("100"));
        scan.setFilter(filter);
        scan.setCacheBlocks(false);
        scan.setCaching(100);
        ResultScanner scanner = hTable.getScanner(scan);
        Result res = null;
        while (null != (res = scanner.next())) {
            for (Cell cell : res.rawCells()) {
                System.out.println(Bytes.toString(CellUtil.cloneRow(cell)) + "\t->\t"
                        + Bytes.toString(CellUtil.cloneFamily(cell)) + ":"
                        + Bytes.toString(CellUtil.cloneQualifier(cell)) + "\t"
                        + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
        IOUtils.closeStream(hTable);
    }

    /**
     * main.
     * @param args args
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        //getTableByTableName("user");
        //getData();
        //putData();
        //deleteData();
        scanData();
    }
}
