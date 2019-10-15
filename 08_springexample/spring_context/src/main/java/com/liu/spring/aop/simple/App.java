package com.liu.spring.aop.simple;

import com.liu.spring.aop.simple.service.CustomerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring aop样例 .
 * @Auther: liudongfei
 * @Date: 2019/9/18 23:49
 * @Description:
 */
public class App {

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                new String[] { "spring5.xml" });
        // 无通知 或自动代理类
        CustomerService cust = (CustomerService) appContext.getBean("customerService");
        // 所有方法执行之前（或之后、异常抛出后、环绕）通知
        //CustomerService cust = (CustomerService) appContext.getBean("customerServiceProxy");

        System.out.println("*************************");
        cust.printName();
        System.out.println("*************************");
        cust.printURL();
        System.out.println("*************************");
        try {
            cust.printThrowException();
        } catch (Exception e) {
            e.getMessage();
        }

    }


}
