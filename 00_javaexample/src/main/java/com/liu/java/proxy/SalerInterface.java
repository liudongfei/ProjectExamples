package com.liu.java.proxy;

/**
 * 销售代理接口.
 * @Auther: liudongfei
 * @Date: 2019/3/21 15:14
 * @Description:
 */
public interface SalerInterface {
    /**
     * 获取指定衣服的价格.
     * @param name name
     * @return
     */
    int getPrice(String name);
}
