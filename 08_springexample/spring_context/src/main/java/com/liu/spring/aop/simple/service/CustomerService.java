package com.liu.spring.aop.simple.service;

/**
 * 简单的客户服务类.
 * @Auther: liudongfei
 * @Date: 2019/9/18 23:48
 * @Description:
 */
public class CustomerService {
    private String name;
    private String url;

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void printName() {
        System.out.println("Customer name : " + this.name);
    }

    public void printURL() {
        System.out.println("Customer website : " + this.url);
    }

    public void printThrowException() {
        throw new IllegalArgumentException();
    }

}


