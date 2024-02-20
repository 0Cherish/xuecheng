package com.xuecheng.search.controller;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.search.dto.SearchCourseParamDTO;
import com.xuecheng.search.dto.SearchPageResultDTO;
import com.xuecheng.search.po.CourseIndex;
import com.xuecheng.search.service.CourseSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 课程搜索接口
 *
 * @author Lin
 * @date 2024/2/20 16:13
 */
@Api(value = "课程搜索接口", tags = "课程搜索接口")
@RestController
@RequestMapping("/course")
public class CourseSearchController {

    @Autowired
    CourseSearchService courseSearchService;


    @ApiOperation("课程搜索列表")
    @GetMapping("/list")
    public SearchPageResultDTO<CourseIndex> list(PageParams pageParams, SearchCourseParamDTO searchCourseParamDto) {

        return courseSearchService.queryCoursePubIndex(pageParams, searchCourseParamDto);

    }
}
