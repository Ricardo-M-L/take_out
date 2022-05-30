package com.yj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj.reggie.common.R;
import com.yj.reggie.dto.DishDto;
import com.yj.reggie.entity.Dish;
import com.yj.reggie.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
@Api(tags ={"菜品接口相关"})
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品接口")
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        //清理所有菜品的缓存数据
      /*  Set keys = redisTemplate.keys("dish_*");//获取所有以dish_xxx开头的key
        redisTemplate.delete(keys);//删除这些key*/

        //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "菜品分页查询接口")
    public R<Page> page(int page, int pageSize, String name) {
        Page<DishDto> dishDtoPage = dishService.page(page, pageSize, name);
        return R.success(dishDtoPage);
    }

    /**
     * 对菜品批量或者是单个 进行停售或者是起售
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "菜品批量(或单个)停起售接口")
    public R<String> updateStatus(@PathVariable int status, Long[] id) {
        log.info("status:{}", status);
        log.info("ids:{}", id);
        dishService.updateStatus(status, id);

        return R.success("售卖状态修改成功");
    }

    /**
     * 菜品批量删除和单个删除
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "菜品删除接口")
    public R<String> delete(Long[] id) {
        dishService.deleteWithFlavors(id);
        return R.success("菜品删除成功");
    }

    /**
     * 修改菜品时回显菜品数据:根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "菜品及对应的口味信息id查询接口")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改菜品接口")
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        //清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");//获取所有以dish_xxx开头的key
        //redisTemplate.delete(keys);//删除这些key

        //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

    /**
     * 新增套餐时回显菜品数据(以及用户界面展示菜品时可以选择规格展示口味数据）：根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
   /* @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        List<Dish> list = dishService.list(dish);
        return R.success(list);
    }*/
    @GetMapping("/list")
    @ApiOperation(value = "菜品条件查询接口")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> list = dishService.list(dish);
        return R.success(list);
    }
}
