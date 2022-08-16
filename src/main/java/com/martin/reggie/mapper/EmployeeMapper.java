package com.martin.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.martin.reggie.entitty.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
