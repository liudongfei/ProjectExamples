package com.liu.logmsgreplay.util.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/9 11:07
 * @Description:
 */
public class UtilTcpReqPacket {
    private String dstIp;
    private Integer dstPort;
    private ByteBuf content;
    private static final Charset DEFAUT_CHARSET = Charset.forName("UTF8");

    /**
     * set dstIp.
     * @param dstIp dstIp
     * @return
     */
    public UtilTcpReqPacket dstIp(String dstIp) {
        if (dstIp == null) {
            throw new NullPointerException("dstIp");
        }
        this.dstIp = dstIp;
        return this;
    }

    /**
     * set dstPort.
     * @param dstPort dstPort
     * @return
     */
    public UtilTcpReqPacket dstPort(Integer dstPort) {
        if (dstPort == null) {
            throw new NullPointerException("dstPort");
        }
        this.dstPort = dstPort;
        return this;
    }

    /**
     * set byte[] content.
     * @param content content
     * @return
     */
    public UtilTcpReqPacket content(byte[] content) {
        if (content == null) {
            throw new NullPointerException("content");
        }
        this.content = Unpooled.copiedBuffer(content);
        return this;
    }

    /**
     * set string content.
     * @param content content
     * @return
     */
    public UtilTcpReqPacket content(String content) {
        if (content == null) {
            throw new NullPointerException("content");
        }
        this.content = Unpooled.copiedBuffer(content, DEFAUT_CHARSET);
        return this;
    }

    public String getDstIp() {
        return dstIp;
    }

    public Integer getDstPort() {
        return dstPort;
    }

    public ByteBuf getContent() {
        return content;
    }
}
