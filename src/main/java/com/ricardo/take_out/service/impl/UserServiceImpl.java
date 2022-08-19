package com.ricardo.take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.take_out.entity.User;
import com.ricardo.take_out.mapper.UserMaapper;
import com.ricardo.take_out.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMaapper, User> implements UserService {

}
