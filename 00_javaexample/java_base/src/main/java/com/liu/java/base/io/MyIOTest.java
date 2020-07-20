package com.liu.java.base.io;

import com.liu.common.PropertiesUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * IO流测试.
 * @Auther: liudongfei
 * @Date: 2019/9/6 10:01
 * @Description:
 */
public class MyIOTest {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        String sInputString = PropertiesUtil.getStringValue("app.properties", "param");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(sInputString.getBytes());
        byte[] buf = new byte[1024];
        int length = 0;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            while ((length = inputStream.read(buf, 0, buf.length)) > 0) {
                bout.write(buf, 0, length);
            }
            bout.flush();
            System.out.println(new String(bout.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
