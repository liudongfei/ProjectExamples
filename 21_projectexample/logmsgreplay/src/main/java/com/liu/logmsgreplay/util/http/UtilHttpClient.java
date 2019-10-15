package com.liu.logmsgreplay.util.http;

import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * util netty http client.
 * @Auther: liudongfei
 * @Date: 2019/4/8 16:08
 * @Description:
 */
public class UtilHttpClient {
    private UtilHttpChannelPool channelPool;

    private ConfigBuilder    configBuilder;

    private UtilHttpClient(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        this.channelPool = new UtilHttpChannelPool(configBuilder.getMaxPerRoute(), configBuilder
                .getConnectTimeOutInMilliSecondes(), configBuilder.getMaxIdleTimeInMilliSecondes(),
                configBuilder.getForbidForceConnect(), configBuilder.getAdditionalChannelInitializer(),
                configBuilder.getOptions(), configBuilder.getGroup());
    }

    /**
     *  send request.
     * @param request request
     * @return .
     * @throws Exception Exception
     */
    public UtilHttpResponseFuture doPost(UtilHttpRequest request) throws Exception {
        HttpRequest httpRequest = UtilHttpRequestUtil.create(request, HttpMethod.POST);
        InetSocketAddress route = new InetSocketAddress(request.getUri().getHost(), request.getUri().getPort());
        return channelPool.sendRequest(route, httpRequest);
    }

    /**
     * send get request.
     * @param request request
     * @return .
     * @throws Exception exception
     */
    public UtilHttpResponseFuture doGet(UtilHttpRequest request) throws Exception {
        HttpRequest httpRequest = UtilHttpRequestUtil.create(request, HttpMethod.GET);
        InetSocketAddress route = new InetSocketAddress(request.getUri().getHost(), request.getUri().getPort());
        return channelPool.sendRequest(route, httpRequest);
    }

    public void close() throws InterruptedException {
        channelPool.close();
    }

    public ConfigBuilder getConfigBuilder() {
        return configBuilder;
    }

    public void setConfigBuilder(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
    }

    public static final class ConfigBuilder {
        private Map<ChannelOption, Object> options = new HashMap<>();
        // max idle time for a channel before close
        private int maxIdleTimeInMilliSecondes;
        // max time wait for a channel return from pool
        private int connectTimeOutInMilliSecondes;

        /**
         * value is false indicates that when there is not any channel in pool and no new
         * channel allowed to be create based on maxPerRoute, a new channel will be forced
         * to create.Otherwise, a <code>TimeoutException</code> will be thrown
         * value is false.
         */
        private boolean forbidForceConnect = false;
        private AdditionalHttpChannelInitializer additionalChannelInitializer;
        // max number of channels allow to be created per route
        private Map<String, Integer> maxPerRoute;

        private EventLoopGroup customGroup;

        public ConfigBuilder() {
        }

        public UtilHttpClient build() {
            return new UtilHttpClient(this);
        }

        public ConfigBuilder maxPerRoute(Map<String, Integer> maxPerRoute) {
            this.maxPerRoute = maxPerRoute;
            return this;
        }

        public ConfigBuilder connectTimeOutInMilliSecondes(int connectTimeOutInMilliSecondes) {
            this.connectTimeOutInMilliSecondes = connectTimeOutInMilliSecondes;
            return this;
        }

        public ConfigBuilder option(ChannelOption key, Object value) {
            options.put(key, value);
            return this;
        }

        public ConfigBuilder maxIdleTimeInMilliSecondes(int maxIdleTimeInMilliSecondes) {
            this.maxIdleTimeInMilliSecondes = maxIdleTimeInMilliSecondes;
            return this;
        }

        public ConfigBuilder additionalChannelInitializer(AdditionalHttpChannelInitializer additionalChannelInitializer) {
            this.additionalChannelInitializer = additionalChannelInitializer;
            return this;
        }

        public ConfigBuilder customGroup(EventLoopGroup customGroup) {
            this.customGroup = customGroup;
            return this;
        }

        public ConfigBuilder forbidForceConnect(boolean forbidForceConnect) {
            this.forbidForceConnect = forbidForceConnect;
            return this;
        }

        public Map<ChannelOption, Object> getOptions() {
            return options;
        }

        public int getMaxIdleTimeInMilliSecondes() {
            return maxIdleTimeInMilliSecondes;
        }

        public AdditionalHttpChannelInitializer getAdditionalChannelInitializer() {
            return additionalChannelInitializer;
        }

        public Map<String, Integer> getMaxPerRoute() {
            return maxPerRoute;
        }

        public int getConnectTimeOutInMilliSecondes() {
            return connectTimeOutInMilliSecondes;
        }

        public EventLoopGroup getGroup() {
            return this.customGroup;
        }

        public boolean getForbidForceConnect() {
            return this.forbidForceConnect;
        }
    }
}
