package com.xuecheng.ucenter.model.dto;

import lombok.Data;

/**
 * 注册
 *
 * @author Lin
 * @date 2024/2/22 14:59
 */
@Data
public class RegisterDTO {
    private String cellphone;
    private String username;
    private String email;
    private String nickname;
    private String password;
    private String confirmpwd;
    private String checkcodekey;
    private String checkcode;
}
