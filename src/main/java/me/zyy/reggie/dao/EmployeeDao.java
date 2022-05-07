package me.zyy.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zyy.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeDao extends BaseMapper<Employee> {

}