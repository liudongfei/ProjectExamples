package com.liu.java.base.web.netty.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(UtilTcpController.class);

    /**
     * main.
     * @param args args
     * @throws IOException exception
     * @throws InterruptedException exception
     * @throws ExecutionException exception
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        List<UtilTcpResPacketFuture> tcpResPacketFutureList = new ArrayList<>();
        UtilTcpClient client = new UtilTcpClient
                .ConfigBuilder()
                .maxIdleTimeInMilliSecondes(200 * 1000)
                .forbidForceConnect(false)
                .connectTimeOutInMilliSecondes(30 * 1000).build();
        int num = 1;
        for (int i = 0; i < 5000; i++) {
            UtilTcpReqPacket reqPacket = new UtilTcpReqPacket();
            reqPacket.dstIp("localhost").dstPort(8899).content("hello netty");

            UtilTcpResPacketFuture resPacketFuture = client.send(reqPacket);
            //Thread.sleep(0);
            tcpResPacketFutureList.add(resPacketFuture);
            logger.info("num:\t" + num);
            if (num == 20) {
                logger.info("error");
            }
            num++;

        }

        //for (UtilTcpResPacketFuture resPacketFuture : tcpResPacketFutureList) {
        //UtilTcpResPacket resPacket = resPacketFuture.get();
        //System.out.println("result:\t" + resPacket.getContent());
        //}

    }
}
