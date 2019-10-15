package com.liu.spring.aop.simple.advice;

import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * 该方法返回一个结果之后它将执行.
 * @Auther: liudongfei
 * @Date: 2019/9/18 23:56
 * @Description:
 */
public class HijackAfterMethod implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws Throwable {
        System.out.println("HijackAfterMethod : After method hijacked!");
    }
}
