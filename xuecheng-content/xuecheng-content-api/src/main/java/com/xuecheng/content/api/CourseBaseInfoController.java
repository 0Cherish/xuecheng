package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDTO;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 课程信息相关接口
 *
 * @author Lin
 * @date 2024/2/7 17:01
 */
@Api(value = "课程信息管理接口", tags = "课程信息管理接口")
@RestController
@RequestMapping("/course")
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDTO queryCourseParamsDTO) {
        return courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDTO);
    }

    @ApiOperation("新增课程")
    @PostMapping
    public CourseBaseInfoDTO createCourseBase(@RequestBody AddCourseDTO addCourseDTO) {
        // TODO 获取用户所属机构id
        Long companyId = 1L;
        return courseBaseInfoService.createCourseBase(companyId, addCourseDTO);
    }
}
