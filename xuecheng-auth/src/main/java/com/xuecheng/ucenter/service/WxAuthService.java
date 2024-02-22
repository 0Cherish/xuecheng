package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.po.XcUser;

import java.util.Map;

/**
 * @author Lin
 * @date 2024/2/22 13:41
 */
public interface WxAuthService {

    /**
     * 微信认证
     *
     * @param code code
     * @return 用户信息
     */
    XcUser wxAuth(String code);

    /**
     * 保存用户到数据库
     *
     * @param userInfoMap 微信用户信息
     * @return 用户信息
     */
    XcUser addWxUser(Map<String, String> userInfoMap);
}
