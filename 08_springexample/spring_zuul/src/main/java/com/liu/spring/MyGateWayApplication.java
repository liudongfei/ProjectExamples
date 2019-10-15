package com.liu.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * gateway.
 * @Auther: liudongfei
 * @Date: 2019/10/14 16:59
 * @Description:
 */
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class MyGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyGateWayApplication.class, args);
    }
}
