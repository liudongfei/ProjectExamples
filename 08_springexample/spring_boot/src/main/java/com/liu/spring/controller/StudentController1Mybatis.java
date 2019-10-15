package com.liu.spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liu.spring.bean.Student;
import com.liu.spring.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 对student进行操作的controller,集成mybatis.
 * @Auther: liudongfei
 * @Date: 2019/10/9 10:34
 * @Description:
 */
@Controller
public class StudentController1Mybatis {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 查询.
     * @throws JsonProcessingException e
     * @return
     */
    @RequestMapping("/queryStudents1")
    @ResponseBody
    public String queryStudents() throws JsonProcessingException {
        //从redis缓存中获得指定的数据
        String userListData = redisTemplate.boundValueOps("student.findAll").get();
        //如果redis中没有数据的话
        if (null == userListData) {
            //查询数据库获得数据
            List<Student> all = studentMapper.queryStudents();
            //转换成json格式字符串
            ObjectMapper om = new ObjectMapper();

            userListData = om.writeValueAsString(all);
            //将数据存储到redis中，下次在查询直接从redis中获得数据，不用在查询数据库
            redisTemplate.boundValueOps("student.findAll").set(userListData);
            System.out.println("===============从数据库获得数据===============");
        } else {
            System.out.println("===============从redis缓存中获得数据===============");
        }
        return userListData;
    }
}
