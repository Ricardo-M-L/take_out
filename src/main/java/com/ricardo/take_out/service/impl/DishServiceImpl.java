package com.ricardo.take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.take_out.dto.DishDto;
import com.ricardo.take_out.entity.Category;
import com.ricardo.take_out.mapper.DishMapper;
import com.ricardo.take_out.service.CategoryService;
import com.ricardo.take_out.service.DishFlavorService;
import com.ricardo.take_out.common.CustomException;
import com.ricardo.take_out.entity.Dish;
import com.ricardo.take_out.entity.DishFlavor;
import com.ricardo.take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional //涉及多张表的操作 要加入事务控制
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        //获取菜品的id
        Long dishId = dishDto.getId();

        //获取菜品口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        //将菜品id添加到菜品口味数据中去
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<DishDto> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        this.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> dishRecords = pageInfo.getRecords();
        List<DishDto> dishDtoRecords = new ArrayList<>();

        for (Dish dish : dishRecords) {
            //将dish中的数据拷贝到dishDto中
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            Long categoryId = dish.getCategoryId(); //分类id

            //根据id查询分类对象,再得到分类的名称
            Category category = categoryService.getById(categoryId);
            //if(category != null) {
            String categoryName = category.getName();

            //将分类名称保存到dishDto对象里,再将对象添加到...
            dishDto.setCategoryName(categoryName);
            //}
            dishDtoRecords.add(dishDto);
        }
        dishDtoPage.setRecords(dishDtoRecords);
        return dishDtoPage;
    }

    /**
     * 对菜品批量或者是单个 进行停售或者是起售
     * @return
     */
    @Override
    public void updateStatus(int status, Long[] id) {
        /*LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(id !=null,Dish::getId,id);
        //根据数据进行批量查询
        List<Dish> list = this.list(queryWrapper);*/

        //根据数据进行批量查询
        List<Dish> dishList = this.listByIds(Arrays.asList(id));
        for (Dish dish : dishList) {
            if (dish != null) {
                dish.setStatus(status);
                this.updateById(dish);
                //清理所有菜品的缓存数据
                //Set keys = redisTemplate.keys("dish_*");//获取所有以dish_xxx开头的key
                //redisTemplate.delete(keys);//删除这些key

                //清理某个分类下面的菜品缓存数据
                String key = "dish_" + dish.getCategoryId() + "_1";
                redisTemplate.delete(key);
            }

        }
    }

    /**
     * 根据id删除删除菜品信息和对应的口味信息
     * @param id
     */
    @Override
    @Transactional   //涉及多张表的操作 要加入事务控制
    public void deleteWithFlavors(Long[] id) {

        //添加查询条件，根据菜品id来删除对应菜品口味数据
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(id != null,Dish::getId,id);

        LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
        flavorQueryWrapper.in(DishFlavor::getDishId,id);

        //先查询该菜品是否在售卖，如果是则抛出业务异常
        List<Dish> dishList = this.list(queryWrapper);
        for (Dish dish : dishList) {
            Integer status = dish.getStatus();

            //如果是在停售状态,则可以删除,先删除口味信息，再删除菜品信息
            if( status == 0) {
                //log.info("删除菜品{}",dish.getName());
                dishFlavorService.remove(flavorQueryWrapper);
                this.removeById(dish.getId());
            }else {
                //此时应该回滚,因为可能前面的删除了，但是后面的是正在售卖
                throw new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
            }
        }
    }

    /**
     *修改菜品时回显菜品数据:根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的基本信息，从dish表查
        Dish dish = this.getById(id);

        //将数据从dish对象拷贝到dishDto对象中
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息(口味信息为List集合对象)，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);//dish.getId()就是id
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //dishFlavorService.removeById(dishDto.getId());

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        //将dishId添加到flavor对象中
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }

        dishFlavorService.saveBatch(flavors);

        //dishFlavorService.updateBatchById(flavors);   //这个方法对口味更新不起作用
    }

    /**
     * 新增套餐时回显菜品数据(以及用户界面展示菜品时可以选择规格展示口味数据）：根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @Override
    /*public List<Dish> list(Dish dish) {
        //构造查询条件，添加条件，查询状态为1（起售状态）的菜品,再按照分类id查找菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus,1).eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //执行查询
        List<Dish> list = this.list(queryWrapper);

        return list;
    }*/
    public List<DishDto> list(Dish dish) {

        //动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//dish_1397844391040167938_1

        //先从redis中获取缓存数据
        List<DishDto> list = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(list != null){
            //如果存在，直接返回，无需查询数据库
            return list;
        }

        //如果不存在，则查询数据库
        //构造查询条件，添加条件，查询状态为1（起售状态）的菜品,再按照分类id查找菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus,1).eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //执行查询
        List<Dish> dishList = this.list(queryWrapper);
        List<DishDto> dishDtoList = new ArrayList<>();

        for (Dish item : dishList) {
            //将dish中的数据拷贝到dishDto中
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId(); //分类id

            //根据id查询分类对象,再得到分类的名称
            Category category = categoryService.getById(categoryId);
            //if(category != null) {
            String categoryName = category.getName();
            //将分类名称保存到dishDto对象里,再将对象添加到...
            dishDto.setCategoryName(categoryName);
            //}

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            dishDtoList.add(dishDto);
        }

        //将查询到的菜品数据缓存到Redis并设置过期时间
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return dishDtoList;
    }
}
