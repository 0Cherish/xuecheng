package com.xuecheng.gateway.config;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * 错误响应参数包装
 *
 * @author Lin
 * @date 2024/2/21 15:42
 */
@Data
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }
}
