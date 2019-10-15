package com.liu.spring.ioc.upgrade;

import com.liu.spring.ioc.upgrade.annotation.MyAnnotation;
import com.liu.spring.ioc.upgrade.service.MyService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**通过spring自动加载类的三种方式2.
 * @Auther: liudongfei
 * @Date: 2019/3/25 00:13
 * @Description:
 */
public class MySpringTest2 {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring2.xml");
        Map<String, Object> beans = context.getBeansWithAnnotation(MyAnnotation.class);
        for (Object bean : beans.values()) {
            // 强制类型转换
            MyService myService = (MyService)bean;
            String liu = myService.hello("liu");
            System.out.println(liu);
        }
    }
}
