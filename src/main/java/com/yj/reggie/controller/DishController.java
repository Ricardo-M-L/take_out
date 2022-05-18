package com.yj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj.reggie.common.R;
import com.yj.reggie.dto.DishDto;
import com.yj.reggie.entity.Category;
import com.yj.reggie.entity.Dish;
import com.yj.reggie.entity.Employee;
import com.yj.reggie.service.CategoryService;
import com.yj.reggie.service.DishFlavorService;
import com.yj.reggie.service.DishService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
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
    public R<Page> page(int page, int pageSize, String name) {
        Page<DishDto> dishDtoPage = dishService.page(page, pageSize, name);
        return R.success(dishDtoPage);
    }

    /**
     * 对菜品批量或者是单个 进行停售或者是起售
     * @return
     */
    @PostMapping("/status/{status}")
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
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
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
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> list = dishService.list(dish);
        return R.success(list);
    }
}
