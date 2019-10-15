package com.liu.spring.aop.simple.advice;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * 它会在方法执行之前执行.
 * @Auther: liudongfei
 * @Date: 2019/9/18 23:52
 * @Description:
 */
public class HijackBeforeMethod implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        System.out.println("HijackBeforeMethod : Before method hijacked!");
    }
}
