package com.martin.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.martin.reggie.entitty.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
