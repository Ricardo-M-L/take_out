package com.yj.reggie.dto;

import com.yj.reggie.entity.OrderDetail;
import com.yj.reggie.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("订单Dto")
public class OrdersDto extends Orders {

    @ApiModelProperty("订单明细")
    private List<OrderDetail> orderDetails;
}
