package com.liu.cdh.common.rpc.impl;

import com.liu.cdh.common.rpc.interf.ActionInterf;

/**
 * RPC服务实现.
 * @Auther: liudongfei
 * @Date: 2019/3/29 09:59
 * @Description:
 */
public class People implements ActionInterf {

    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
