package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.dto.TeachplanDTO;
import com.xuecheng.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Lin
 * @date 2024/2/15 14:00
 */
@SpringBootTest
public class TeachplanMapperTests {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Test
    public void testSelectTreeNodes() {
        List<TeachplanDTO> teachplanDTOS = teachplanMapper.selectTreeNodes(117);
        System.out.println(teachplanDTOS);
    }
}
