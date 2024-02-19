package com.xuecheng.content.service.impl;

import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.CoursePreviewDTO;
import com.xuecheng.content.model.dto.TeachplanDTO;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lin
 * @date 2024/2/19 14:16
 */
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private TeachplanService teachplanService;

    @Override
    public CoursePreviewDTO getCoursePreviewInfo(Long courseId) {
        CourseBaseInfoDTO courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        List<TeachplanDTO> teachplanTree = teachplanService.findTeachplanTree(courseId);

        CoursePreviewDTO coursePreviewDTO = new CoursePreviewDTO();
        coursePreviewDTO.setCourseBase(courseBaseInfo);
        coursePreviewDTO.setTeachplans(teachplanTree);
        return coursePreviewDTO;
    }
}
