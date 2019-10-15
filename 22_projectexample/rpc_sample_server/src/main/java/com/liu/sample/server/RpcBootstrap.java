package com.liu.sample.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * rpc server main.
 * @Auther: liudongfei
 * @Date: 2019/3/26 10:52
 * @Description:
 */
public class RpcBootstrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
