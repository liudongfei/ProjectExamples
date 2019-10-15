package com.liu.cdh.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

/**
 * Mapreduce集成Hbase：
 * 使用MR从hbase中的表'user'读取数据，写入'basic'.
 * @Auther: liudongfei
 * @Date: 2018/12/3 22:02
 * @Description:
 * HBase与MapReduce集成，从一个表中读数据向另外一个表中写数据
 */
public class User2BasicMapReduce extends Configured implements Tool {
    public static class UserReadMapper extends TableMapper<Text, Put> {
        private Text outputValue = new Text();

        @Override
        public void map(ImmutableBytesWritable key, Result value, Context context)
                throws IOException, InterruptedException {
            String rowKey = Bytes.toString(key.get());
            outputValue.set(rowKey);
            Put put = new Put(key.get());
            for (Cell cell : value.rawCells()) {
                if ("info".equals(Bytes.toString(CellUtil.cloneFamily(cell)))) {
                    if ("name".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                        put.add(cell);
                    }
                    if ("address".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                        put.add(cell);
                    }
                }
            }
            context.write(outputValue, put);
        }
    }

    public static class BasicWriteReduce extends TableReducer<Text, Put, ImmutableBytesWritable> {

        @Override
        protected void reduce(Text key, Iterable<Put> values, Context context)
                throws IOException, InterruptedException {
            for (Put put : values) {
                context.write(null, put);
            }
        }
    }

    @Override
    public int run(String[] strings) throws Exception {
        Job job = Job.getInstance(this.getConf(), this.getClass().getSimpleName());

        job.setJarByClass(this.getClass());
        Scan scan = new Scan();
        scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
        scan.setCacheBlocks(false);  // don't set to true for MR jobs
        // set other scan attrs

        TableMapReduceUtil.initTableMapperJob(
                "user",      // input table
                scan,             // Scan instance to control CF and attribute selection
                UserReadMapper.class,   // mapper class
                Text.class,             // mapper output key
                Put.class,             // mapper output value
                job);
        TableMapReduceUtil.initTableReducerJob(
                "basic",      // output table
                BasicWriteReduce.class,             // reducer class
                job);
        job.setNumReduceTasks(1);
        job.submit();
        boolean isSuccess = job.waitForCompletion(true);
        return isSuccess ? 0 : 1;
    }

    /**
     * main.
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        Configuration configuration = HBaseConfiguration.create();
        int run = ToolRunner.run(configuration, new User2BasicMapReduce(), args);
        System.exit(run);
    }

}
