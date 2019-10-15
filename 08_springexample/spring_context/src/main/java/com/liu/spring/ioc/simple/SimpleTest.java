package com.liu.spring.ioc.simple;

import com.liu.spring.ioc.bean.Two;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring 配置文件加载java bean的样例.
 * @Auther: liudongfei
 * @Date: 2019/3/24 22:47
 * @Description:
 */
public class SimpleTest {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        //按照配置文件中的顺序依次加载各个类
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        Two two = (Two) context.getBean("two");
        System.out.println(two);
        context.close();//将关闭该应用程序的上下文，释放所有资源，并销毁所有缓存的单例bean
    }
}
