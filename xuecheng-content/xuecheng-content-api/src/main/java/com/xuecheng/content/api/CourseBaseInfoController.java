package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDTO;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.EditCourseDTO;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("课程分页查询")
    @PostMapping("/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDTO queryCourseParamsDTO) {
        return courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDTO);
    }

    @ApiOperation("新增课程")
    @PostMapping
    public CourseBaseInfoDTO createCourseBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDTO addCourseDTO) {
        // TODO 获取用户所属机构id
        Long companyId = 1232141425L;
        return courseBaseInfoService.createCourseBase(companyId, addCourseDTO);
    }

    @ApiOperation("根据id查询课程")
    @GetMapping("/{courseId}")
    public CourseBaseInfoDTO getCourseBaseById(@PathVariable Long courseId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user);
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程")
    @PutMapping
    public CourseBaseInfoDTO modifyCourseBase(@RequestBody @Validated(ValidationGroups.Update.class) EditCourseDTO editCourseDTO) {
        // TODO 获取用户所属机构id
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId, editCourseDTO);
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/{courseId}")
    public void deleteCourseBase(@PathVariable Long courseId) {
        courseBaseInfoService.deleteCourseBase(courseId);
    }
}
