package com.martin.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.martin.reggie.common.R;
import com.martin.reggie.dto.SetmealDto;
import com.martin.reggie.entitty.Category;
import com.martin.reggie.entitty.Setmeal;
import com.martin.reggie.entitty.SetmealDish;
import com.martin.reggie.service.CategoryService;
import com.martin.reggie.service.SetmealDishService;
import com.martin.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);
        //条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);
//        return R.success(pageInfo);   不能直接返回，因为分类名称在setmeal中没有，只有分类id
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");//由于范型不一样，不要拷records
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> setmealRecords = records.stream().map((item) -> {
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            if (category != null) { //核心逻辑：添加分类名
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(setmealRecords);
        return R.success(dtoPage);
    }
}
