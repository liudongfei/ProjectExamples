package com.liu.spark.sql.bean;

import java.io.Serializable;

/**
 * bean.
 */
public class Student implements Serializable {

    private int id;
    private String name;
    private int age;

    public Student() {
    }

    /**
     * 构造器.
     * @param id id
     * @param name name
     * @param age age
     */
    public Student(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Student{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", age=" + age
                + '}';
    }

}
