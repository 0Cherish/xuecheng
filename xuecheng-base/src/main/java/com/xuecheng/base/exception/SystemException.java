package com.xuecheng.base.exception;

import lombok.Getter;

/**
 * @author Lin
 * @date 2024/2/10 12:41
 */
@Getter
public class SystemException extends RuntimeException {

    public SystemException() {
    }

    public SystemException(String message) {
        super(message);
    }

    public static void cast(String message) {
        throw new SystemException(message);
    }

    public static void cast(CommonError error){
        throw new SystemException(error.getErrMessage());
    }
}
