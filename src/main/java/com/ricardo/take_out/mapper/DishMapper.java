package com.ricardo.take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ricardo.take_out.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
