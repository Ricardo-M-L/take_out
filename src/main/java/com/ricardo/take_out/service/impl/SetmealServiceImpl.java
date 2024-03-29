package com.ricardo.take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.take_out.dto.SetmealDto;
import com.ricardo.take_out.entity.Category;
import com.ricardo.take_out.entity.Dish;
import com.ricardo.take_out.entity.Setmeal;
import com.ricardo.take_out.entity.SetmealDish;
import com.ricardo.take_out.mapper.SetmealMapper;
import com.ricardo.take_out.service.CategoryService;
import com.ricardo.take_out.service.SetmealDishService;
import com.ricardo.take_out.service.SetmealService;
import com.ricardo.take_out.common.CustomException;
import com.ricardo.take_out.entity.*;
import com.ricardo.take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作setmeal,执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }

        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
        log.info(String.valueOf(setmealDishes));
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> page(int page, int pageSize, String name) {
        //构造一个分页构造器
        Page<Setmeal> pageInfo= new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行分页查询
         this.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> setmeals = pageInfo.getRecords();
        List<SetmealDto> setmealDtos =new ArrayList<>();

        for (Setmeal setmeal : setmeals) {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(setmeal,setmealDto);
            //分类id
            Long categoryId = setmeal.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
                setmealDtos.add(setmealDto);
            }
        }
        dtoPage.setRecords(setmealDtos);
        return dtoPage;
    }


    /**
     * 根据套餐id修改售卖状态
     * @param status
     * @param id
     */
    @Override
    public void updateSetmealStatus(int status, Long[] id) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(id !=null,Setmeal::getId,id);
        List<Setmeal> list = this.list(queryWrapper);

        for (Setmeal setmeal : list) {
            if (setmeal != null){
                setmeal.setStatus(status);
                this.updateById(setmeal);

                //清理所有套餐的缓存数据
                //Set keys = redisTemplate.keys("setmeal_*"); //获取所有以dish_xxx开头的key
                //redisTemplate.delete(keys); //删除这些key

                //清理某个分类下面的套餐缓存数据
                String key = "setmeal_" + setmeal.getCategoryId() + "_1";
                redisTemplate.delete(key);
            }
        }
    }


    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param id
     */
    @Override
    @Transactional
    public void removeWithDish(Long[] id) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(id != null, Setmeal::getId, id);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(Arrays.asList(id));

        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, id);
        //删除关系表中的数据----setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);
    }

    /**
     * 修改套餐时回显套餐数据：根据id查询套餐信息和对应的菜品信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto get(Long id) {
        //查询套餐的基本信息，从setmeal表中查询
        Setmeal setmeal = this.getById(id);

        //将数据从setmeal对象拷贝到setmealDto对象中
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询当前套餐对应的菜品信息(菜品信息为List集合对象)，从setmeal_dish表查询
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     * 更新套餐信息，同时更新对应的菜品信息
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新setmeal表基本信息
        this.updateById(setmealDto);

        //清理当前套餐对应菜品数据---setmeal_dish表的delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //setmealDishService.removeById(setmealDto.getId());

        //添加当前提交过来的菜品数据---setmeal_dish表的insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //将dishId添加到setmealDishes对象中
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 用户界面展示套餐：根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {

        //构造查询条件，按照分类id查找套餐
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        List<Setmeal> setmealList = this.list(queryWrapper);

        return setmealList;


    }

    /**
     * 用户界面点击套餐, 展示其中的菜品数据
     * @param id
     * @return
     */
    @Override
    public List<Dish> getSetmealDish(Long id) {   //id为套餐id

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        List<Dish> dishList = new ArrayList<>();
        for (SetmealDish setmealDish : list) {
            Long dishId = setmealDish.getDishId();
            Dish dish = dishService.getById(dishId);
            dishList.add(dish);
        }
        return dishList;  //返回菜品数据
    }
}
