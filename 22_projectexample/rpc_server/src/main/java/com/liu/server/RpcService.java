package com.liu.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * rpc interface impl annotation.
 * @Auther: liudongfei
 * @Date: 2019/3/25 23:07
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * value.
     * @return
     */
    Class<?> value();
}
