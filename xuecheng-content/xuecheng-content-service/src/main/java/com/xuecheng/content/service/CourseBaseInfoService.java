package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDTO;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.EditCourseDTO;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;

/**
 * 课程信息管理接口
 *
 * @author Lin
 * @date 2024/2/8 10:58
 */
public interface CourseBaseInfoService {

    /**
     * 课程分页查询
     *
     * @param companyId       机构id
     * @param pageParams      分页参数
     * @param courseParamsDTO 查询条件
     * @return 查询结果
     */
    PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDTO courseParamsDTO);

    /**
     * 新增课程
     *
     * @param companyId    机构id
     * @param addCourseDTO 新增课程信息
     * @return 课程基本信息
     */
    CourseBaseInfoDTO createCourseBase(Long companyId, AddCourseDTO addCourseDTO);

    /**
     * 根据课程id查询课程信息
     *
     * @param courseId 课程id
     * @return 课程信息
     */
    CourseBaseInfoDTO getCourseBaseInfo(Long courseId);

    /**
     * 修改课程
     *
     * @param companyId     机构id
     * @param editCourseDTO 修改课程信息
     * @return 课程基本信息
     */
    CourseBaseInfoDTO updateCourseBase(Long companyId, EditCourseDTO editCourseDTO);

    /**
     * 删除课程信息
     *
     * @param courseId 课程id
     */
    void deleteCourseBase(Long courseId);
}
