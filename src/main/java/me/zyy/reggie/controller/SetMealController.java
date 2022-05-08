package me.zyy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.zyy.reggie.DTO.SetMealDTO;
import me.zyy.reggie.common.R;
import me.zyy.reggie.entity.Category;
import me.zyy.reggie.entity.SetMeal;
import me.zyy.reggie.service.CategoryService;
import me.zyy.reggie.service.SetMealDishService;
import me.zyy.reggie.service.SetMealService;
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

/**
 * 套餐
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private SetMealService setMealService;

    @Resource
    private SetMealDishService setMealDishService;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody SetMealDTO setMealDTO) {
//        log.info("{}", setMealDTO);
        setMealService.saveWithDishes(setMealDTO);

        // 清理当前分类的缓存
        String key = "set_meal" + setMealDTO.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success(1, "套餐保存成功");
    }

    @GetMapping("/page")
    public R<Page<SetMealDTO>> page(int page, int pageSize, String param) {
        Page<SetMeal> pageInfo = new Page<>(page, pageSize);
        Page<SetMealDTO> dtoPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(param), SetMeal::getName, param).orderByDesc(SetMeal::getUpdateTime);

        setMealService.page(pageInfo, queryWrapper);

        // 以下代码是将 categoryId 转换成 categoryName
        BeanUtils.copyProperties(pageInfo, dtoPage, "records"); // 将除了records的属性拷贝至dtoPage
        List<SetMeal> records = pageInfo.getRecords();

        List<SetMealDTO> _records = records.stream().map(e -> {
            SetMealDTO setMealDTO = new SetMealDTO();
            BeanUtils.copyProperties(e, setMealDTO);
            Long categoryId = e.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String cName = category.getName();
                setMealDTO.setCategoryName(cName);
            }
            return setMealDTO;
        }).collect(Collectors.toList());

        dtoPage.setRecords(_records);

        return R.success(1, dtoPage, "分页查询成功");
    }

    /**
     * 修改页面的回填
     */
    @GetMapping("/{id}")
    public R<SetMealDTO> getById(@PathVariable Long id) {
        SetMealDTO setMealDTO = setMealService.getByIdWithDishes(id);
        return R.success(1, setMealDTO, "旧数据获取成功");
    }

    /**
     * 修改页面的提交
     */
    @PutMapping
    public R<String> update(@RequestBody SetMealDTO setMealDTO) {
        setMealService.updateWithDishes(setMealDTO);

        String key = "set_meal" + setMealDTO.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success(1, "修改成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> id) {
//        log.info("{}", id);
        setMealService.removeWithDishes(id);
        return R.success(1, "删除成功");
    }

    @PostMapping("/status")
    public R<String> statusChange(@RequestParam("status") int newStatus, @RequestParam("id") List<Long> id) {
//        log.info("{}, {}", newStatus, id);
        setMealService.updateStatus(newStatus, id);

        return R.success(1, "更新成功");
    }

    @GetMapping("/list")
    public R<List<SetMeal>> list(@RequestParam Long categoryId, @RequestParam int status) { // 参数可以换成SetMeal setMeal
        List<SetMeal> res;

        String key = "set_meal" + categoryId + "_1";
        res = (List<SetMeal>) redisTemplate.opsForValue().get(key);
        if (res != null) return R.success(1, res, "套餐展示成功");

        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetMeal::getCategoryId, categoryId)
                .eq(SetMeal::getStatus, status)
                .orderByDesc(SetMeal::getUpdateTime);
        res = setMealService.list(queryWrapper);

        redisTemplate.opsForValue().set(key, res, 60, TimeUnit.MINUTES);

        return R.success(1, res, "套餐展示成功");
    }
}
