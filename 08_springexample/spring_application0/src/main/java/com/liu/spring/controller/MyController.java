package com.liu.spring.controller;

import com.liu.spring.client.App1Client;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private App1Client app1Client;

    @RequestMapping("/hello")
    public String hello() {
        return "hello app-0!";
    }

    @RequestMapping("/Rhello")
    public String rhello() {
        return app1Client.hello();
    }
}
