package com.xuecheng.content.util;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 获取当前用户身份工具类
 *
 * @author Lin
 * @date 2024/2/21 16:25
 */
@Slf4j
public class SecurityUtil {

    public static XcUser getUser() {
        try {
            Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principalObj instanceof String) {
                String principal = principalObj.toString();
                return JSON.parseObject(principal, XcUser.class);
            }
        } catch (Exception e) {
            log.error("获取当前用户身份出错：{}", e.getMessage());
        }
        return null;
    }

    @Data
    public static class XcUser implements Serializable {

        private static final long serialVersionUID = 1L;

        private String id;

        private String username;

        private String password;

        private String salt;

        private String name;
        private String nickname;
        private String wxUnionid;
        private String companyId;
        /**
         * 头像
         */
        private String userpic;

        private String utype;

        private LocalDateTime birthday;

        private String sex;

        private String email;

        private String cellphone;

        private String qq;

        /**
         * 用户状态
         */
        private String status;

        private LocalDateTime createTime;

        private LocalDateTime updateTime;


    }
}
