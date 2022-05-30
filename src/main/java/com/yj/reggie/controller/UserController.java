package com.yj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yj.reggie.common.R;
import com.yj.reggie.entity.User;
import com.yj.reggie.service.UserService;
import com.yj.reggie.utils.SMSUtils;
import com.yj.reggie.utils.ValidateCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags ={"用户接口相关"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    @ApiOperation("发送手机短信验证码接口")
    public R<String> sendMsg(@RequestBody User user,HttpSession session) {
        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //String code = ValidateCodeUtils.generateValidateCode4String(4);
            log.info("code={}",code);

            //调用阿里云提供的短信API完成发送短信任务
            //SMSUtils.sendMessage("外卖","",phone,code);

            //需要将生成的验证码保存到Session
            //request.getSession().setAttribute(phone,code);
            //session.setAttribute("phoneCode",code);

            //需要将生成的验证码保存到Redis,设置过期时间
            redisTemplate.opsForValue().set("phoneCode",code,5, TimeUnit.MINUTES);
            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("移动端用户登录接口")
    public R<User> login(@RequestBody Map map, HttpSession session) {

        log.info(map.toString());
        //获取页面的手机号
        String phone = map.get("phone").toString();
        //判断当前手机号对应的用户是否为新用户
        //构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);

        User user = userService.getOne(queryWrapper);
        if(user == null) {  //用户为空，说明当前手机号为新用户，自动完成注册
            User newUser = new User();
            newUser.setPhone(phone);
            newUser.setStatus(1);
            userService.save(newUser);
            session.setAttribute("userId",newUser.getId());
            return R.success(user);
        }
        //否则说明是老用户，则获取页面的验证码,并判断其是否为空
        String code = map.get("code").toString();
        if(StringUtils.isNotEmpty(code)) {
            //页面的验证码不为空，再从session中取出保存的验证码
            //String phoneCode = session.getAttribute("phoneCode").toString();

            //页面的验证码不为空，再从Redis中获取缓存的验证码
            String phoneCode = redisTemplate.opsForValue().get("phoneCode").toString();
            //进行验证码的比对(页面提交的验证码和session中保存的验证码进行比对)
            if(code.equals(phoneCode)) {
                //如果能比对成功，则可以登录成功
                session.setAttribute("userId",user.getId());

                //从Redis中删除缓存的验证码
                redisTemplate.delete("phoneCode");
                
                return R.success(user);
            }
            return R.error("验证码错误，登录失败");
        }

        //否则登录失败
        return R.error("验证码为空，登录失败");
    }
}
