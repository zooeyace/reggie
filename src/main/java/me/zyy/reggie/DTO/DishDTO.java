package me.zyy.reggie.DTO;


import lombok.Data;
import me.zyy.reggie.entity.Dish;
import me.zyy.reggie.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDTO extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
