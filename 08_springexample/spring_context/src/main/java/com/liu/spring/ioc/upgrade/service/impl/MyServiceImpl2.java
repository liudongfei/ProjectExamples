package com.liu.spring.ioc.upgrade.service.impl;

import com.liu.spring.ioc.upgrade.annotation.MyAnnotation;
import com.liu.spring.ioc.upgrade.service.MyService;

/**
 * 自定义服务实现2.
 * @Auther: liudongfei
 * @Date: 2019/3/24 23:55
 * @Description:
 */
@MyAnnotation(value = "myservice2")
public class MyServiceImpl2 implements MyService {
    @Override
    public String hello(String name) {
        System.out.println("hello2" + name);
        return "hello2 " + name;
    }
}
