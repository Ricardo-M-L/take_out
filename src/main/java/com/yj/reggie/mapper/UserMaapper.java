package com.yj.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yj.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMaapper extends BaseMapper<User> {
}
