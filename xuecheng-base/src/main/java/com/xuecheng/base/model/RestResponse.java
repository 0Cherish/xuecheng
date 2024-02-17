package com.xuecheng.base.model;

import lombok.Data;

/**
 * 通用结果类型
 *
 * @author Lin
 * @date 2024/2/17 13:53
 */
@Data
public class RestResponse<T> {
    /**
     * 响应编码，0正常，-1错误
     */
    private int code;
    /**
     * 响应提示信息
     */
    private String msg;
    /**
     * 响应内容
     */
    private T result;

    public RestResponse() {
        this(0, "success");
    }

    public RestResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 错误信息的封装
     *
     * @param msg 错误信息
     * @param <T> 内容泛型
     * @return 返回结果
     */
    public static <T> RestResponse<T> fail(String msg) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        response.setMsg(msg);
        return response;
    }

    public static <T> RestResponse<T> fail(T result, String msg) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        response.setResult(result);
        response.setMsg(msg);
        return response;
    }

    /**
     * 正常响应数据
     *
     * @param result 响应数据
     * @param <T>    数据泛型
     * @return 响应
     */
    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setResult(result);
        return response;
    }

    public static <T> RestResponse<T> success(T result, String msg) {
        RestResponse<T> response = new RestResponse<>();
        response.setResult(result);
        response.setMsg(msg);
        return response;
    }
}
