package com.liu.java.drools;

import com.liu.java.drools.Car;
import com.liu.java.drools.Person;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * demo.
 * @Auther: liudongfei
 * @Date: 2020/1/2 10:47
 * @Description:
 */
public class Demo {

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        //从工厂中获得KieServices实例
        KieServices kieServices = KieServices.Factory.get();
        //默认自动加载 META-INF/kmodule.xml
        //从KieServices中获得KieContainer实例，其会加载kmodule.xml文件并load规则文件
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        //kmodule.xml 中定义的 ksession name
        //建立KieSession到规则文件的通信管道
        //kieSession有状态, 维护会话状态，type=stateful  最后结束要调用dispose()
        //statelessKieSession无状态，不会维护会话状态 type=stateless
        KieSession kieSession = kieContainer.newKieSession("all-rules");
        Person p1 = new Person();
        p1.setAge(31);
        Car car = new Car();
        car.setPerson(p1);

        kieSession.insert(car);//插入到working memory
        int count = kieSession.fireAllRules();//通知规则引擎执行规则
        System.out.println(count);
        System.out.println(car.getDiscount());

        kieSession.dispose();

    }
}
