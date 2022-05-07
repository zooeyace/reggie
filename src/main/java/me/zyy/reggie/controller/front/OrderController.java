package me.zyy.reggie.controller.front;

import lombok.extern.slf4j.Slf4j;
import me.zyy.reggie.common.R;
import me.zyy.reggie.entity.Orders;
import me.zyy.reggie.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order) {
        //log.info("订单数据：{}", order);
        orderService.submit(order);
        return R.success(1, "下单成功");
    }
}