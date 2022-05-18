package com.yj.reggie.dto;

import com.yj.reggie.entity.Dish;
import com.yj.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private  Integer copies;
}
