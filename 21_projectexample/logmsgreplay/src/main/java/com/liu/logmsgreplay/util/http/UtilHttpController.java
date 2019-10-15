package com.liu.logmsgreplay.util.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/8 16:53
 * @Description:
 */
public class UtilHttpController {

    /**
     * main.
     * @param args args
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        List<UtilHttpResponseFuture> httpResponseFutureList = new ArrayList<>();
        String getUrl = "http://localhost:8899";
        UtilHttpRequest request = new UtilHttpRequest();
        request.content("hello server!".getBytes(CharsetUtil.UTF_8));
        request.header(HttpHeaders.Names.CONTENT_TYPE, "text/json; charset=UTF8").uri(getUrl);
        UtilHttpClient client = new UtilHttpClient.ConfigBuilder()
                .maxIdleTimeInMilliSecondes(200 * 1000)
                .connectTimeOutInMilliSecondes(30 * 1000).build();
        for (int i = 0; i < 500; i++) {
            UtilHttpResponseFuture responseFuture = client.doPost(request);
            httpResponseFutureList.add(responseFuture);
        }
        //UtilHttpResponse result = responseFuture.get();
        client.close();

    }
}
