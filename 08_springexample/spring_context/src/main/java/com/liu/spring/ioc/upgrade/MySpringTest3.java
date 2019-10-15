package com.liu.spring.ioc.upgrade;

import com.liu.spring.ioc.upgrade.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 通过spring自动加载类的三种方式3.
 * @Auther: liudongfei
 * @Date: 2019/3/25 00:16
 * @Description:
 */
@Component
public class MySpringTest3 {
    @Autowired
    @Qualifier("myservice2")// 多个MyService实现采用注解的value区分进行注入
    private MyService myService;

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring2.xml");
        MySpringTest3 bean = context.getBean(MySpringTest3.class);
        System.out.println(bean.myService.hello("liu"));
    }
}
