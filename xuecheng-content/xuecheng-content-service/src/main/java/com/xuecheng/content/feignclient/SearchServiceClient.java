package com.xuecheng.content.feignclient;

import com.xuecheng.content.model.dto.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

/**
 * 搜索服务远程接口
 *
 * @author Lin
 * @date 2024/2/20 17:00
 */
@FeignClient(value = "search", fallbackFactory = SearchServiceClientFallbackFactory.class)
public interface SearchServiceClient {

    /**
     * 添加索引
     *
     * @param courseIndex 索引
     * @return 添加成功
     */
    @PostMapping("/search/index/course")
    Boolean add(@RequestPart CourseIndex courseIndex);
}
