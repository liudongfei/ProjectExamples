package com.liu.spring.jdbc.dao;

import com.liu.spring.jdbc.bean.Customer;

import java.util.List;

/**
 * customer dao 接口.
 * @Auther: liudongfei
 * @Date: 2019/9/18 10:43
 * @Description:
 */
public interface CustomerDAO {
    /**
     * 插入一条customer数据.
     * @param customer customer
     */
    public void insert(Customer customer);

    /**
     * 批量插入customer数据.
     * @param customers customers
     */
    public void insertBatch(List<Customer> customers);

    /**
     * 根据客户id查询客户.
     * @param custId custId
     * @return
     */
    public Customer findByCustomerId(int custId);

    /**
     * 查询所有的customer数据.
     * @return
     */
    public List<Customer> findAllCustomers();

    /**
     * 查询所有的customer记录数量.
     * @return
     */
    public int findAllCustomerCount();
}
