package com.liu.sample.app;

import com.liu.client.RpcProxy;
import com.liu.sample.client.HelloService;
import com.liu.sample.client.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * test RPC example.
 * @Auther: liudongfei
 * @Date: 2019/3/26 10:45
 * @Description:
 */
@Component
public class App {
    @Autowired
    private RpcProxy rpcProxy;

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        App bean = context.getBean(App.class);
        HelloService helloService = bean.rpcProxy.create(HelloService.class);
        String liu = helloService.hello(new Person("xiaoliu", 10));
        System.out.println(liu);
    }
}
