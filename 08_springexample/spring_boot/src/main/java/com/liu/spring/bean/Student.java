package com.liu.spring.bean;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * javaBean.
 * @Auther: liudongfei
 * @Date: 2019/10/9 10:27
 * @Description:
 */
@Data
@Entity
public class Student {
    @Id
    @GeneratedValue
    private int id;

    private String name;

    private String password;

    private int age;

    public Student() {
    }

    /**
     * 构造器.
     * @param id id
     * @param name name
     * @param password password
     * @param age age
     */
    public Student(int id, String name, String password, int age) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.age = age;
    }
}
