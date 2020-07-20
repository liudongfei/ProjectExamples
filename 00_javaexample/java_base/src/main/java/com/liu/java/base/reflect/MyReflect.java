package com.liu.java.base.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 简单的反射用法样例.
 * @Auther: liudongfei
 * @Date: 2019/3/21 13:40
 * @Description:
 */
public class MyReflect {
    /**
     * main.
     * @param args args
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        String className = "com.liu.java.base.reflect.People";
        Class peopleClass = Class.forName(className);
        People people = (People) peopleClass.newInstance();//调用类无参构造器创建对象
        System.out.println(people);
        Constructor constructor = peopleClass.getConstructor(String.class, int.class);//获取类的public有参构造器
        People people1 = (People) constructor.newInstance("liu", 2);
        System.out.println(people1);
        Constructor constructor1 = peopleClass.getConstructor(String.class);
        People people2 = (People) constructor1.newInstance("liu");
        System.out.println(people2);
        Constructor declaredConstructor = peopleClass.getDeclaredConstructor(int.class);//获取类的私有构造器
        declaredConstructor.setAccessible(true);//强制取消权限检查
        People people3 = (People) declaredConstructor.newInstance(10);
        System.out.println(people2);
        Field field = peopleClass.getField("id");//获取类的public属性
        field.set(people, 1);//给对象设定属性值
        System.out.println(people);
        Field field1 = peopleClass.getDeclaredField("name");//获取类的私有属性
        field1.setAccessible(true);//强制取消私有属性的权限检查
        field1.set(people, "sun");
        System.out.println(people);
        Method method = peopleClass.getMethod("getName");//获取类的无参数public方法
        String invoke = (String)method.invoke(people);//执行方法,获取返回值
        System.out.println(invoke);
        Method method1 = peopleClass.getMethod("setName", String.class);
        method1.invoke(people, "li");
        System.out.println(people);
        Method method2 = peopleClass.getDeclaredMethod("getId");//获取类的无参数私有方法
        method2.setAccessible(true);
        Object invoke1 = method2.invoke(people);
        System.out.println(invoke1);
        Method method3 = peopleClass.getDeclaredMethod("setId", int.class);//获取类的有参数的私有方法
        method3.setAccessible(true);
        method3.invoke(people, 2);
        System.out.println(people);
        ClassLoader classLoader = peopleClass.getClassLoader();//获取类的类加载器
        System.out.println(classLoader);
        Class[] interfaces = peopleClass.getInterfaces();//获取当前类的接口
        for (Class inter : interfaces) {
            System.out.println(inter);
        }
        Class superclass = peopleClass.getSuperclass();//获取当前类的父类
        System.out.println(superclass);

        Type genericSuperclass = peopleClass.getGenericSuperclass();//获取当前类的直接父类的类型
        System.out.println(genericSuperclass);


        System.out.println(peopleClass.isArray());//当前类是否是数组
        System.out.println(peopleClass.isEnum());//当前类是否是枚举
        System.out.println(peopleClass.isInterface());//当前类是否是接口

        System.out.println(peopleClass.isInstance(people));//如果people是peopleClass的实例则返回true

        System.out.println(people instanceof People);//同上



    }
}
