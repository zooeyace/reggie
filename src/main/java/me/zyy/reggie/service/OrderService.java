package me.zyy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.zyy.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     */
    void submit(Orders orders);
}
