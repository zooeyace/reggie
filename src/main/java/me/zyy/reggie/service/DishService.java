package me.zyy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.zyy.reggie.DTO.DishDTO;
import me.zyy.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    /**
     *  保存菜品 同时关联 flavor表
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 查询 2.关联口味信息的 1.菜品信息
     */
    DishDTO getByIdWithFlavor(Long id);

    /**
     *  修改
     *      涉及到dish表和dish_flavor表.
     *      对于dish表直接更新(update)，对于dish_flavor表先删除再新增
     */
    void updateWithFlavor(DishDTO dishDTO);
}
