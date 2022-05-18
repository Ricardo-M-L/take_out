package com.yj.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yj.reggie.common.R;
import com.yj.reggie.dto.DishDto;
import com.yj.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //菜品信息分页查询
    public Page<DishDto> page(int page, int pageSize, String name);

    //对菜品批量或者是单个 进行停售或者是起售
    public void updateStatus(int status,Long[] id);

    //根据id删除删除菜品信息和对应的口味信息
    public void deleteWithFlavors(Long[] id);

    //修改菜品时回显菜品数据:根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新对应的口味信息
    public void updateWithFlavor(DishDto dishDto);

    //新增套餐时回显菜品数据(以及用户界面展示菜品时可以选择规格展示口味数据）：根据条件查询对应的菜品数据
    //public List<Dish> list(Dish dish);
    public List<DishDto> list(Dish dish);


}
