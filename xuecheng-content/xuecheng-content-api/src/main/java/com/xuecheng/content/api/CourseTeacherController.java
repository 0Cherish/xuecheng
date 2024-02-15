package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lin
 * @date 2024/2/15 17:09
 */
@Api(value = "师资相关接口", tags = "师资相关接口")
@RestController
@RequestMapping("/courseTeacher")
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    @ApiOperation("教师查询")
    @GetMapping("/list/{courseId}")
    public List<CourseTeacher> list(@PathVariable Long courseId) {
        return courseTeacherService.list(courseId);
    }

    @ApiOperation("保存教师")
    @PostMapping
    public CourseTeacher saveCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        // TODO 获取用户所属机构id
        Long companyId = 1232141425L;
        return courseTeacherService.saveCourseTeacher(companyId, courseTeacher);
    }

    @ApiOperation("删除教师")
    @DeleteMapping("/course/{courseId}/{teacherId}")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        // TODO 获取用户所属机构id
        Long companyId = 1232141425L;
        courseTeacherService.deleteCourseTeacher(companyId, courseId, teacherId);
    }
}
