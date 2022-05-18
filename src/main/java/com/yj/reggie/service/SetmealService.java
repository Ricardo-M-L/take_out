package com.yj.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yj.reggie.common.R;
import com.yj.reggie.dto.DishDto;
import com.yj.reggie.dto.SetmealDto;
import com.yj.reggie.entity.Dish;
import com.yj.reggie.entity.Setmeal;
import com.yj.reggie.entity.SetmealDish;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.LinkOption;
import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    // 新增套餐，同时需要保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);

    // 套餐分页查询
    public Page<SetmealDto> page(int page, int pageSize, String name);

    // 删除套餐，同时需要删除套餐和菜品的关联数据
    public void removeWithDish(Long[] id);

    // 根据套餐id修改售卖状态
    public void updateSetmealStatus(int status, Long[] id);

    // 修改套餐时回显套餐数据：根据id查询套餐信息和对应的菜品信息
    public SetmealDto get(Long id);

    //更新套餐信息，同时更新对应的菜品信息
    public void updateWithDish(SetmealDto setmealDto);


    //用户界面展示套餐：根据条件查询套餐数据
    public List<Setmeal> list(Setmeal setmeal);

    //用户界面点击套餐, 展示其中的菜品数据
    public List<Dish> getSetmealDish(Long id);
}
