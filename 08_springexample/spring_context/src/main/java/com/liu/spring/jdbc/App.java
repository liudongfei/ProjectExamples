package com.liu.spring.jdbc;

import com.liu.spring.jdbc.bean.Customer;
import com.liu.spring.jdbc.dao.CustomerDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * spring jdbc 样例.
 * @Auther: liudongfei
 * @Date: 2019/9/18 10:55
 * @Description:
 */
public class App {

    /**
     * main.
     * @param args args
     */
    public static void main( String[] args ) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("spring3.xml");

        CustomerDAO customerDAO = (CustomerDAO) context.getBean("customerDAO");
        Customer customer = new Customer(5, "liudf",29);
        Customer customer2 = new Customer(6, "liudf",29);
        ArrayList<Customer> customers = new ArrayList<>();
        customers.add(customer);
        customers.add(customer2);
        //customerDAO.insert(customer);
        //Customer customer1 = customerDAO.findByCustomerId(1);
        List<Customer> customerList = customerDAO.findAllCustomers();
        //System.out.println(customer1);
        System.out.println(customerList);
        int count = customerDAO.findAllCustomerCount();
        System.out.println(count);
        customerDAO.insertBatch(customers);


    }
}


