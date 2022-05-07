package me.zyy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.zyy.reggie.DTO.SetMealDTO;
import me.zyy.reggie.entity.SetMeal;

import java.util.List;

public interface SetMealService extends IService<SetMeal> {

    /**
     *  新增套餐 并同时保存'菜品'的信息
     */
    void saveWithDishes(SetMealDTO setMealDTO);

    /**
     *  查询套餐包含菜品信息
     */
    SetMealDTO getByIdWithDishes(Long id);

    /**
     *  修改套餐表和套餐-菜品表
     */
    void updateWithDishes(SetMealDTO setMealDTO);

    /**
     *  删除套餐 同时删除关联关系
     */
    void removeWithDishes(List<Long> id);

    /**
     *  套餐状态修改
     */
    void updateStatus(int newStatus, List<Long> id);
}
