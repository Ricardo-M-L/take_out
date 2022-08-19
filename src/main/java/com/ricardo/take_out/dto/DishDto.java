package com.ricardo.take_out.dto;

import com.ricardo.take_out.entity.Dish;
import com.ricardo.take_out.entity.DishFlavor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("菜品Dto")
public class DishDto extends Dish {

    @ApiModelProperty("菜品口味数据List")
    private List<DishFlavor> flavors = new ArrayList<>();

    @ApiModelProperty("分类id")
    private String categoryName;

    @ApiModelProperty("份数")
    private  Integer copies;
}
