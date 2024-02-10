package com.xuecheng.content;

import com.xuecheng.content.model.dto.CourseCategoryTreeDTO;
import com.xuecheng.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Lin
 * @date 2024/2/7 18:14
 */
@SpringBootTest
public class CourseCategoryServiceTests {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Test
    public void testCourseCategoryService() {
        List<CourseCategoryTreeDTO> courseCategoryTreeDTOS = courseCategoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDTOS);
    }
}
