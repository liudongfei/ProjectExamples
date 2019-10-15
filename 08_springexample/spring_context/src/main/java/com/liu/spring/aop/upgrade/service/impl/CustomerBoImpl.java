package com.liu.spring.aop.upgrade.service.impl;

import com.liu.spring.aop.upgrade.service.CustomerBo;

/**
 * 自定义的简单客户业务接口实现.
 * @Auther: liudongfei
 * @Date: 2019/9/19 15:27
 * @Description:
 */
public class CustomerBoImpl implements CustomerBo {
    @Override
    public void addCustomer() {
        System.out.println("addCustomer() is running ");
    }

    @Override
    public String addCustomerReturnValue() {
        System.out.println("addCustomerReturnValue() is running ");
        return "abc";
    }

    @Override
    public void addCustomerThrowException() throws Exception {
        System.out.println("addCustomerThrowException() is running ");
        throw new Exception("Generic Error");
    }

    @Override
    public void addCustomerAround(String name) {
        System.out.println("addCustomerAround() is running, args : " + name);
    }


}
