package com.liu.logmsgreplay.util.http;

import io.netty.channel.Channel;

/**
 * channel init.
 * @Auther: liudongfei
 * @Date: 2019/4/8 16:16
 * @Description:
 */
public interface AdditionalHttpChannelInitializer {
    void initChannel(Channel ch) throws Exception;
}
