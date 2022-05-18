package com.yj.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yj.reggie.common.CustomException;
import com.yj.reggie.entity.Category;
import com.yj.reggie.entity.Dish;
import com.yj.reggie.entity.Setmeal;
import com.yj.reggie.mapper.CategoryMapper;
import com.yj.reggie.service.CategoryService;
import com.yj.reggie.service.DishService;
import com.yj.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    DishService dishService;
    @Autowired
    SetmealService setmealService;

    @Override
    public Page<Category> page(int page, int pageSize) {
        //分页构造器
        Page pageinfo = new Page(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);

        //分页查询
        this.page(pageinfo,queryWrapper);
        return pageinfo;
    }

    /**
     * 根据id删除分类，删除之前需要进行判断
     */
    @Override
    public void remove(Long id) {

        //添加查询条件，根据分类id进行查询菜品数据
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishQueryWrapper);

        //查询当前分类是否已经关联了菜品，如果已经关联，抛出一个业务异常
        if(dishCount > 0) {
            //已关联菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //添加查询条件，根据分类id进行查询套餐数据
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = dishService.count(dishQueryWrapper);

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        if(setmealCount > 0) {
            //已关联套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除分类
        this.removeById(id);
    }

    /**
     * 新增菜品时回显分类数据：根据条件查询分类数据
     * @param category
     * @return
     */
    @Override
    public List<Category> list(Category category) {
        //添加条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getCreateTime);

        //查询出分类的数据
        List<Category> list = this.list(queryWrapper);
        log.info(String.valueOf(list));
      /*  for (Category category1 : list) {
            String row = String.valueOf(category1);
            String[] lines = row.split(",");
            log.info(row);
            log.info(lines[2]);
        }*/
        return list;
    }
}
