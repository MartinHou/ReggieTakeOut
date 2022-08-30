package com.martin.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.martin.reggie.common.CustomException;
import com.martin.reggie.dto.SetmealDto;
import com.martin.reggie.entitty.Setmeal;
import com.martin.reggie.entitty.SetmealDish;
import com.martin.reggie.mapper.SetmealMapper;
import com.martin.reggie.service.SetmealDishService;
import com.martin.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐信息，更新Setmeal表
        this.save(setmealDto);

        //保存套餐菜品关系，更新SetmealDish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().peek((item) -> item.setSetmealId(setmealDto.getId())).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询售卖状态，只能删除停售的 select count(*) from setmeal where id in (1,2) and status=1
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐售卖中，不能删除");
        }

        //删除套餐表中数据 setmeal
        this.removeByIds(ids);

        //删除关系表中数据 setmealDish
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper1);
    }
}
