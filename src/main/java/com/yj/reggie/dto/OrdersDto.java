package com.yj.reggie.dto;

import com.yj.reggie.entity.OrderDetail;
import com.yj.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {
    private List<OrderDetail> orderDetails;
}
