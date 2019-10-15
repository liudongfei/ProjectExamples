package com.liu.logmsgcommon.rpc.protocol;


/**
 * util tcp client service interface.
 * @Auther: liudongfei
 * @Date: 2019/4/9 20:39
 * @Description:
 */
public interface UtilTcpClientService {
    boolean sendPacket(byte[] bytes);
}
