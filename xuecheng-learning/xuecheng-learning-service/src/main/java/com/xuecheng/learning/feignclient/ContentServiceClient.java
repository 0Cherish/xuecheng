package com.xuecheng.learning.feignclient;

import com.xuecheng.content.model.po.CoursePublish;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 内容管理远程接口
 *
 * @author Lin
 * @date 2024/2/22 15:53
 */
@FeignClient(value = "content-api", fallbackFactory = ContentServiceClientFallbackFactory.class)
public interface ContentServiceClient {

    /**
     * 查询课程发布信息
     *
     * @param courseId 课程id
     * @return 课程发布信息
     */
    @ResponseBody
    @GetMapping("/content/r/coursepublish/{courseId}")
    CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId);

}
