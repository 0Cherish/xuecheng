package com.xuecheng.checkcode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Lin
 * @date 2024/2/21 17:24
 */
@SpringBootTest
class CheckcodeApplicationTests {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("aaa","aaa");
        System.out.println("===");
    }

}
