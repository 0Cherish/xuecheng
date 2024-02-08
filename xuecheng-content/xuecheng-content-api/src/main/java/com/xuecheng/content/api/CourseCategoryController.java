package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDTO;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程分类相关接口
 *
 * @author Lin
 * @date 2024/2/8 13:31
 */
@Api(value = "课程分类相关接口", tags = "课程分类相关接口")
@RestController
@RequestMapping("/course-category")
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @ApiOperation("课程分类查询接口")
    @GetMapping("/tree-nodes")
    public List<CourseCategoryTreeDTO> queryTreeNodes() {
        return courseCategoryService.queryTreeNodes("1");
    }
}
