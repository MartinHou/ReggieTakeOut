package com.martin.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.martin.reggie.common.CustomException;
import com.martin.reggie.entitty.Category;
import com.martin.reggie.entitty.Dish;
import com.martin.reggie.entitty.Setmeal;
import com.martin.reggie.mapper.CategoryMapper;
import com.martin.reggie.service.CategoryService;
import com.martin.reggie.service.DishService;
import com.martin.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，要判断是否关联菜品或套餐
     */
    @Override
    public void remove(Long ids) {
        //查询当前分类是否关联菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0) {
            //已关联菜品，抛出业务异常
            throw new CustomException("当前分类已关联菜品，无法删除");
        }

        //查询当前分类是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            //已关联套餐，抛出业务异常
            throw new CustomException("当前分类已关联套餐，无法删除");
        }

        //删除分类
        super.removeById(ids);
    }
}
