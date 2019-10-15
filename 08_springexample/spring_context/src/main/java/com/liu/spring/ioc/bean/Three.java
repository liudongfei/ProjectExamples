package com.liu.spring.ioc.bean;

import org.springframework.stereotype.Component;

/**
 * spring bean.
 * @Auther: liudongfei
 * @Date: 2019/3/24 22:58
 * @Description:
 */
@Component//用于在对包进行扫描时，标记初始化
public class Three {

    public Three() {
        System.out.println("THREE");
    }
}
