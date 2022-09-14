package com.martin.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.martin.reggie.common.R;
import com.martin.reggie.entitty.User;
import com.martin.reggie.service.UserService;
import com.martin.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;
    
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code:{}",code);

            //（不进行）调用API发短信
            // ...

            //保存验证码到Redis中，设置有效期5min
//            session.setAttribute(phone,code); // 原方案将验证码保存在session，30s过期
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机短信验证码发送成功");
        }
        return R.error("手机短信验证码发送失败");
    }

    /**
     * 前端登录
     * @param map 手机号-验证码 数据结构
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String,String> map, HttpSession session) {
        //获取手机号
        String phone = map.get("phone");
        //获取验证码
        String code = map.get("code");
        //session中获取正确的验证码
//        Object codeInSession = session.getAttribute(phone);
        //redis中获取正确的验证码
        Object codeInRedis = redisTemplate.opsForValue().get(phone);
        //比对验证码,成功则登录
        if (codeInRedis != null && codeInRedis.equals(code)) {
            //判断是否手机号为新用户，是则注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if(user==null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
