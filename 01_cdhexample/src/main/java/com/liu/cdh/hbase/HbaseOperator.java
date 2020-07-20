package com.liu.cdh.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

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
    public static Table getTableByTableName(String tableName) {
        Configuration configuration = HBaseConfiguration.create();

        Table table = null;
        Connection connection = null;
        try {
            connection = ConnectionFactory.createConnection(configuration);
            table = connection.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedMutatorParams mutatorParams = new BufferedMutatorParams(TableName.valueOf(tableName));

        ExecutorService pool = mutatorParams.getPool();
        mutatorParams.writeBufferSize(10000);
        return table;
    }

    /**
     * hbase的get操作.
     * @throws IOException exception
     */
    public static void getData() throws IOException {
        Table table = getTableByTableName("user");

        Get get = new Get(Bytes.toBytes("10010"));
        get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"));
        Result result = table.get(get);
        for (Cell cell : result.rawCells()) {
            System.out.println(Bytes.toString(CellUtil.cloneRow(cell)) + "\t->\t"
                    + Bytes.toString(CellUtil.cloneFamily(cell)) + ":"
                    + Bytes.toString(CellUtil.cloneQualifier(cell)) + "\t"
                    + Bytes.toString(CellUtil.cloneValue(cell)));
        }
        IOUtils.closeStream(table);
    }

    /**
     * hbase的put操作.
     * @throws IOException exception
     */
    public static void putData() throws IOException {
        Table table = getTableByTableName("user");
        Put put = new Put(Bytes.toBytes("10010"));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("address"), Bytes.toBytes("age"));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes("sun"));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes(23));
        put.setDurability(Durability.SYNC_WAL);
        table.put(put);
        IOUtils.closeStream(table);
    }

    /**
     * hbase的删除操作.
     * @throws IOException exception
     */
    public static void deleteData() throws IOException {
        Table table = getTableByTableName("user");
        Delete delete = new Delete(Bytes.toBytes("10003"));
        delete.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"));
        delete.addColumn(Bytes.toBytes("info"), Bytes.toBytes("address"));
        delete.addColumns(Bytes.toBytes("info"), Bytes.toBytes("age"));
        table.delete(delete);
        IOUtils.closeStream(table);
    }

    /**
     * 全表扫描数据.
     * @throws IOException exception
     */
    public static void scanData() throws IOException {
        Table table = getTableByTableName("user");
        Scan scan = new Scan();
        //scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"));
        scan.setStartRow(Bytes.toBytes("10002"));
        scan.setStopRow(Bytes.toBytes("10011"));//包头不包尾
        Filter filter = new PrefixFilter(Bytes.toBytes("100"));
        scan.setFilter(filter);
        scan.setCacheBlocks(false);
        scan.setCaching(100);
        scan.setAllowPartialResults(true);
        scan.setMaxResultSize(1000);
        scan.setBatch(2);
        ResultScanner scanner = table.getScanner(scan);
        Result res = null;
        while (null != (res = scanner.next())) {
            for (Cell cell : res.rawCells()) {
                System.out.println(Bytes.toString(CellUtil.cloneRow(cell)) + "\t->\t"
                        + Bytes.toString(CellUtil.cloneFamily(cell)) + ":"
                        + Bytes.toString(CellUtil.cloneQualifier(cell)) + "\t"
                        + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
        IOUtils.closeStream(table);
    }

    /**
     * main.
     * @param args args
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        //getTableByTableName("user");
        getData();
        //putData();
        //deleteData();
        //scanData();
    }
}
