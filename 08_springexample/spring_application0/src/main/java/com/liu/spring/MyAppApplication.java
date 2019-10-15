package com.liu.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * app.
 * @Auther: liudongfei
 * @Date: 2019/10/12 14:23
 * @Description:
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MyAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyAppApplication.class, args);
    }
}
