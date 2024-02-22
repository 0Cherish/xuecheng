package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDTO;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.AuthService;
import com.xuecheng.ucenter.service.WxAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 微信扫描认证
 *
 * @author Lin
 * @date 2024/2/21 16:56
 */
@Slf4j
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService, WxAuthService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WxAuthService currentProxy;

    @Value("${wexin.appid}")
    private String appid;

    @Value("${weixin.secret}")
    private String secret;

    @Override
    public XcUserExt execute(AuthParamsDTO authParamsDTO) {
        String username = authParamsDTO.getUsername();
        XcUser user = xcUserMapper.selectOne(
                new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));

        if (user == null) {
            throw new RuntimeException("账号不存在");
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user, xcUserExt);
        return xcUserExt;
    }

    @Override
    public XcUser wxAuth(String code) {
        // 使用授权码获取令牌
        Map<String, String> accessTokenMap = getAccessToken(code);
        if (accessTokenMap == null) {
            return null;
        }

        // 使用令牌获取用户信息
        String openid = accessTokenMap.get("openid");
        String accessToken = accessTokenMap.get("access_token");
        Map<String, String> userInfo = getUserInfo(accessToken, openid);
        if (userInfo == null) {
            return null;
        }

        // 保存用户到数据库
        return currentProxy.addWxUser(userInfo);
    }

    /**
     * 保存用户到数据库
     *
     * @param userInfoMap 微信用户信息
     * @return 用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public XcUser addWxUser(Map<String, String> userInfoMap) {
        String unionid = userInfoMap.get("unionid");
        XcUser xcUser = xcUserMapper.selectOne(
                new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));

        if (xcUser != null) {
            return xcUser;
        }

        String userId = UUID.randomUUID().toString();
        xcUser = new XcUser();
        xcUser.setId(userId);
        xcUser.setWxUnionid(unionid);
        // 记录微信用户信息
        xcUser.setNickname(userInfoMap.get("nickname"));
        xcUser.setUserpic(userInfoMap.get("headimagurl"));
        xcUser.setName(userInfoMap.get("nickname"));
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
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

        return xcUser;
    }

    /**
     * 申请访问令牌
     *
     * @param code 授权码
     */
    private Map<String, String> getAccessToken(String code) {
        String wxUrlTemplate = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String wxUrl = String.format(wxUrlTemplate, appid, secret, code);
        log.info("调用微信接口申请access_token，url：{}", wxUrl);

        ResponseEntity<String> response = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);
        String result = response.getBody();
        log.info("调用微信接口申请access_token，返回值：{}", result);

        return JSON.parseObject(result, Map.class);
    }

    /**
     * 获取用户信息
     *
     * @param accessToken 令牌
     * @param openid      openid
     */
    private Map<String, String> getUserInfo(String accessToken, String openid) {
        String wxUrlTemplate = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String wxUrl = String.format(wxUrlTemplate, accessToken, openid);
        log.info("调用微信接口获取用户信息，url：{}", wxUrl);

        ResponseEntity<String> response = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);
        // 防止乱码进行转码
        String result = new String(response.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        log.info("调用微信接口获取用户信息，返回值：{}", result);

        return JSON.parseObject(result, Map.class);
    }
}
