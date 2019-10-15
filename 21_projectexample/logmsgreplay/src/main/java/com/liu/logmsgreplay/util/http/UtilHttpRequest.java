package com.liu.logmsgreplay.util.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/8 16:11
 * @Description:
 */
public class UtilHttpRequest {
    private URI uri;
    private Map<String, Object> headers;
    private ByteBuf content;
    private static final Charset DEFAUT_CHARSET = Charset.forName("UTF8");

    public UtilHttpRequest uri(String uri) {
        this.uri = URI.create(uri);
        return this;
    }

    /**
     * uir.
     * @param uri uri
     * @return
     */
    public UtilHttpRequest uri(URI uri) {
        if (null == uri) {
            throw new NullPointerException("uri");
        }
        this.uri = uri;
        return this;
    }

    /**
     * header.
     * @param key key
     * @param value value
     * @return
     */
    public UtilHttpRequest header(String key, Object value) {
        if (null == this.headers) {
            this.headers = new HashMap<>();
        }
        headers.put(key, value);
        return this;
    }

    /**
     * headers.
     * @param headers headers.
     * @return
     */
    public UtilHttpRequest headers(Map<String, Object> headers) {
        if (null == headers) {
            throw new NullPointerException("headers");
        }

        if (null == this.headers) {
            this.headers = new HashMap<>();
        }

        this.headers.putAll(headers);
        return this;
    }

    /**
     * content.
     * @param content content
     * @return
     */
    public UtilHttpRequest content(ByteBuf content) {
        if (null == content) {
            throw new NullPointerException("content");
        }

        this.content = content;
        return this;
    }

    /**
     * content.
     * @param content content
     * @return
     */
    public UtilHttpRequest content(byte[] content) {
        if (null == content) {
            throw new NullPointerException("content");
        }
        this.content = Unpooled.copiedBuffer(content);
        return this;
    }

    /**
     * content.
     * @param content content
     * @param charset charset
     * @return
     */
    public UtilHttpRequest content(String content, Charset charset) {
        if (null == content) {
            throw new NullPointerException("content");
        }
        charset = null == charset ? DEFAUT_CHARSET : charset;
        this.content = Unpooled.copiedBuffer(content, charset);
        return this;
    }

    public URI getUri() {
        return uri;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public ByteBuf getContent() {
        return content;
    }
}
