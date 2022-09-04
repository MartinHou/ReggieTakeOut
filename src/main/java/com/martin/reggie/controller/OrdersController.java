package com.martin.reggie.controller;

import com.martin.reggie.common.R;
import com.martin.reggie.entitty.Orders;
import com.martin.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * 只传来了地址、备注和支付方式信息，其他信息通过session的用户id获取，因此在service中封装该逻辑
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }
}
