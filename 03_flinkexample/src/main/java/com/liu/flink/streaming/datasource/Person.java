package com.liu.flink.streaming.datasource;

import java.io.Serializable;

/**
 * @Auther: liudongfei
 * @Date: 2019/7/5 15:43
 * @Description:
 */
public class Person implements Serializable {
    private String name;
    private int age;

    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
