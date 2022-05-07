package me.zyy.reggie.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.zyy.reggie.common.BaseContext;
import me.zyy.reggie.common.R;
import me.zyy.reggie.entity.ShoppingCart;
import me.zyy.reggie.service.ShoppingCartService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart) {

        log.info("{}", shoppingCart);

        // 设置用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 查询条件 userId
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if (shoppingCart.getDishId() != null) {
            // 是菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            // 是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        // SQL: select * from shopping_cart where user_id = ? and dish_id = ?;
        // SQL: select * from shopping_cart where user_id = ? and set_meal_id = ?;

        // 对于菜品的多选，如果口味选的不一致，需要新增一条记录，否则直接number字段+1
        ShoppingCart record = shoppingCartService.getOne(queryWrapper);

        if (record == null) {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        } else if (!StringUtils.equals(record.getDishFlavor(), shoppingCart.getDishFlavor())) {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        } else {
            // 记录存在并且口味相同，只需要修改记录的数量字段
            record.setNumber(record.getNumber() + 1);
            shoppingCartService.updateById(record);
        }
        return R.success(1, "购物车添加成功");
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> res = shoppingCartService.list(queryWrapper);
        return R.success(1, res, "购物车数据加载成功");
    }

    @DeleteMapping("/clean")
    public R<String> clear() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success(1, "清空购物车成功");
    }

}