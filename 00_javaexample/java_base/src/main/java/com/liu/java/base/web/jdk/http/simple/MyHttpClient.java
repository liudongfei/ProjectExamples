package com.liu.java.base.web.jdk.http.simple;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * 基于jdk的http客户端.
 * @Auther: liudongfei
 * @Date: 2019/2/28 16:52
 * @Description:
 */
public class MyHttpClient {
    /**
     * 发送get请求.
     * @param urlStr urlStr
     * @param param param
     * @return
     */
    public String sendGet(String urlStr, String param) {
        String result = "";
        urlStr = urlStr + "?" + param;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) "
                    + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Safari/605.1.15");
            connection.connect();
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            for (String key : headerFields.keySet()) {
                System.out.println(key + "---->" + headerFields.get(key));
            }
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                result += line + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送post请求.
     * @param urlStr urlStr
     * @param param param
     * @return
     */
    public String sendPost(String urlStr, String param) {
        String result = "";
        URL url = null;
        try {
            url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) "
                    + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Safari/605.1.15");
            connection.connect();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bufferedWriter.write(param);
            bufferedWriter.flush();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                result += line + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * main.
     * @param args args.
     */
    public static void main(String[] args) {
        String urlStr = "http://127.0.0.1:4701/server";
        String param = "k1=v1";
        //String res = new MyHttpClient().sendGet(urlStr, param);
        String res = new MyHttpClient().sendPost(urlStr, param);
        System.out.println(res);
    }
}
