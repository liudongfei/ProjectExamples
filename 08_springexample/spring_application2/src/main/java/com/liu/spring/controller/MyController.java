package com.liu.spring.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * controller.
 * @Auther: liudongfei
 * @Date: 2019/10/15 15:45
 * @Description:
 */
@RestController
public class MyController {

    @Value("${env}")
    private String env;

    @RequestMapping("/env")
    public String env() {
        return env;
    }
}
