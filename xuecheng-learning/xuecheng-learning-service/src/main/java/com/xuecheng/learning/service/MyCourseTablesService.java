package com.xuecheng.learning.service;

import com.xuecheng.base.model.PageResult;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDTO;
import com.xuecheng.learning.model.dto.XcCourseTablesDTO;
import com.xuecheng.learning.model.po.XcCourseTables;

/**
 * @author Lin
 * @date 2024/2/22 16:08
 */
public interface MyCourseTablesService {

    /**
     * 添加选课
     *
     * @param userId   用户id
     * @param courseId 课程id
     * @return 选课信息
     */
    XcChooseCourseDTO addChooseCourse(String userId, Long courseId);

    /**
     * 判断学习资格
     *
     * @param userId   用户id
     * @param courseId 课程id
     * @return 课程表
     */
    XcCourseTablesDTO getLearningStatus(String userId, Long courseId);

    /**
     * 保存选课成功
     *
     * @param chooseCourseId 选课id
     * @return 成功
     */
    boolean saveChooseCourseSuccess(String chooseCourseId);

    /**
     * 我的课程表
     *
     * @param params 查询参数
     * @return 返回结果
     */
    PageResult<XcCourseTables> myCourseTables(MyCourseTableParams params);
}
