package com.martin.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.martin.reggie.entitty.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
