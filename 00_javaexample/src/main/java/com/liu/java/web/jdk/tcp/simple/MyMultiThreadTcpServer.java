package com.liu.java.web.jdk.tcp.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程TCP服务器,可以同时接收.
 * @Auther: liudongfei
 * @Date: 2019/3/1 10:42
 * @Description:
 */
public class MyMultiThreadTcpServer {
    private int port = 4700;
    private int poolSize = 2;
    private int threadNum = 0;
    private ServerSocket server;
    private ExecutorService executorService;

    public MyMultiThreadTcpServer() throws IOException {
        this.server = new ServerSocket(port);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * 服务监听连接请求.
     * @throws IOException exception
     */
    public void service() throws IOException {
        while (true) {
            Socket socket = server.accept();
            executorService.execute(new MyMultiThreadHandler(socket, threadNum++));
        }
    }

    /**
     * main.
     * @param args args
     * @throws IOException e
     */
    public static void main(String[] args) throws IOException {
        MyMultiThreadTcpServer myMultiThreadTcpServer = new MyMultiThreadTcpServer();
        myMultiThreadTcpServer.service();
    }
}

/**
 * Http服务端业务处理类.
 */
class MyMultiThreadHandler implements Runnable {
    private Socket socket = null;
    private Integer threadNum = null;

    public MyMultiThreadHandler(Socket socket, int threadNum) {
        this.threadNum = threadNum;
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("num:" + threadNum + " connect start!");
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream());
            String line = bufferedReader.readLine();
            while (null != line & !"bye".equals(line)) {
                System.out.println("num:" + threadNum + " connect receive message:\t" + line);
                printWriter.println("message receivce!");
                printWriter.flush();
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("num:" + threadNum + " connect close");
            try {
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
                if (null != printWriter) {
                    printWriter.close();
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

