package com.liu.spring.mapper;

import com.liu.spring.bean.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * mybatis的mapper.
 * @Auther: liudongfei
 * @Date: 2019/10/9 10:29
 * @Description:
 */
@Mapper
public interface StudentMapper {
    public List<Student> queryStudents();
}
