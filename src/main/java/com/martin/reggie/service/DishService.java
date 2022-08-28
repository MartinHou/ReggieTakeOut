package com.martin.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.martin.reggie.dto.DishDto;
import com.martin.reggie.entitty.Dish;

public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，并同时插入口味数据，要操作dish和dishFlavor两张表
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品信息和口味信息
     */
    DishDto getByIdWithFlavor(Long id);
}
