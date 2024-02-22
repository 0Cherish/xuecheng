package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lin
 * @date 2024/2/10 12:53
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 系统异常
     *
     * @param e 异常信息
     * @return 异常响应
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(SystemException e) {
        log.error("系统异常：{}", e.getMessage(), e);

        return new RestErrorResponse(e.getMessage());
    }

    /**
     * 系统异常
     *
     * @param e 异常信息
     * @return 异常响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        // 存储错误信息
        List<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(item -> errors.add(item.getDefaultMessage()));

        String errMessage = StringUtils.join(errors, ",");

        log.error("系统异常：{}", errMessage, e);

        return new RestErrorResponse(errMessage);
    }

    /**
     * 其他异常
     *
     * @param e 异常信息
     * @return 异常响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);

        if ("不允许访问".equals(e.getMessage())) {
            return new RestErrorResponse("没有操作此功能的权限");
        }

        return new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());
    }
}
