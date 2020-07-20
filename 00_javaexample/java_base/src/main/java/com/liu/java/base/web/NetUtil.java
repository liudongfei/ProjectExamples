package com.liu.java.base.web;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 本机网络信息工具类.
 * @author admin
 *
 */
public class NetUtil {
    /**
     * 获取本机IP.
     * @return
     */
    public static String getLocalHostIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本机的hostname.
     * @return
     */
    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        System.out.println(getLocalHostIP());
        System.out.println(getLocalHostName());
    }
}

