package com.martin.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.martin.reggie.entitty.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
