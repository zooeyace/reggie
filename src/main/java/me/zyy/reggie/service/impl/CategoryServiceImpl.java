package me.zyy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.zyy.reggie.common.CustomException;
import me.zyy.reggie.dao.CategoryDao;
import me.zyy.reggie.entity.Category;
import me.zyy.reggie.entity.Dish;
import me.zyy.reggie.entity.SetMeal;
import me.zyy.reggie.service.CategoryService;
import me.zyy.reggie.service.DishService;
import me.zyy.reggie.service.SetMealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Resource
    private DishService dishService;

    @Resource
    private SetMealService setMealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<SetMeal> setMealQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        setMealQueryWrapper.eq(SetMeal::getCategoryId, id);
        if (dishService.count(dishQueryWrapper) > 0) {
            // 有关联菜品
            throw new CustomException("该分类下有关联菜品，无法删除");
        }
        if (setMealService.count(setMealQueryWrapper) > 0) {
            // 有关联套餐
            throw new CustomException("该分类下有关联套餐，无法删除");
        }

        // 没有关联
        super.removeById(id);
    }
}
