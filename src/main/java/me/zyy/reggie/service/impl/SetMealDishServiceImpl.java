package me.zyy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.zyy.reggie.dao.SetMealDishDao;
import me.zyy.reggie.entity.SetMealDish;
import me.zyy.reggie.service.SetMealDishService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishDao, SetMealDish> implements SetMealDishService {
}
