package com.xuecheng.content;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lin
 * @date 2024/2/7 18:14
 */
@SpringBootTest
public class CourseBaseInfoServiceTests {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Test
    public void testCourseBaseInfoService() {

        Long companyId = 1232141425L;
        // 查询条件
        QueryCourseParamsDTO courseParamsDTO = new QueryCourseParamsDTO();
        courseParamsDTO.setCourseName("java");
        courseParamsDTO.setAuditStatus("202004");

        // 分页参数对象
        PageParams pageParams = new PageParams(1L, 5L);

        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(companyId, pageParams, courseParamsDTO);
        System.out.println(courseBasePageResult);
    }
}
