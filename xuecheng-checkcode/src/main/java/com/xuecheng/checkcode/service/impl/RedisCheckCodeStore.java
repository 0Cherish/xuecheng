package com.xuecheng.checkcode.service.impl;

import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 使用redis存储验证码，测试用
 *
 * @author Lin
 * @date 2024/2/21 17:36
 */
@Component("RedisCheckCodeStore")
public class RedisCheckCodeStore implements CheckCodeService.CheckCodeStore {

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;


    @Override
    public void set(String key, String value, Integer expire) {
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }
}
