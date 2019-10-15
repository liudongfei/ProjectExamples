package com.liu.sample.client;

/**
 * defind rpc interface.
 * @Auther: liudongfei
 * @Date: 2019/3/26 10:46
 * @Description:
 */
public interface HelloService {
    String hello(String name);

    String hello(Person person);
}
