package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.RegisterDTO;
import com.xuecheng.ucenter.model.dto.RetrievePasswordDTO;

/**
 * @author Lin
 * @date 2024/2/22 15:02
 */
public interface UserService {

    /**
     * 找回密码
     *
     * @param retrievePasswordDTO 参数
     */
    void findPassword(RetrievePasswordDTO retrievePasswordDTO);

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     */
    void register(RegisterDTO registerDTO);
}
