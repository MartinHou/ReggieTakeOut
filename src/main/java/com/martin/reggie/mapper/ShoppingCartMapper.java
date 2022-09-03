package com.martin.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.martin.reggie.entitty.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
