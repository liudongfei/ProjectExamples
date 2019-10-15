package com.liu.spring.aop.simple.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Arrays;

/**
 * @Auther: liudongfei
 * @Date: 2019/9/19 00:03
 * @Description:
 */
public class HijackAroundMethod implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("Method name : " + methodInvocation.getMethod().getName());
        System.out.println("Method arguments : " + Arrays.toString(methodInvocation.getArguments()));

        // same with MethodBeforeAdvice
        System.out.println("HijackAroundMethod : Before method hijacked!");

        try {
            // proceed to original method call
            Object result = methodInvocation.proceed();

            // same with AfterReturningAdvice
            System.out.println("HijackAroundMethod : Before after hijacked!");

            return result;

        } catch (IllegalArgumentException e) {
            // same with ThrowsAdvice
            System.out.println("HijackAroundMethod : Throw exception hijacked!");
            throw e;
        }


    }
}
