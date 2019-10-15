package com.liu.spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * app0 controller.
 * @Auther: liudongfei
 * @Date: 2019/10/14 14:56
 * @Description:
 */
@RestController
public class MyController {

    @RequestMapping("/hello")
    public String hello() {
        return "hello app-1!";
    }

    @RequestMapping("/quick")
    public String quick() {
        return "hello zuul";
    }
}
