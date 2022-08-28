package com.martin.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.martin.reggie.common.R;
import com.martin.reggie.dto.DishDto;
import com.martin.reggie.entitty.Category;
import com.martin.reggie.entitty.Dish;
import com.martin.reggie.service.CategoryService;
import com.martin.reggie.service.DishFlavorService;
import com.martin.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto 接收前端参数，由于多了一个DishFlavor，故不能用Dish而要用DishDto接收（数据传输单元）
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        //构建分页器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        //构建dto的分页器
        Page<DishDto> dtoPageInfo = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //dto分页器获取dish分页器（拷贝）
        BeanUtils.copyProperties(pageInfo,dtoPageInfo,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dtoRecords = records.stream().map((item)->{
            //把除了分类名称的属性给到dishDto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //把分类名称给到dishDto
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dtoPageInfo.setRecords(dtoRecords);

//        return R.success(pageInfo);   Dish无分类名称，所以不能直接返回pageInfo,只有DishDto才有
        return R.success(dtoPageInfo);
    }

    /**
     * 根据id查询菜品信息和口味信息（dto）
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto 接收前端参数，由于多了一个DishFlavor，故不能用Dish而要用DishDto接收（数据传输单元）
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }
}
