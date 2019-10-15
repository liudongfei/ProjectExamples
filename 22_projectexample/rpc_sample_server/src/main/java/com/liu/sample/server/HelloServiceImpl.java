package com.liu.sample.server;

import com.liu.sample.client.HelloService;
import com.liu.sample.client.Person;
import com.liu.server.RpcService;

/**
 * rpc interface impl.
 * @Auther: liudongfei
 * @Date: 2019/3/26 10:55
 * @Description:
 */
@RpcService(value = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }

    @Override
    public String hello(Person person) {
        return "hello " + person;
    }
}
