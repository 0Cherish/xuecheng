package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDTO;

/**
 * @author Lin
 * @date 2024/2/19 14:14
 */
public interface CoursePublishService {

    /**
     * 获取课程预览信息
     *
     * @param courseId 课程id
     * @return 课程预览信息
     */
    CoursePreviewDTO getCoursePreviewInfo(Long courseId);
}
