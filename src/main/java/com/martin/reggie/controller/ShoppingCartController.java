package com.martin.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.martin.reggie.common.BaseContext;
import com.martin.reggie.common.R;
import com.martin.reggie.entitty.ShoppingCart;
import com.martin.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 将单一菜品加入购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart:{}",shoppingCart);

        //设置用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //如果菜品存在于购物车，则让数量+1
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        queryWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart oldShoppingCart = shoppingCartService.getOne(queryWrapper);
        if(oldShoppingCart!=null){
            oldShoppingCart.setNumber(oldShoppingCart.getNumber()+1);
            shoppingCartService.updateById(oldShoppingCart);
            return R.success(oldShoppingCart);
        }
        //不存在于购物车
        shoppingCart.setNumber(1);
        shoppingCartService.save(shoppingCart);
        return R.success(shoppingCart);
    }
}
