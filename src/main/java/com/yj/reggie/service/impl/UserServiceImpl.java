package com.yj.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yj.reggie.entity.User;
import com.yj.reggie.mapper.UserMaapper;
import com.yj.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMaapper, User> implements UserService {
}
