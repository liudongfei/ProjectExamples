package com.liu.logmsgreplay.util.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.List;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/9 11:14
 * @Description:
 */
public class UtilTcpResPacket {
    private volatile List<ByteBuf> content;
    private volatile boolean success = false;
    private volatile Throwable cause;

    /**
     * get content.
     * @return
     */
    public String getContent() {
        return getContent(CharsetUtil.UTF_8);
    }

    /**
     * get Content.
     * @param charset charset
     * @return
     */
    public String getContent(Charset charset) {
        if (null == content || 0 == content.size()) {
            return null;
        }
        StringBuilder responseBody = new StringBuilder();
        for (ByteBuf cont : content) {
            responseBody.append(cont.toString(charset));
        }

        return responseBody.toString();
    }

    public void setContent(List<ByteBuf> content) {
        this.content = content;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
