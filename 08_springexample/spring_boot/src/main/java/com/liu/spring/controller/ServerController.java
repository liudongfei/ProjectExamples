package com.liu.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SpringBoot的controller.
 * @Auther: liudongfei
 * @Date: 2019/10/8 16:22
 * @Description:
 */
@Controller
public class ServerController {

    @RequestMapping("/server")
    @ResponseBody
    public String server() {
        return "springboot2.0 server succeed !!";
    }
}
