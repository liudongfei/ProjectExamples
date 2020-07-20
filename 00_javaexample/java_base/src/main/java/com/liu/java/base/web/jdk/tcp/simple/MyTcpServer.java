package com.liu.java.base.web.jdk.tcp.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 简单的TCP连接的服务端.
 * @Auther: liudongfei
 * @Date: 2019/2/28 16:51
 * @Description:
 */
public class MyTcpServer {
    public static final int PORT = 4700;

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        ServerSocket server = null;
        Socket socket = null;
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;
        try {
            server = new ServerSocket(PORT);
            socket = server.accept();
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream());
            String line = bufferedReader.readLine();
            while (null != line & !"bye".equals(line)) {
                System.out.println("receive message:\t" + line);
                printWriter.println("receive message!");
                printWriter.flush();
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("close server!");
            try {
                if (null != printWriter) {
                    printWriter.close();
                }
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
                if (null != socket) {
                    socket.close();
                }
                if (null != server) {
                    server.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
