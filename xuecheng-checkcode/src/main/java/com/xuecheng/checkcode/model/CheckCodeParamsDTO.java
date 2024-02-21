package com.xuecheng.checkcode.model;

import lombok.Data;

/**
 * 验证码生成参数类
 *
 * @author Lin
 * @date 2024/2/21 17:26
 */
@Data
public class CheckCodeParamsDTO {

    /**
     * 验证码类型:pic、sms、email等
     */
    private String checkCodeType;

    /**
     * 业务携带参数
     */
    private String param1;
    private String param2;
    private String param3;
}
