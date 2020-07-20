package com.liu.java.base.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileInputIODemo {

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("");
            boolean supported = fileInputStream.markSupported();
            System.out.println(supported);
        } catch (FileNotFoundException e) {
            System.out.println("找不到输入文件");
        }
    }
}
