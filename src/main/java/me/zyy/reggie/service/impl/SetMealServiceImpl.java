package me.zyy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.zyy.reggie.DTO.SetMealDTO;
import me.zyy.reggie.common.CustomException;
import me.zyy.reggie.dao.SetMealDao;
import me.zyy.reggie.entity.SetMeal;
import me.zyy.reggie.entity.SetMealDish;
import me.zyy.reggie.service.SetMealDishService;
import me.zyy.reggie.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealDao, SetMeal> implements SetMealService {

    @Resource
    private SetMealDishService setMealDishService;

    @Override
    @Transactional
    public void saveWithDishes(SetMealDTO setMealDTO) {

        this.save(setMealDTO);

        // 补充 关系表
        List<SetMealDish> setMealDishesList = setMealDTO.getSetMealDishes(); // 传过来的 没有setMealId

        setMealDishesList = setMealDishesList.stream().map(e -> {
            e.setSetMealId(setMealDTO.getId()); // 此时setMealDTO对象已经插入set_meal表，id已经生成，获取到后赋值即可
            return e;
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(setMealDishesList);
    }

    @Override
    public SetMealDTO getByIdWithDishes(Long id) {
        SetMealDTO res = new SetMealDTO();
        // setMeal信息拿到
        SetMeal setMeal = this.getById(id);
        BeanUtils.copyProperties(setMeal, res);

        LambdaQueryWrapper<SetMealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetMealDish::getSetMealId, id);

        List<SetMealDish> list = setMealDishService.list(queryWrapper);
        res.setSetMealDishes(list);

        return res;
    }

    @Override
    public void updateWithDishes(SetMealDTO setMealDTO) {
        this.updateById(setMealDTO);

        // 对于 关系表的记录要先删除再新增
        LambdaQueryWrapper<SetMealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetMealDish::getSetMealId, setMealDTO.getId());

        setMealDishService.remove(queryWrapper);

        List<SetMealDish> dishesList = setMealDTO.getSetMealDishes(); // 没有setMealId

        dishesList = dishesList.stream().map(e -> {
            e.setSetMealId(setMealDTO.getId());
            return e;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(dishesList);
    }

    @Override
    public void removeWithDishes(List<Long> id) {
        // 套餐表的删除
        // select .. from set_meal where id in(ids) and status = 1 满足条件的可以删除
        // 不满足条件 抛出异常 在全局异常捕获
        // 最后是关系表的删除
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetMeal::getId, id).eq(SetMeal::getStatus, 1);
        if (this.count(queryWrapper) > 0) throw new CustomException("当前套餐尚在使用，无法删除");
        this.removeByIds(id);

        LambdaQueryWrapper<SetMealDish> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(SetMealDish::getSetMealId, id);
        setMealDishService.remove(queryWrapper2);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(int newStatus, List<Long> id) {
//        int _status = newStatus == 0 ? 1 : 0;
        // update status=xxxxx from set_meal where id in (xxxxx);
//        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.in(SetMeal::getId, id);
//        List<SetMeal> list = this.list(queryWrapper);
//        for (SetMeal setMeal : list) {
//            SetMeal res = new SetMeal();
//            res.setStatus(newStatus);
//            this.update(res, queryWrapper);
//        }
        LambdaUpdateWrapper<SetMeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SetMeal::getStatus, newStatus);
        updateWrapper.in(SetMeal::getId, id);
        this.update(updateWrapper);
    }
}
