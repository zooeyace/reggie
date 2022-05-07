package me.zyy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.zyy.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    /**
     *  自定义删除，判断当前分类id是否关联到菜品或套餐
     */
    void remove(Long id);
}
