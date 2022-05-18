package com.yj.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yj.reggie.entity.ShoppingCart;
import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {

    //添加购物车
    public ShoppingCart add(ShoppingCart shoppingCart);

    //查看购物车
    public List<ShoppingCart> getShoppingCart();

    //清空购物车
    public void clean();

    //购物车中减少商品
    public ShoppingCart sub(ShoppingCart shoppingCart);
}
