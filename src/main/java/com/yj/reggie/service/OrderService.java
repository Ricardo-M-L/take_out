package com.yj.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yj.reggie.common.R;
import com.yj.reggie.dto.OrdersDto;
import com.yj.reggie.entity.Orders;

import java.util.Map;


public interface OrderService extends IService<Orders> {

    //用户下单
    public void submit(Orders orders);

    //用户订单分页查询
    public Page<OrdersDto> page(int page, int pageSize);

    //客户端点击再来一单
    public void againSubmit(Map<String,String> map);

    //后台查询订单明细
    public Page page(int page, int pageSize, String number, String beginTime, String endTime);
}
