package com.yj.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yj.reggie.common.R;
import com.yj.reggie.entity.User;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService extends IService<User> {

}
