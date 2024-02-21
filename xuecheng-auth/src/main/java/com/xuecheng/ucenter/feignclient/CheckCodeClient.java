package com.xuecheng.ucenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 验证码服务远程接口
 *
 * @author Lin
 * @date 2024/2/21 18:44
 */
@FeignClient(value = "checkcode", fallbackFactory = CheckCodeClientFallbackFactory.class)
public interface CheckCodeClient {

    @PostMapping(value = "/checkcode/verify")
    Boolean verify(@RequestParam("key") String key, @RequestParam("code") String code);

}
