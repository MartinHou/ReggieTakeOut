package com.martin.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.martin.reggie.dto.SetmealDto;
import com.martin.reggie.entitty.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保持菜品和套餐关系
     */
    void saveWithDish(SetmealDto setmealDto);
}
