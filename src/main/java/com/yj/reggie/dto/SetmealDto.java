package com.yj.reggie.dto;


import com.yj.reggie.entity.Setmeal;
import com.yj.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
