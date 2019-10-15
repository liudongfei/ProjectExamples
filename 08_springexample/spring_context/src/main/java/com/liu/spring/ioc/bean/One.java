package com.liu.spring.ioc.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * spring bean.
 * @Auther: liudongfei
 * @Date: 2019/3/24 22:47
 * @Description:
 */

public class One implements ApplicationContextAware, InitializingBean, DisposableBean {
    private String one;

    public One(String one) {
        this.one = one;
        System.out.println("ONE" + one);
    }

    @Override//初始化完成后最后运行该方法
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet");
    }

    @Override//初始化完成后紧接着运行该方法
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("setApplicationContext");
    }

    @Override
    public String toString() {
        return "One{"
                + "one='" + one + '\''
                + '}';
    }

    @Override // 将运行 destroy()在 Spring 容器释放该 bean 之后
    public void destroy() throws Exception {
        System.out.println("Spring Container is destroy! One clean up");
    }
}
