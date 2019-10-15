package com.liu.spring.ioc.upgrade;

import com.liu.spring.ioc.bean.Collection;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring如何注入值到集合类型(List, Set, Map, and Properties).
 * @Auther: liudongfei
 * @Date: 2019/9/18 22:02
 * @Description:
 */
public class MySpringTest4 {

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring4.xml");
        Collection bean = (Collection) context.getBean("CollectionBean");
        System.out.println(bean);
    }
}
