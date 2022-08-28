package com.martin.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.martin.reggie.dto.DishDto;
import com.martin.reggie.entitty.Dish;
import com.martin.reggie.entitty.DishFlavor;
import com.martin.reggie.mapper.DishMapper;
import com.martin.reggie.service.DishFlavorService;
import com.martin.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional  //多表操作加事务
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品到Dish
        this.save(dishDto); //会给Dish一个随机id，但DishFlavor里的id为null

        Long dishId = dishDto.getId();
        //获得菜品口味，并给DishFlavor的所有元素赋值id(原来为null)
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().peek((item)-> item.setDishId(dishId)).collect(Collectors.toList());

        //保存口味到DishFlavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) { //dish的id，不是dishFlavor的id
        //查询菜品基本信息，从dish
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品口味信息，从dishFlavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);
        return dishDto;
    }
}
