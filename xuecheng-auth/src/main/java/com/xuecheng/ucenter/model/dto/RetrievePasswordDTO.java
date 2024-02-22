package com.xuecheng.ucenter.model.dto;

import lombok.Data;

/**
 * 找回密码
 *
 * @author Lin
 * @date 2024/2/22 14:57
 */
@Data
public class RetrievePasswordDTO {
    private String cellphone;
    private String email;
    private String checkcode;
    private String checkcodekey;
    private String confirmpwd;
    private String password;
}
