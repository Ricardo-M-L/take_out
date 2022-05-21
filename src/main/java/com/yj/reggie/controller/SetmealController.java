package com.yj.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj.reggie.common.R;
import com.yj.reggie.dto.SetmealDto;
import com.yj.reggie.entity.Dish;
import com.yj.reggie.entity.Setmeal;
import com.yj.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("新增套餐信息:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);

        //清理所有套餐的缓存数据
        //Set keys = redisTemplate.keys("setmeal_*"); //获取所有以dish_xxx开头的key
        //redisTemplate.delete(keys); //删除这些key

        //清理某个分类下面的套餐缓存数据
        String key = "setmeal_" + setmealDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
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
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info(setmealDto.toString());
        setmealService.updateWithDish(setmealDto);

        //清理所有套餐的缓存数据
        //Set keys = redisTemplate.keys("setmeal_*"); //获取所有以dish_xxx开头的key
        //redisTemplate.delete(keys); //删除这些key

        //清理某个分类下面的套餐缓存数据
        String key = "setmeal_" + setmealDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("套餐修改成功");
    }

    /**
     * 用户界面展示套餐：根据条件查询套餐数据
     * @param setmeal
     * @return
     */
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
