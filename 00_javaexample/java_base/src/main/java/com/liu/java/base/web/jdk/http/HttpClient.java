package com.liu.java.base.web.jdk.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * jdk自带的http客户端样例.
 * @ClassName HttpClient
 * @Description acpsdk发送后台http请求类
 * @date 2016-7-22 下午4:03:25
 */
public class HttpClient {
    /* 目标地址 */
    private URL url;

    /* 通信连接超时时间 */
    private int connectionTimeout;

    /* 通信读超时时间 */
    private int readTimeOut;


    /**
     * 构造函数.
     * @param url 目标地址
     * @param connectionTimeout HTTP连接超时时间
     * @param readTimeOut HTTP读写超时时间
     */
    public HttpClient(String url, int connectionTimeout, int readTimeOut) {
        try {
            this.url = new URL(url);
            this.connectionTimeout = connectionTimeout;
            this.readTimeOut = readTimeOut;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送信息到服务端.
     * @param data 待发送信息
     * @param encoding 编码
     * @return
     */
    public int send(Map<String, String> data, String encoding) {
        int res = -1;
        try {
            HttpURLConnection httpURLConnection = createConnection(encoding);
            if (null == httpURLConnection) {
                throw new Exception("Create httpURLConnection Failure");
            }
            String sendData = this.getRequestParamString(data, encoding);
            requestServer(httpURLConnection, sendData, encoding);
            res = httpURLConnection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
    /**
     * 发送信息到服务端 GET方式.
     * @param encoding encoding
     * @return
     */
    public int sendGet(String encoding) {
        int res = -1;
        try {
            HttpURLConnection httpURLConnection = createConnectionGet(encoding);
            if (null == httpURLConnection)  {
                throw new Exception("创建联接失败");
            }
            res = httpURLConnection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    
    /**
     * HTTP Post发送消息.
     * @param connection connection
     * @param message message
     */
    private void requestServer(final URLConnection connection, String message, String encoder) {
        PrintStream out = null;
        try {
            connection.connect();
            out = new PrintStream(connection.getOutputStream(), false, encoder);
            out.print(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }

    /**
     * 显示Response消息.
     * @param connection connection
     * @param encoding encoding
     * @return
     */
    private String response(final HttpURLConnection connection, String encoding) {
        InputStream in = null;
        StringBuilder sb = new StringBuilder(1024);
        BufferedReader br = null;
        try {
            if (200 == connection.getResponseCode()) {
                in = connection.getInputStream();
                sb.append(new String(read(in), encoding));
            } else {
                in = connection.getErrorStream();
                sb.append(new String(read(in), encoding));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != connection) {
                connection.disconnect();
            }
            return sb.toString();
        }
    }

    /**
     * 读取输入流.
     * @param in in
     * @return
     */
    public static byte[] read(InputStream in) {
        byte[] buf = new byte[1024];
        int length = 0;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            while ((length = in.read(buf, 0, buf.length)) > 0) {
                bout.write(buf, 0, length);
            }
            bout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bout.toByteArray();
    }
    
    /**
     * 创建连接.
     * @return
     */
    private HttpURLConnection createConnection(String encoding) {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        httpURLConnection.setConnectTimeout(this.connectionTimeout);// 连接超时时间
        httpURLConnection.setReadTimeout(this.readTimeOut);// 读取结果超时时间
        httpURLConnection.setDoInput(true); // 可读
        httpURLConnection.setDoOutput(true); // 可写
        httpURLConnection.setUseCaches(false);// 取消缓存
        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=" + encoding);
        try {
            httpURLConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        }
        return httpURLConnection;
    }

    /**
     * 创建连接.
     * @param encoding encoding
     * @return
     */
    private HttpURLConnection createConnectionGet(String encoding) {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpURLConnection.setConnectTimeout(this.connectionTimeout);// 连接超时时间
        httpURLConnection.setReadTimeout(this.readTimeOut);// 读取结果超时时间
        httpURLConnection.setUseCaches(false);// 取消缓存
        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=" + encoding);
        try {
            httpURLConnection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        return httpURLConnection;
    }
    
    /**
     * 将Map存储的对象，转换为key=value&key=value的字符.
     * @param requestParam requestParam
     * @param coder coder
     * @return
     */
    private String getRequestParamString(Map<String, String> requestParam, String coder) {
        if (null == coder || "".equals(coder)) {
            coder = "UTF-8";
        }
        StringBuffer sf = new StringBuffer("");
        String reqstr = "";
        if (null != requestParam && 0 != requestParam.size()) {
            for (Entry<String, String> en : requestParam.entrySet()) {
                try {
                    sf.append(en.getKey() + "=" + (null == en.getValue() || "".equals(en.getValue()) ? "" : URLEncoder
                                    .encode(en.getValue(), coder)) + "&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            reqstr = sf.substring(0, sf.length() - 1);
        }
        return reqstr;
    }

}
