package com.liu.spring.aop.upgrade.service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Arrays;

/**
 * AspectJ切入点.
 * @Auther: liudongfei
 * @Date: 2019/9/19 15:30
 * @Description:
 */
@Aspect
public class LoggingAspect {

    /**
     * 在执行某个方法之前执行.
     * @param joinPoint joinPoint
     */
    @Before("execution(* com.liu.spring.aop.upgrade.service.CustomerBo.addCustomer(..))")
    public void logBefore(JoinPoint joinPoint) { //方法将在 customerBo接口的 addCustomer()方法的执行之前被执行

        System.out.println("logBefore() is running!");
        System.out.println("hijacked : " + joinPoint.getSignature().getName());
        System.out.println("******");
    }

    /**
     * 在执行某个方法之后执行.
     * @param joinPoint joinPoint
     */
    @After("execution(* com.liu.spring.aop.upgrade.service.CustomerBo.addCustomer(..))")
    public void logAfter(JoinPoint joinPoint) {

        System.out.println("logAfter() is running!");
        System.out.println("hijacked : " + joinPoint.getSignature().getName());
        System.out.println("******");

    }

    /**
     * 在方法返回值之后执行.
     * @param joinPoint joinYiibai
     * @param result result 返回的值
     */
    @AfterReturning(pointcut =
            "execution(* com.liu.spring.aop.upgrade.service.CustomerBo.addCustomerReturnValue(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {

        System.out.println("logAfterReturning() is running!");
        System.out.println("hijacked : " + joinPoint.getSignature().getName());
        System.out.println("Method returned value is : " + result);
        System.out.println("******");
    }


    /**
     * 在方法抛出异常之后执行.
     * @param joinPoint joinPoint
     * @param error error 抛出的异常
     */
    @AfterThrowing(
            pointcut = "execution(* com.liu.spring.aop.upgrade.service.CustomerBo.addCustomerThrowException(..))",
            throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {

        System.out.println("logAfterThrowing() is running!");
        System.out.println("hijacked : " + joinPoint.getSignature().getName());
        System.out.println("Exception : " + error);
        System.out.println("******");

    }

    /**
     * 环绕执行方法执行.
     * @param joinPoint joinPoint
     * @throws Throwable th
     */
    @Around("execution(* com.liu.spring.aop.upgrade.service.CustomerBo.addCustomerAround(..))")
    public void logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        System.out.println("logAround() is running!");
        System.out.println("hijacked method : " + joinPoint.getSignature().getName());
        System.out.println("hijacked arguments : " + Arrays.toString(joinPoint.getArgs()));

        System.out.println("Around before is running!");
        joinPoint.proceed(); //continue on the intercepted method
        System.out.println("Around after is running!");

        System.out.println("******");

    }







}
