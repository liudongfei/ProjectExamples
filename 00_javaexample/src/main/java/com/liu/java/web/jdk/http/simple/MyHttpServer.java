package com.liu.java.web.jdk.http.simple;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * 简单的HTTP Server端.
 * @Auther: liudongfei
 * @Date: 2019/2/28 16:52
 * @Description:
 */
public class MyHttpServer {
    /**
     * main.
     * @param args args
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(4701);
        HttpServer server = HttpServer.create(inetSocketAddress, 0);
        server.createContext("/server", new MyHttpHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server is listening on port 4701");
    }
}

/**
 * Http服务端业务处理类.
 */
class MyHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("get")) {
            doGet(httpExchange);
        }
        if (requestMethod.equalsIgnoreCase("post")) {
            doPost(httpExchange);
        }
    }

    /**
     * 处理get请求.
     * @param httpExchange httpExchange
     * @throws IOException exception
     */
    private void doGet(HttpExchange httpExchange) throws IOException {
        Headers requestHeaders = httpExchange.getRequestHeaders();
        InputStream requestBody = httpExchange.getRequestBody();
        System.out.println(is2String(requestBody));
        //获取请求参数
        System.out.println(httpExchange.getRequestURI());
        String query = httpExchange.getRequestURI().getQuery();
        System.out.println(query);
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/plain");
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write("jdk inner http server example".getBytes());
        responseBody.close();
    }

    /**
     * 处理post请求.
     * @param httpExchange httpExchange
     * @throws IOException exception
     */
    private void doPost(HttpExchange httpExchange) throws IOException {
        InputStream requestBody = httpExchange.getRequestBody();
        System.out.println(is2String(requestBody));
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/plain");
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write("jdk inner http server example".getBytes());
        responseBody.close();
    }

    private String is2String(InputStream is) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(is, "UTF-8");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0) {
                break;
            }
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}