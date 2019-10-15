package com.liu.spring.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Springboot的controller类.
 * @Auther: liudongfei
 * @Date: 2019/10/8 16:01
 * @Description:
 */
@Controller
public class QuickController {

    @Value("${person.name.str}")//获取配置文件中的参数值
    private String name;

    @Value("${person.age.num}")
    private int age;

    @RequestMapping("/quick")
    @ResponseBody
    public String quick() {
        return "springboot2.0 quick succeed !!\t" + name + ":\t" + age;
    }

}
