package me.zyy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.zyy.reggie.DTO.DishDTO;
import me.zyy.reggie.common.R;
import me.zyy.reggie.entity.Category;
import me.zyy.reggie.entity.Dish;
import me.zyy.reggie.entity.DishFlavor;
import me.zyy.reggie.service.CategoryService;
import me.zyy.reggie.service.DishFlavorService;
import me.zyy.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishService dishService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> saveDish(@RequestBody DishDTO dishDTO) {
        dishService.saveWithFlavor(dishDTO);

        // 清理缓存,为了防止redis与数据库的数据不一致，在修改数据的时候先将redis缓存清除
        String key = "dish_" + dishDTO.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success(1, "新增菜品成功");
    }

    /**
     * 菜品分页查询
     * 希望展示菜品分类的名称，所以返回 DishDTO，因其含有categoryName属性
     */
    @GetMapping("/page")
    public R<Page<DishDTO>> page(int page, int pageSize, String param) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDTO> dtoPage = new Page<>(page, pageSize);

        // 条件查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(param), Dish::getName, param).orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper); // pageInfo赋值完成

        BeanUtils.copyProperties(pageInfo, dtoPage, "records"); // 将除了records的属性拷贝至dtoPage
        List<Dish> records = pageInfo.getRecords();

        List<DishDTO> _records = records.stream().map(e -> {
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(e, dishDTO);
            Long categoryId = e.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String cName = category.getName();
                dishDTO.setCategoryName(cName);
            }
            return dishDTO;
        }).collect(Collectors.toList());

        dtoPage.setRecords(_records);

        return R.success(1, dtoPage, "分页成功");
    }

    /**
     * 根据id查询对应的菜品信息以及口味信息
     */
    @GetMapping("/{id}")
    public R<DishDTO> getById(@PathVariable Long id) {
        DishDTO res = dishService.getByIdWithFlavor(id);
        return R.success(1, res, "查询成功");
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> updateDish(@RequestBody DishDTO dishDTO) {
        dishService.updateWithFlavor(dishDTO);

        // 清理某个分类下的缓存
        String key = "dish_" + dishDTO.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success(1, "修改菜品信息成功");
    }

    /**
     * 根据 categoryId 查出菜品+口味信息
     */
    @GetMapping("/list")
    public R<List<DishDTO>> list(Dish dish) {
        List<DishDTO> res;

        String key = "dish_" + dish.getCategoryId() + "_1";
        res = (List<DishDTO>) redisTemplate.opsForValue().get(key);
        if (res != null) return R.success(1, res, "查询成功"); // 缓存拿到数据提前返回

        // 缓存没拿到 走数据库查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        // 在查出来的菜品基础上 赋予口味信息
        res = list.stream().map(e -> {
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(e, dishDTO);
            LambdaQueryWrapper<DishFlavor> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(DishFlavor::getDishId, e.getId());
            // SQL: select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper2);
            dishDTO.setFlavors(dishFlavors); // 将查出来的口味集合赋值给dishDTO
            return dishDTO;
        }).collect(Collectors.toList());

        // 将结果写入缓存
        redisTemplate.opsForValue().set(key, res, 60, TimeUnit.MINUTES);

        return R.success(1, res, "查询成功");
    }

    @PostMapping("/status/{status}")
    public R<String> statusChange(@PathVariable int status, @RequestParam("ids") List<Long> id) {
//        log.info("{},{}", status, id);
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus, status);
        updateWrapper.in(Dish::getId, id);
        dishService.update(updateWrapper);
        return R.success(1, "修改状态成功");
    }
}
