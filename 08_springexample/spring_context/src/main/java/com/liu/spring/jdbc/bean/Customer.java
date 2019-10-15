package com.liu.spring.jdbc.bean;

/**
 * 测试用户表实例.
 * @Auther: liudongfei
 * @Date: 2019/9/18 10:41
 * @Description:
 */
public class Customer {
    private int custId;
    private String name;
    private int age;

    public Customer() {
    }

    /**
     * 全属性构造器.
     * @param custId custId
     * @param name name
     * @param age age
     */
    public Customer(int custId, String name, int age) {
        this.custId = custId;
        this.name = name;
        this.age = age;
    }

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
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
        return "Customer{"
                + "custId=" + custId
                + ", name='" + name + '\''
                + ", age=" + age
                + '}';
    }
}
