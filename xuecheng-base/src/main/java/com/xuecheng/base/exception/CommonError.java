package com.xuecheng.base.exception;

import lombok.Getter;

/**
 * 通用错误信息
 *
 * @author Lin
 * @date 2024/2/10 12:42
 */
@Getter
public enum CommonError {

    /**
     * 未知异常
     */
    UNKNOWN_ERROR("执行过程异常，请重试"),
    /**
     * 非法参数
     */
    PARAMS_ERROR("非法参数"),
    /**
     * 对象为空
     */
    OBJECT_NULL("对象为空"),
    /**
     * 查询结果为空
     */
    QUERY_NULL("查询结果为空"),
    /**
     * 请求参数为空
     */
    REQUEST_NULL("请求参数为空");
    ;

    private final String errMessage;

    CommonError(String errMessage) {
        this.errMessage = errMessage;
    }

}
