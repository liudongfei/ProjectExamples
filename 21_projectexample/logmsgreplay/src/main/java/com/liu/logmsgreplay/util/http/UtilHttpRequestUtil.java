package com.liu.logmsgreplay.util.http;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/8 16:17
 * @Description:
 */
public class UtilHttpRequestUtil {
    /**
     * .
     * @param request request
     * @param httpMethod httpmethod
     * @return
     */
    public static HttpRequest create(UtilHttpRequest request, HttpMethod httpMethod) {
        HttpRequest httpRequest = null;
        if (HttpMethod.POST == httpMethod) {
            httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, request
                    .getUri().getRawPath(), request.getContent().retain());

            httpRequest.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
                    request.getContent().readableBytes());
        } else {
            httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, request
                    .getUri().getRawPath());
        }
        for (Map.Entry<String, Object> entry : request.getHeaders().entrySet()) {
            httpRequest.headers().set(entry.getKey(), entry.getValue());
        }
        httpRequest.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        httpRequest.headers().set(HttpHeaders.Names.HOST, request.getUri().getHost());

        return httpRequest;
    }
}
