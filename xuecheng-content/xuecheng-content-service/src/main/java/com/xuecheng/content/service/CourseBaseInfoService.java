package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDTO;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
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
     * @param pageParams      分页参数
     * @param courseParamsDTO 查询条件
     * @return 查询结果
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDTO courseParamsDTO);

    /**
     * 新增课程
     *
     * @param companyId    机构id
     * @param addCourseDTO 新增课程信息
     * @return 课程基本信息
     */
    CourseBaseInfoDTO createCourseBase(Long companyId, AddCourseDTO addCourseDTO);
}
