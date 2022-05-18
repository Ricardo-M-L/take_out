package com.yj.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yj.reggie.common.BaseContext;
import com.yj.reggie.entity.ShoppingCart;
import com.yj.reggie.mapper.ShoppingCartMapper;
import com.yj.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    /**
     * 添加购物车
     * 对相同口味的菜品或者同一套餐，如果选择多份不需要添加多条记录，增加数量number即可，对选择不同口味的菜品，要新增记录
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        log.info("购物车数据:{}",shoppingCart);

        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            //queryWrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());  //前端这里没处理
        }else{
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询当前（同一口味的菜品）或者套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? setmeal_id = ?/dish_id =  ?
        ShoppingCart cartServiceOne = this.getOne(queryWrapper);

        if(cartServiceOne != null){
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            this.updateById(cartServiceOne);
        }else{
            //如果不存在，则添加到购物车，数量默认就是1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return cartServiceOne;
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> getShoppingCart() {
        log.info("查看购物车...");

        //SQL:select * from shopping_cart where user_id = ? order by create_time
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = this.list(queryWrapper);
        return list;
    }

    /**
     * 购物车中减少商品
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart sub(ShoppingCart shoppingCart) {

        ShoppingCart cartServiceOne;
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        if(setmealId != null){
            //购物车中将要减少的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            cartServiceOne = this.getOne(queryWrapper);
        } else{
            //购物车中将要减少的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            //queryWrapper.eq(shoppingCart.getDishFlavor() != null,ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor()); //前端这里没处理
            cartServiceOne = this.getOne(queryWrapper);
        }

        //获得购物车中当前要减少的菜品或者套餐的数量
        Integer number = cartServiceOne.getNumber();
        if(number > 1) {
            cartServiceOne.setNumber(number - 1);
            cartServiceOne.setCreateTime(LocalDateTime.now());
            this.updateById(cartServiceOne);
        }else if(number == 1){
            this.removeById(cartServiceOne.getId());
        }

        return cartServiceOne;
    }

    /**
     * 清空购物车
     * @return
     */
    @Override
    public void clean() {
        //SQL:delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        this.remove(queryWrapper);
    }
}
