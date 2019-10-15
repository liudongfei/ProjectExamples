package com.liu.logmsgreplay.rpc.serviceimpl;

import com.liu.logmsgcommon.rpc.protocol.UtilTcpClientService;
import com.liu.logmsgcommon.rpc.server.RpcService;
import com.liu.logmsgreplay.netty.tcp.UtilTcpClientPool;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/9 18:45
 * @Description:
 */
@RpcService(UtilTcpClientService.class)
public class UtilTcpClientServiceImpl implements UtilTcpClientService {
    private static final Logger logger = LoggerFactory.getLogger(UtilTcpClientServiceImpl.class);
    private UtilTcpClientPool tcpClientPool;

    /**
     * constructor.
     * @param dstIp dstIp
     * @param dstPort dstPort
     * @param poolSize poolSize
     */
    public UtilTcpClientServiceImpl(String dstIp, int dstPort, int poolSize) {
        try {
            this.tcpClientPool = new UtilTcpClientPool(dstIp, dstPort, poolSize);
        } catch (InterruptedException e) {
            logger.error("error occur when create rpc server:\t", e);
        }
    }

    @Override
    public boolean sendPacket(byte[] bytes) {
        try {
            ChannelFuture conn = tcpClientPool.getConnection();
            conn.channel().writeAndFlush(Unpooled.copiedBuffer(bytes));
            tcpClientPool.returnConnection(conn);
        } catch (InterruptedException e) {
            logger.error("failed to send packet! exception:\t{}", e);
            return false;
        }
        return true;
    }

}
