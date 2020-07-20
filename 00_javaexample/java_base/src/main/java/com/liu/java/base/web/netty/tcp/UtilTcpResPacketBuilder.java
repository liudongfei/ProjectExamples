package com.liu.java.base.web.netty.tcp;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/9 11:16
 * @Description:
 */
public class UtilTcpResPacketBuilder {
    private volatile List<ByteBuf> pendingContent;

    private volatile Throwable cause;

    private volatile UtilTcpResPacket resPacket;

    private AtomicBoolean isBuild = new AtomicBoolean(false);

    private volatile boolean success = false;

    /**
     * .
     * @return
     */
    public UtilTcpResPacket build() {
        if (isBuild.getAndSet(true)) {
            return resPacket;
        }
        UtilTcpResPacket tcpResPacket = new UtilTcpResPacket();
        resPacket = tcpResPacket;

        if (success) {
            resPacket.setSuccess(true);
            resPacket.setContent(pendingContent);
        } else {
            resPacket.setCause(cause);
        }
        return resPacket;
    }

    /**
     * .
     * @param byteBuf byteBuf
     */
    public void addContent(ByteBuf byteBuf) {
        if (null == pendingContent) {
            pendingContent = new ArrayList<ByteBuf>();
        }
        pendingContent.add(byteBuf);
    }

    /**
     * .
     * @return the contents
     */
    public List<ByteBuf> getContents() {
        return pendingContent;
    }

    /**
     * Getter method for property <tt>pendingContents</tt>.
     *
     * @return property value of pendingContents
     */
    public List<ByteBuf> getPendingContents() {
        return pendingContent;
    }

    /**
     * Setter method for property <tt>pendingContents</tt>.
     *
     * @param pendingContents value to be assigned to property pendingContents
     */
    public void setPendingContents(List<ByteBuf> pendingContents) {
        this.pendingContent = pendingContents;
    }

    /**
     * Getter method for property <tt>cause</tt>.
     *
     * @return property value of cause
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * Setter method for property <tt>cause</tt>.
     *
     * @param cause value to be assigned to property cause
     */
    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    /**
     * Getter method for property <tt>success</tt>.
     *
     * @return property value of success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Setter method for property <tt>success</tt>.
     *
     * @param success value to be assigned to property success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
