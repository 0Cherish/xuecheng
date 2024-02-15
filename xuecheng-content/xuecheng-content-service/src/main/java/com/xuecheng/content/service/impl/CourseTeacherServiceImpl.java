package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.SystemException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Lin
 * @date 2024/2/15 17:11
 */
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> list(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Transactional
    @Override
    public CourseTeacher saveCourseTeacher(Long companyId, CourseTeacher courseTeacher) {
        Long courseId = courseTeacher.getCourseId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            SystemException.cast("课程不存在");
        }

        if (!companyId.equals(courseBase.getCompanyId())) {
            SystemException.cast("本机构只能添加本机构的教师");
        }

        Long id = courseTeacher.getId();
        // 修改教师
        if (id != null){
            courseTeacherMapper.updateById(courseTeacher);
        }else {
            courseTeacher.setCreateDate(LocalDateTime.now());
            courseTeacherMapper.insert(courseTeacher);
        }

        return courseTeacherMapper.selectById(courseTeacher.getId());
    }

    @Override
    public void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            SystemException.cast("课程不存在");
        }

        if (!companyId.equals(courseBase.getCompanyId())) {
            SystemException.cast("本机构只能添加本机构的教师");
        }

        courseTeacherMapper.deleteById(teacherId);
    }
}
