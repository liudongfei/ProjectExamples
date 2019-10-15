package com.liu.spring.aop.upgrade;

import com.liu.spring.aop.upgrade.service.CustomerBo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * aspect注解实现aop入口类.
 * @Auther: liudongfei
 * @Date: 2019/9/19 15:40
 * @Description:
 */
public class App {
    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) throws Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                new String[] { "spring6.xml" });
        CustomerBo customerBo = (CustomerBo) appContext.getBean("customerBo");
        customerBo.addCustomer();
        customerBo.addCustomerReturnValue();
        //customerBo.addCustomerThrowException();
        customerBo.addCustomerAround("nihao");
    }
}
