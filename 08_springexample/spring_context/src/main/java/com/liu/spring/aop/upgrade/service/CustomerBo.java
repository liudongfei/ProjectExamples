package com.liu.spring.aop.upgrade.service;

/**
 * 自定义的简单客户业务接口.
 * @Auther: liudongfei
 * @Date: 2019/9/19 09:38
 * @Description:
 */
public interface CustomerBo {

    void addCustomer();

    String addCustomerReturnValue();

    void addCustomerThrowException() throws Exception;

    void addCustomerAround(String name);
}


