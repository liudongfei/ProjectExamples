package com.liu.spring.repository;

import com.liu.spring.bean.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * student的jpa respository.
 * @Auther: liudongfei
 * @Date: 2019/10/11 14:14
 * @Description:
 */
public interface StudentRespository extends JpaRepository<Student, Integer> {
    public List<Student> findAll();

    public long count();
}
