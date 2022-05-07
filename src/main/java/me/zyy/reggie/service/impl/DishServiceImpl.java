package me.zyy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.zyy.reggie.DTO.DishDTO;
import me.zyy.reggie.dao.DishDao;
import me.zyy.reggie.entity.Dish;
import me.zyy.reggie.entity.DishFlavor;
import me.zyy.reggie.service.DishFlavorService;
import me.zyy.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        // 基本信息保存至 dish表
        this.save(dishDTO);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 设置 dishId
        flavors = flavors.stream().map(e -> {
            e.setDishId(dishDTO.getId());
            return e;
        }).collect(Collectors.toList());

        // dishFlavor表
        dishFlavorService.saveBatch(flavors);


    }

    @Override
    public DishDTO getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDTO res = new DishDTO();
        BeanUtils.copyProperties(dish, res);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);

        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        res.setFlavors(list);
        return res;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        // 直接更新dish表
        this.updateById(dishDTO);

        // 单独对口味表处理
        // 先删除
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDTO.getId());
        dishFlavorService.remove(queryWrapper);

        // 再新增新的
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 此时flavors内每一个每一个对象都是没有dishId属性的
        flavors = flavors.stream().map(e -> {
            e.setDishId(dishDTO.getId());
            return e;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
