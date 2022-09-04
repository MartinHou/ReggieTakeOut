package com.martin.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.martin.reggie.entitty.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
