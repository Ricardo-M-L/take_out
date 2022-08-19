package com.ricardo.take_out.dto;

import com.ricardo.take_out.entity.OrderDetail;
import com.ricardo.take_out.entity.Orders;
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
