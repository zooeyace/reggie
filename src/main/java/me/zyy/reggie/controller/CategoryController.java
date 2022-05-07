package me.zyy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.zyy.reggie.common.R;
import me.zyy.reggie.entity.Category;
import me.zyy.reggie.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success(1, category.getName(), "添加分类成功");
    }

    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        // 分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        // 条件构造器，用于排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo, queryWrapper);
        return R.success(1, pageInfo, "分类查询成功");
    }

    @DeleteMapping
    public R<String> delete(Long id) {
        categoryService.remove(id);
        return R.success(1, "分类删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category); // category: id,name,sort
        return R.success(1, "修改分类信息成功");
    }

    /**
     *  新增/修改菜品 钩子函数会先请求这个接口
     * @param category 请求参数dishId会封装在这个对象内 ,(后期可能会增加查询参数，所以不直接使用id做参数)
     */
    @GetMapping("/list")
    public R<List<Category>> listCategories(Category category) {
        // 条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> res = categoryService.list(queryWrapper);
        return R.success(1, res, "查询成功");
    }
}
