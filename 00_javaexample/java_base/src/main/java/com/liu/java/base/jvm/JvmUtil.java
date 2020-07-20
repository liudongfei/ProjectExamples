package com.liu.java.base.jvm;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 虚拟机工具类.
 * @author admin
 *
 */
public class JvmUtil {
    /**
     * 得到jvm的相关信息.
     */
    public static void getJvmInfo() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        if (runtime != null) {
            System.out.println("vmName:\t" + runtime.getVmName());
            System.out.println("vmVendor:\t" + runtime.getVmVendor());
            System.out.println("vmVersion:\t" + runtime.getVmVersion());
        }
    }

    /**
     * 得到当前虚拟机的内存使用情况.
     */
    public static void getCurrentJvmStatus() {
        Runtime runtime = Runtime.getRuntime();
        if (runtime != null) {
            System.out.println("total memery:\t" + runtime.totalMemory() * 0.001);
            System.out.println("free memery:\t" + runtime.freeMemory() * 0.001);
            System.out.println("max memery:\t" + runtime.maxMemory() * 0.001);
        }
    }
    public static void main(String[] args) {
        getCurrentJvmStatus();
    }
}
