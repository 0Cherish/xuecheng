package com.xuecheng.learning.api;

import com.xuecheng.base.exception.SystemException;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDTO;
import com.xuecheng.learning.model.dto.XcCourseTablesDTO;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import com.xuecheng.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 我的课程表接口
 *
 * @author Lin
 * @date 2024/2/22 15:48
 */

@Api(value = "我的课程表接口", tags = "我的课程表接口")
@Slf4j
@RestController
public class MyCourseTablesController {

    @Autowired
    private MyCourseTablesService courseTablesService;

    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDTO addChooseCourse(@PathVariable("courseId") Long courseId) {
        // 登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            SystemException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        return courseTablesService.addChooseCourse(userId, courseId);
    }

    @ApiOperation("查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDTO getLearnStatus(@PathVariable("courseId") Long courseId) {
        // 登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            SystemException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        return courseTablesService.getLearningStatus(userId, courseId);
    }

    @ApiOperation("我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> myCourseTable(MyCourseTableParams params) {
        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            SystemException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        //设置当前的登录用户
        params.setUserId(userId);
        return courseTablesService.myCourseTables(params);
    }
}
