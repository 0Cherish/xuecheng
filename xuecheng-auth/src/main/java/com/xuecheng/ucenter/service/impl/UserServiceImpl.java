package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDTO;
import com.xuecheng.ucenter.model.dto.RegisterDTO;
import com.xuecheng.ucenter.model.dto.RetrievePasswordDTO;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.AuthService;
import com.xuecheng.ucenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Lin
 * @date 2024/2/21 16:01
 */
@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private XcMenuMapper xcMenuMapper;

    @Autowired
    private XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CheckCodeClient checkCodeClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
     *
     * @param user 用户信息
     */
    public UserDetails getUserPrincipal(XcUserExt user) {
        // 用户权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(user.getId());
        List<String> permissions = new ArrayList<>();
        if (xcMenus.isEmpty()) {
            permissions.add("p1");
        } else {
            xcMenus.forEach(menu -> permissions.add(menu.getCode()));
        }

        user.setPermissions(permissions);
        String password = user.getPassword();

        // 令牌中不存密码
        user.setPassword(null);
        String userString = JSON.toJSONString(user);

        // 创建UserDetails对象
        String[] authorities = permissions.toArray(new String[0]);
        return User
                .withUsername(userString)
                .password(password)
                .authorities(authorities)
                .build();
    }

    @Override
    public void findPassword(RetrievePasswordDTO retrievePasswordDTO) {
        // 两次密码是否一致
        String password = retrievePasswordDTO.getPassword();
        String confirmPwd = retrievePasswordDTO.getConfirmpwd();
        if (!password.equals(confirmPwd)) {
            throw new RuntimeException("两次密码不一致");
        }

        // 校验验证码
        String checkCodeKey = retrievePasswordDTO.getCheckcodekey();
        String checkCode = retrievePasswordDTO.getCheckcode();
        Boolean verify = checkCodeClient.verify(checkCodeKey, checkCode);
        if (!verify) {
            throw new RuntimeException("验证码错误");
        }

        // 根据手机号（优先）和邮箱查询用户
        LambdaQueryWrapper<XcUser> queryWrapper = new LambdaQueryWrapper<>();
        String cellphone = retrievePasswordDTO.getCellphone();
        String email = retrievePasswordDTO.getEmail();
        if (cellphone != null) {
            queryWrapper.eq(XcUser::getCellphone, cellphone);
        } else if (email != null) {
            queryWrapper.eq(XcUser::getEmail, email);
        } else {
            throw new RuntimeException("手机号和邮箱不能同时为空");
        }
        XcUser xcUser = xcUserMapper.selectOne(queryWrapper);
        if (xcUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新密码
        xcUser.setPassword(passwordEncoder.encode(password));
        xcUserMapper.insert(xcUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterDTO registerDTO) {
        // 两次密码是否一致
        String password = registerDTO.getPassword();
        String confirmPwd = registerDTO.getConfirmpwd();
        if (!password.equals(confirmPwd)) {
            throw new RuntimeException("两次密码不一致");
        }

        // 校验验证码
        String checkCodeKey = registerDTO.getCheckcodekey();
        String checkCode = registerDTO.getCheckcode();
        Boolean verify = checkCodeClient.verify(checkCodeKey, checkCode);
        if (!verify) {
            throw new RuntimeException("验证码错误");
        }

        // 校验用户是否存在
        String username = registerDTO.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (xcUser != null) {
            throw new RuntimeException("用户已存在");
        }

        // 向用户表、用户角色关系表写入数据
        String userId = UUID.randomUUID().toString();
        xcUser = new XcUser();
        xcUser.setId(userId);
        // 学生类型
        xcUser.setUtype("101001");
        // 用户状态
        xcUser.setStatus("1");
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);

        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        // 学生角色
        xcUserRole.setRoleId("17");
        xcUserRoleMapper.insert(xcUserRole);
    }
}
