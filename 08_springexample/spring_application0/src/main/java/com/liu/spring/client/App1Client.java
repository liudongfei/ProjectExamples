package com.liu.spring.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * app1 client.
 * @Auther: liudongfei
 * @Date: 2019/10/14 15:06
 * @Description:
 */
@FeignClient(name = "app1")
public interface App1Client {

    @RequestMapping("/hello")
    String hello();
}
