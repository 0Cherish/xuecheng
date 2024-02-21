package com.xuecheng.checkcode.service;

import com.xuecheng.checkcode.model.CheckCodeParamsDTO;
import com.xuecheng.checkcode.model.CheckCodeResultDTO;

/**
 * 验证码接口
 *
 * @author Lin
 * @date 2024/2/21 17:27
 */
public interface CheckCodeService {


    /**
     * 生成验证码
     *
     * @param checkCodeParamsDto 生成验证码参数
     * @return 验证码结果
     */
    CheckCodeResultDTO generate(CheckCodeParamsDTO checkCodeParamsDto);

    /**
     * 校验验证码
     *
     * @param key  key
     * @param code 验证码
     * @return 校验成功
     */
    boolean verify(String key, String code);


    /**
     * 验证码生成器
     */
    interface CheckCodeGenerator {
        /**
         * 验证码生成
         *
         * @param length 长度
         * @return 验证码
         */
        String generate(int length);
    }

    /**
     * key生成器
     */
    interface KeyGenerator {

        /**
         * key生成
         *
         * @param prefix 前缀
         * @return 验证码
         */
        String generate(String prefix);
    }


    /**
     * 验证码存储
     */
    interface CheckCodeStore {

        /**
         * 向缓存设置key
         *
         * @param key    key
         * @param value  value
         * @param expire 过期时间,单位秒
         */
        void set(String key, String value, Integer expire);

        /**
         * 从缓存获得key
         *
         * @param key key
         * @return 值
         */
        String get(String key);

        /**
         * 从缓存删除key
         *
         * @param key key
         */
        void remove(String key);
    }
}
