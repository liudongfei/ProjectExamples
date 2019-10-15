package com.liu.java.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试java的jvm结构样例类.
 * @Auther: liudongfei
 * @Date: 2019/3/27 16:15
 * @Description:
 */
public class TestMemory {
    static class OOMOject {
        public byte[] placeholder = new byte[64 * 1024];
    }

    /**
     * 向堆中添加一定数量的对象.
     *
     * @param num list num
     */
    public static void fillHeap(int num) throws InterruptedException {
        List<OOMOject> oomOjects = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Thread.sleep(50);
            oomOjects.add(new OOMOject());
        }
    }

    /**
     * 主方法.
     *
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException {
        fillHeap(1000);
        Thread.sleep(500000);

    }
}
