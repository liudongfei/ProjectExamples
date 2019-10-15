package com.liu.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * config server.
 * @Auther: liudongfei
 * @Date: 2019/10/14 16:10
 * @Description:
 */
@SpringBootApplication
@EnableEurekaClient
@EnableConfigServer
public class MyConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyConfigApplication.class, args);
    }
}
