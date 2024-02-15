package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author Lin
 * @date 2024/2/15 17:11
 */
public interface CourseTeacherService {

    /**
     * 查询过教师
     *
     * @param courseId 课程id
     * @return 查询过结果
     */
    List<CourseTeacher> list(Long courseId);

    /**
     * 保存教师
     *
     * @param companyId     机构id
     * @param courseTeacher 教师信息
     * @return 教师信息
     */
    CourseTeacher saveCourseTeacher(Long companyId, CourseTeacher courseTeacher);

    /**
     * 删除教师
     *
     * @param companyId 机构id
     * @param courseId  课程id
     * @param teacherId 教师id
     */
    void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId);
}
