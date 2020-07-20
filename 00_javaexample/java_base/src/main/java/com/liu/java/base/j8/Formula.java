package com.liu.java.base.j8;

/**
 * 给接口添加默认方法.
 */
public interface Formula {
    double calculate(int a);
    default double sqrt(int a) {
        return Math.sqrt(a);
    }
}
