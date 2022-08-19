package com.ricardo.take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.take_out.mapper.OrderDetailMapper;
import com.ricardo.take_out.service.OrderDetailService;
import com.ricardo.take_out.entity.OrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
