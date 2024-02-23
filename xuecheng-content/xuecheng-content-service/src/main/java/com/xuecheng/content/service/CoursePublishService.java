package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDTO;
import com.xuecheng.content.model.po.CoursePublish;

import java.io.File;

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

    /**
     * 提交审核
     *
     * @param companyId 机构id
     * @param courseId  课程id
     */
    void commitAudit(Long companyId, Long courseId);

    /**
     * 课程发布
     *
     * @param companyId 机构id
     * @param courseId  课程id
     */
    void publish(Long companyId, Long courseId);

    /**
     * 静态化文件
     *
     * @param courseId 课程id
     * @return 文件
     */
    File generateCourseHtml(Long courseId);

    /**
     * 上传课程静态化页面
     *
     * @param courseId 课程id
     * @param file     文件
     */
    void uploadCourseHtml(Long courseId, File file);

    /**
     * 查询课程发布信息
     *
     * @param courseId 课程id
     * @return 课程发布信息
     */
    CoursePublish getCoursePublish(Long courseId);

    /**
     * 从缓存查询课程发布信息
     *
     * @param courseId 课程id
     * @return 课程发布信息
     */
    CoursePublish getCoursePublishCache(Long courseId);
}
