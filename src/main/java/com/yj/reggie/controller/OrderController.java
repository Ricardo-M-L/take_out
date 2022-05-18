package com.yj.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj.reggie.common.R;
import com.yj.reggie.dto.OrdersDto;
import com.yj.reggie.entity.Orders;
import com.yj.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 用户订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(int page, int pageSize) {
        Page<OrdersDto> pageInfo = orderService.page(page, pageSize);
        return R.success(pageInfo);
    }

    /**
     * 用户再来一单
     * @param map
     * @return
     */
    @PostMapping("/again")
    public R<String> againSubmit(@RequestBody Map<String,String> map){
        orderService.againSubmit(map);
        return R.success("操作成功");
    }

    /**
     * 后台查询订单明细
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,String beginTime,String endTime){
        Page pageInfo = orderService.page(page, pageSize, number, beginTime, endTime);
        return R.success(pageInfo);
    }
}
