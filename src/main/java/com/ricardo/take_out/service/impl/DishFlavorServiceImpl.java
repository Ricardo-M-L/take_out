package com.ricardo.take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.take_out.mapper.DishFlavorMapper;
import com.ricardo.take_out.service.DishFlavorService;
import com.ricardo.take_out.entity.DishFlavor;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
