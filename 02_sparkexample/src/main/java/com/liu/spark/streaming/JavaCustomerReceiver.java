package com.liu.spark.streaming;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2018/12/9 13:06
 * @Description: 自定义SparkStreaming的Receiver
 */
public class JavaCustomerReceiver extends Receiver<String> {
    private String host = null;
    private int port = -1;

    /**
     * .
     * @param host host
     * @param port port
     */
    public JavaCustomerReceiver(String host, int port) {
        super(StorageLevel.MEMORY_AND_DISK_2());
        this.host = host;
        this.port = port;
    }

    @Override
    public void onStart() {
        new Thread() {
            @Override
            public void run() {
                receive();
            }
        }.start();

    }

    @Override
    public void onStop() {
    }

    private void receive() {
        Socket socket = null;
        String userInput = null;
        try {
            socket = new Socket(host, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!isStopped() && (userInput = reader.readLine()) != null) {
                System.out.println("Received data '" + userInput + "'");
                store(userInput);
            }
            reader.close();
            socket.close();
            restart("trying to connect again");
        } catch (ConnectException ce) {
            restart("could not connect", ce);
        } catch (Throwable te) {
            restart("error receiving data", te);
        }
    }
}
