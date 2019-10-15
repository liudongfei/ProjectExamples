package com.liu.spring.ioc.upgrade.service.impl;

import com.liu.spring.ioc.upgrade.annotation.MyAnnotation;
import com.liu.spring.ioc.upgrade.service.MyService;

/**
 * 自定义服务实现1.
 * @Auther: liudongfei
 * @Date: 2019/3/24 23:55
 * @Description:
 */
@MyAnnotation(value = "myservice1")
public class MyServiceImpl1 implements MyService {
    @Override
    public String hello(String name) {
        System.out.println("hello1" + name);
        return "hello1 " + name;
    }
}
