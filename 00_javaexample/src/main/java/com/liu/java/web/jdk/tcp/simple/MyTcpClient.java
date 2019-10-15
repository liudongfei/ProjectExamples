package com.liu.java.web.jdk.tcp.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 简单的TCP连接的客户端.
 * @Auther: liudongfei
 * @Date: 2019/2/28 16:51
 * @Description:
 */
public class MyTcpClient {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader input = null;
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        try {
            socket = new Socket("127.0.0.1", 4700);
            input = new BufferedReader(new InputStreamReader(System.in));
            printWriter = new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String readline = input.readLine(); //从系统标准输入读入一字符串
            while (!readline.equals("bye")) {
                printWriter.println(readline);
                printWriter.flush();
                System.out.println("send message:\t" + readline);
                System.out.println("receive message:\t" + bufferedReader.readLine());
                readline = input.readLine(); //从系统标准输入读入一字符串
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("close connection!");
            try {
                if (null != input) {
                    input.close();
                }
                if (null != printWriter) {
                    printWriter.close();
                }
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
                if (null != socket) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
