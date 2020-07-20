package com.liu.cdh.hbase;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * 协处理器的实现.
 * @Auther: liudongfei
 * @Date: 2019/11/11 13:48
 * @Description:
 */
public class MyCoprocessor1 extends BaseRegionObserver {


    @Override
    public void preGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
        System.out.println("get之前的动作！！！modify");
    }

    @Override
    public void postGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
        System.out.println("get之后的动作！！！");
    }

}
