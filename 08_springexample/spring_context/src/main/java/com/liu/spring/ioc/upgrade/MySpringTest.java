package com.liu.spring.ioc.upgrade;

import com.liu.spring.ioc.upgrade.annotation.MyAnnotation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 通过spring自动加载类的三种方式1.
 * @Auther: liudongfei
 * @Date: 2019/3/24 23:59
 * @Description:
 */
//@Component//在运行myspringtest3时注视掉
public class MySpringTest implements ApplicationContextAware {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring2.xml");
    }

    @Override//初始化完成后紧接着运行该方法
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(MyAnnotation.class);
        for (Object obj : beansWithAnnotation.values()) {
            String value = obj.getClass().getAnnotation(MyAnnotation.class).value();
            System.out.println("注解的value为：\t" + value);
            try {
                // 使用反射方法调用已加载
                Method hello = obj.getClass().getMethod("hello", String.class);
                Object liu = hello.invoke(obj, "liu");
                System.out.println(liu);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
