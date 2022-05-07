package me.zyy.reggie.DTO;

import lombok.Data;
import me.zyy.reggie.entity.SetMeal;
import me.zyy.reggie.entity.SetMealDish;

import java.util.List;

@Data
public class SetMealDTO extends SetMeal {

    private List<SetMealDish> setMealDishes;

    private String categoryName;
}
