package com.martin.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.martin.reggie.common.R;
import com.martin.reggie.dto.DishDto;
import com.martin.reggie.entitty.Category;
import com.martin.reggie.entitty.Dish;
import com.martin.reggie.entitty.DishFlavor;
import com.martin.reggie.service.CategoryService;
import com.martin.reggie.service.DishFlavorService;
import com.martin.reggie.service.DishService;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
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
        BeanUtils.copyProperties(pageInfo, dtoPageInfo, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dtoRecords = records.stream().map((item) -> {
            //把除了分类名称的属性给到dishDto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //把分类名称给到dishDto
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
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
     *
     * @param dishDto 接收前端参数，由于多了一个DishFlavor，故不能用Dish而要用DishDto接收（数据传输单元）
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询菜品数据
     *
     * @param dish 通过categoryId进行获取dish
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId, dish.getCategoryId());
//        //只要启售
//        queryWrapper.eq(Dish::getStatus, 1);
//        //排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtos = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //先从redis获取缓存
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtos != null) {
            //若存在，直接返回
            return R.success(dishDtos);
            //若不在缓存，在return前将数据库中内容放到redis
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //只要启售
        queryWrapper.eq(Dish::getStatus, 1);
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        dishDtos = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //获取Flavors
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, dishId);
            queryWrapper1.orderByDesc(DishFlavor::getUpdateTime);
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavors);
            //获取category
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());

        //将查到的内容放到redis
        redisTemplate.opsForValue().set(key, dishDtos,60, TimeUnit.MINUTES);

        return R.success(dishDtos);
    }
}
