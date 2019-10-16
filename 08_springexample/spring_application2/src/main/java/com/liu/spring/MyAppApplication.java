package com.liu.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * app2.
 * @Auther: liudongfei
 * @Date: 2019/10/15 15:43
 * @Description:
 */
@SpringBootApplication
@EnableEurekaClient
public class MyAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyAppApplication.class, args);
    }
}
