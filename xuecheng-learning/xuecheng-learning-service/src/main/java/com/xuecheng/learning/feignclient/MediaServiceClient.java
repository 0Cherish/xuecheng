package com.xuecheng.learning.feignclient;

import com.xuecheng.base.model.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 媒资服务远程接口
 *
 * @author Lin
 * @date 2024/2/22 15:53
 */
@FeignClient(value = "media-api", fallbackFactory = MediaServiceClientFallbackFactory.class)
@RequestMapping("/media")
public interface MediaServiceClient {

    /**
     * 媒资预览
     *
     * @param mediaId 媒资id
     * @return 预览
     */
    @GetMapping("/open/preview/{mediaId}")
    RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId);

}
