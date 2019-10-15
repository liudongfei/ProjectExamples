package com.liu.logmsgcommon.rpc.protocol;

import io.netty.handler.codec.http.HttpRequest;

/**
 * util http client service interface.
 * @Auther: liudongfei
 * @Date: 2019/4/9 22:06
 * @Description:
 */
public interface UtilHttpClientService {
    boolean sendRequest(HttpRequest request);
}
