package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDTO;
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
import org.springframework.stereotype.Service;

/**
 * @author Lin
 * @date 2024/2/21 16:01
 */
@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 根据账号查询用户信息
     *
     * @param s 账号
     * @return 用户信息
     * @throws UsernameNotFoundException 用户名不存在
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDTO authParamsDTO;
        try {
            // 将认证参数转为AuthParamsDTO类型
            authParamsDTO = JSON.parseObject(s, AuthParamsDTO.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求：{}", s);
            throw new RuntimeException("认证请求数据格式错误");
        }

        // 根据认证类型取出对应的认证的bean
        String authType = authParamsDTO.getAuthType();
        String beanName = authType + "_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        // 调用方法完成认证
        XcUserExt user = authService.execute(authParamsDTO);

        return getUserPrincipal(user);
    }

    /**
     * 封装用户信息
     * @param user 用户信息
     */
    public UserDetails getUserPrincipal(XcUserExt user){
        // 用户权限
        String[] authorities = {"p1"};
        String password = user.getPassword();

        // 令牌中不存密码
        user.setPassword(null);
        String userString = JSON.toJSONString(user);

        // 创建UserDetails对象
        return User
                .withUsername(userString)
                .password(password)
                .authorities(authorities)
                .build();
    }
}
