package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description TODO
 * @date 2024/3/21 16:16
 */
@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * @return org.springframework.security.core.userdetails.UserDetails
     * @description 传入的请求认证的参数就是AuthParamsDto
     * @Param [java.lang.String]
     * @author LuoXuanwei
     * @date 2024/3/21 17:07
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //将传入的json转成AuthParamsDto对象
        AuthParamsDto authParamsDto = null;

        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            throw new RuntimeException("请求认证参数不符合要求");
        }

        //认证类型,有password，wx……
        String authType = authParamsDto.getAuthType();

        //根据认证类型从spring容器取出指定的bean(策略模式)
        String beanName = authType + "_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        //调用
        XcUserExt xcUserExt = authService.execute(authParamsDto);
        //封装xcUserExt用户信息为UserDetails

        UserDetails userPrincipal = getUserPrincipal(xcUserExt);

        return userPrincipal;
    }


    public UserDetails getUserPrincipal(XcUserExt xcUser) {
        String password = xcUser.getPassword();
        //权限
        String[] authorities = {"test"};

        xcUser.setPassword(null);
        String userJson = JSON.toJSONString(xcUser);

        UserDetails userDetails = User.withUsername(userJson).password(password).authorities(authorities).build();
        return userDetails;
    }
}
