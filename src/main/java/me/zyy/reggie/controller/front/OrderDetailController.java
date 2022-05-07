package me.zyy.reggie.controller.front;

import lombok.extern.slf4j.Slf4j;
import me.zyy.reggie.service.OrderDetailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 订单明细
 */
@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {

    @Resource
    private OrderDetailService orderDetailService;

}