package com.liu.java.reflect;

/**
 * 反射用例bean.
 * @Auther: liudongfei
 * @Date: 2019/8/30 11:14
 * @Description:
 */
public class People {
    public int id;
    private String name;
    private int age;

    public People() {
    }

    public People(String name) {
        this.name = name;
    }

    private People(int age) {
        this.age = age;
    }

    public People(String name, int age) {
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
        return "People{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", age=" + age
                + '}';
    }
}
