package com.liu.spring.repository;

import com.liu.spring.MySpringBootApp;
import com.liu.spring.bean.Student;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.AssertTrue;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @Auther: liudongfei
 * @Date: 2019/10/11 14:21
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MySpringBootApp.class)
public class StudentRespositoryTest {

    @Autowired
    private StudentRespository studentRespository;

    @Test
    public void findAll() {
        List<Student> studentList = studentRespository.findAll();
        Assert.assertTrue(studentList.size() > 0);
    }

    @Test
    public void count() {
        long count = studentRespository.count();
        Assert.assertTrue(count > 0);
    }
}