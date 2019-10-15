package com.liu.spring.jdbc.dao.impl;

import com.liu.spring.jdbc.bean.Customer;
import com.liu.spring.jdbc.dao.CustomerDAO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用template实现jdbc dao.
 * @Auther: liudongfei
 * @Date: 2019/9/18 11:15
 * @Description:
 */
public class JdbcTemplateDAO extends JdbcDaoSupport implements CustomerDAO {
    //JdbcDaoSupport类已经有了public final void setDataSource(DataSource dataSource)了
    //不用重写也不能重写

    @Override
    public void insert(Customer customer) {
        String sql = "INSERT INTO customer (CUST_ID, NAME, AGE) VALUES (?, ?, ?)";
        getJdbcTemplate().update(sql,
                new Object[]{customer.getCustId(), customer.getName(), customer.getAge()});

    }

    @Override
    public void insertBatch(List<Customer> customers) {
        String sql = "INSERT INTO customer (CUST_ID, NAME, AGE) VALUES (?, ?, ?)";
        List<Object[]> parameters = new ArrayList<Object[]>();

        for (Customer cust : customers) {
            parameters.add(new Object[]{cust.getCustId(), cust.getName(), cust.getAge()});
        }
        getJdbcTemplate().batchUpdate(sql, parameters);

    }

    @Override
    public Customer findByCustomerId(int custId) {
        String sql = "SELECT * FROM customer WHERE CUST_ID = ?";
        Customer customer = getJdbcTemplate().query(sql, new Object[]{custId}, new ResultSetExtractor<Customer>() {
            @Override
            public Customer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Customer customer = null;
                if (resultSet.next()) {
                    customer = new Customer(
                            resultSet.getInt("CUST_ID"),
                            resultSet.getString("NAME"),
                            resultSet.getInt("Age")
                    );
                }
                return customer;
            }
        });
        return customer;
    }

    @Override
    public List<Customer> findAllCustomers() {
        String sql = "SELECT * FROM customer";
        List<Customer> customerList = getJdbcTemplate().query(sql, new RowMapper<Customer>() {
            @Override
            public Customer mapRow(ResultSet resultSet, int i) throws SQLException {
                System.out.println(i);
                return new Customer(
                        resultSet.getInt("CUST_ID"),
                        resultSet.getString("NAME"),
                        resultSet.getInt("Age")
                );
            }
        });
        return customerList;
    }

    @Override
    public int findAllCustomerCount() {
        String sql = "SELECT COUNT(*) FROM customer";
        int count = getJdbcTemplate().queryForObject(sql, Integer.class);
        return count;
    }
}
