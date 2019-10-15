package com.liu.logmsgreplay.util.tcp;

import com.liu.logmsgreplay.util.http.AdditionalHttpChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * simple netty tcp client.
 * @Auther: liudongfei
 * @Date: 2019/4/8 10:05
 * @Description:
 */
public class UtilTcpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilTcpClient.class);
    private ConfigBuilder configBuilder;
    private UtilTcpChannelPool tcpChannelPool;

    /**
     * .
     * @param configBuilder configBuilder
     */
    public UtilTcpClient(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        this.tcpChannelPool = new UtilTcpChannelPool(configBuilder.getMaxPerRoute(), configBuilder
                .getConnectTimeOutInMilliSecondes(), configBuilder.getMaxIdleTimeInMilliSecondes(),
                configBuilder.getForbidForceConnect(), configBuilder.getAdditionalChannelInitializer(),
                configBuilder.getOptions(), configBuilder.getGroup());
    }

    public ConfigBuilder getConfigBuilder() {
        return configBuilder;
    }

    public void setConfigBuilder(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
    }

    public UtilTcpResPacketFuture send(UtilTcpReqPacket reqPacket) throws IOException, InterruptedException {
        InetSocketAddress route = new InetSocketAddress(reqPacket.getDstIp(), reqPacket.getDstPort());
        return tcpChannelPool.sendPacket(route, reqPacket);
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

        public UtilTcpClient build() {
            return new UtilTcpClient(this);
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

        public ConfigBuilder additionalChannelInitializer(
                AdditionalHttpChannelInitializer additionalChannelInitializer) {
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
