package com.liu.spring.aop.simple.advice;

import org.springframework.aop.ThrowsAdvice;

/**
 * 它将在执行方法抛出一个异常后.
 * @Auther: liudongfei
 * @Date: 2019/9/19 00:00
 * @Description:
 */
public class HijackThrowException implements ThrowsAdvice {
    public void afterThrowing(IllegalArgumentException e) throws Throwable {
        System.out.println("HijackThrowException : Throw exception hijacked!");
    }

}
