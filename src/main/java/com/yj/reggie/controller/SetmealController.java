package com.yj.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj.reggie.common.R;
import com.yj.reggie.dto.SetmealDto;
import com.yj.reggie.entity.Dish;
import com.yj.reggie.entity.Setmeal;
import com.yj.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增套餐
     * @param setmealDto
     */
    @CacheEvict(value = "setmealCache",key ="'setmeal_' + #setmealDto.getCategoryId() + '_1'") //删除套餐对应分类下的所有套餐缓存
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("新增套餐信息:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<SetmealDto> setmealDtoPage = setmealService.page(page, pageSize, name);
        return R.success(setmealDtoPage);
    }

    /**
     * 对套餐批量或者是单个 进行停售或者是起售
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true) //删除setmealCache这个缓存空间里所有数据
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, @RequestParam("ids") Long[] id) {
        log.info("status:{}", status);
        log.info("ids:{}", id);
        setmealService.updateSetmealStatus(status,id);
        return R.success("售卖状态修改成功");
    }
    /**
     * 删除套餐
     * @param id
     * .不加@RequestParam前端的参数名需要和后端控制器的变量名保持一致才能生效.
     * 可以通过@RequestParam(“userId”)或者@RequestParam(value = “userId”)指定参数名。参数名要与前端传入的参数名保持一致
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long[] id){
        log.info("id:{}",id);
        setmealService.removeWithDish(id);
        return R.success("套餐数据删除成功");
    }

    /**
     * 修改套餐时回显套餐数据：根据id查询套餐信息和对应的菜品信息
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.get(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @CacheEvict(value = "setmealCache",key ="'setmeal_' + #setmealDto.getCategoryId() + '_1'") //删除套餐对应分类下的所有套餐缓存
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info(setmealDto.toString());
        setmealService.updateWithDish(setmealDto);

        return R.success("套餐修改成功");
    }

    /**
     * 用户界面展示套餐：根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @Cacheable(value = "setmealCache", key = "'setmeal_' + #setmeal.categoryId + '_' + #setmeal.status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        List<Setmeal> list = setmealService.list(setmeal);
        return R.success(list);
    }

    /**
     * 用户界面点击套餐, 展示其中的菜品数据
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<Dish>> getSetmealDish(@PathVariable Long id) {

        List<Dish> dishList = setmealService.getSetmealDish(id);
        return R.success(dishList);
    }
}
