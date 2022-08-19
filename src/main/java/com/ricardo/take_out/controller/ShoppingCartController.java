package com.ricardo.take_out.controller;


import com.ricardo.take_out.common.R;
import com.ricardo.take_out.entity.ShoppingCart;
import com.ricardo.take_out.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * 对相同口味的菜品或者同一套餐，如果选择多份不需要添加多条记录，增加数量number即可，对选择不同口味的菜品，要新增记录
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        ShoppingCart cartServiceOne = shoppingCartService.add(shoppingCart);
        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCart(){
        List<ShoppingCart> list = shoppingCartService.getShoppingCart();
        return R.success(list);
    }

    /**
     * 购物车中减少商品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){

        ShoppingCart cartServiceOne = shoppingCartService.sub(shoppingCart);
        return R.success(cartServiceOne);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        shoppingCartService.clean();
        return R.success("清空购物车成功");
    }
 }
