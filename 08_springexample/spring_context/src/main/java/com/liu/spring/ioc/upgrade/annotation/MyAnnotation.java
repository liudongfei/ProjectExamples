package com.liu.spring.ioc.upgrade.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解.
 * @Auther: liudongfei
 * @Date: 2019/3/24 23:51
 * @Description: 自定义注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface MyAnnotation {
    /**
     * annotation value.
     * @return
     */
    String value();
}
