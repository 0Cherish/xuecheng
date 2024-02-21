package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDTO;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * @author Lin
 * @date 2024/2/21 16:52
 */
public interface AuthService {

    /**
     * 认证方法
     *
     * @param authParamsDTO 认证参数
     * @return 用户信息
     */
    XcUserExt execute(AuthParamsDTO authParamsDTO);
}
