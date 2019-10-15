package com.liu.logmsgreplay.util.tcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * .
 * @Auther: liudongfei
 * @Date: 2019/4/9 11:17
 * @Description:
 */
public class UtilTcpController {
    /**
     * main.
     * @param args args
     * @throws IOException exception
     * @throws InterruptedException exception
     * @throws ExecutionException exception
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        List<UtilTcpResPacketFuture> tcpResPacketFutureList = new ArrayList<>();
        UtilTcpClient client = new UtilTcpClient.ConfigBuilder().maxIdleTimeInMilliSecondes(200 * 1000)
                .connectTimeOutInMilliSecondes(30 * 1000).build();
        for (int i = 0; i < 500; i++) {
            UtilTcpReqPacket reqPacket = new UtilTcpReqPacket();
            reqPacket.dstIp("localhost").dstPort(8898).content("hello netty");
            UtilTcpResPacketFuture resPacketFuture = client.send(reqPacket);
            Thread.sleep(4);
            tcpResPacketFutureList.add(resPacketFuture);
        }
        for (UtilTcpResPacketFuture resPacketFuture : tcpResPacketFutureList) {
            UtilTcpResPacket resPacket = resPacketFuture.get();
            System.out.println("result:\t" + resPacket.getContent());
        }

    }
}
