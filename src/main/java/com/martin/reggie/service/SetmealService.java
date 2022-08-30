package com.martin.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.martin.reggie.dto.SetmealDto;
import com.martin.reggie.entitty.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保持菜品和套餐关系
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除套餐和菜品的关联关系
     */
    void removeWithDish(List<Long> ids);
}
