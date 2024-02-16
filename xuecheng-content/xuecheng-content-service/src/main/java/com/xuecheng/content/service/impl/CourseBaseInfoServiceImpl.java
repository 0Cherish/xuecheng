package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.SystemException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.AddCourseDTO;
import com.xuecheng.content.model.dto.CourseBaseInfoDTO;
import com.xuecheng.content.model.dto.EditCourseDTO;
import com.xuecheng.content.model.dto.QueryCourseParamsDTO;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Lin
 * @date 2024/2/8 11:02
 */
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDTO courseParamsDTO) {
        // 拼装查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDTO.getCourseName()), CourseBase::getName, courseParamsDTO.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDTO.getAuditStatus()), CourseBase::getAuditStatus, courseParamsDTO.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDTO.getPublishStatus()), CourseBase::getStatus, courseParamsDTO.getPublishStatus());

        // 创建page分页参数对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        List<CourseBase> items = pageResult.getRecords();
        long total = pageResult.getTotal();

        return new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Transactional
    @Override
    public CourseBaseInfoDTO createCourseBase(Long companyId, AddCourseDTO addCourseDTO) {

        // 向课程基本信息表写入数据
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDTO, courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        // 审核状态
        courseBase.setAuditStatus("202002");
        // 发布状态
        courseBase.setStatus("203001");
        int count = courseBaseMapper.insert(courseBase);
        if (count <= 0) {
            SystemException.cast("添加课程失败");
        }

        // 向课程营销表写入数据
        CourseMarket courseMarket = new CourseMarket();
        Long courseId = courseBase.getId();
        BeanUtils.copyProperties(addCourseDTO, courseMarket);
        courseMarket.setId(courseBase.getId());
        // 保存营销信息
        int i = saveCourseMarket(courseMarket);
        if (i <= 0) {
            SystemException.cast("保存课程营销信息失败");
        }

        //查询课程基本信息及营销信息
        return getCourseBaseInfo(courseId);
    }

    @Override
    public CourseBaseInfoDTO getCourseBaseInfo(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        CourseBaseInfoDTO courseBaseInfoDTO = new CourseBaseInfoDTO();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDTO);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDTO);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDTO.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDTO.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDTO;

    }

    @Override
    public CourseBaseInfoDTO updateCourseBase(Long companyId, EditCourseDTO editCourseDTO) {
        Long courseId = editCourseDTO.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            SystemException.cast("课程不存在");
        }

        // 数据合法性校验
        if (!companyId.equals(courseBase.getCompanyId())) {
            SystemException.cast("本机构只能修改本机构的课程");
        }

        // 封装数据
        BeanUtils.copyProperties(editCourseDTO, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());

        // 更新课程基本信息
        int count = courseBaseMapper.updateById(courseBase);
        if (count <= 0) {
            SystemException.cast("修改课程失败");
        }

        // 更新营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDTO, courseMarket);
        saveCourseMarket(courseMarket);

        return getCourseBaseInfo(courseId);
    }

    @Override
    public void deleteCourseBase(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            SystemException.cast("课程不存在");
        }
        if (!"202002".equals(courseBase.getAuditStatus())) {
            SystemException.cast("当前课程不能删除");
        }

        // 删除课程基本信息
        courseBaseMapper.deleteById(courseId);
        // 删除课程营销信息
        courseMarketMapper.deleteById(courseId);
        // 删除课程计划
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        teachplanMapper.delete(queryWrapper);
        // 删除课程计划媒资
        LambdaQueryWrapper<TeachplanMedia> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(TeachplanMedia::getCourseId, courseId);
        teachplanMediaMapper.delete(queryWrapper1);
        // 删除课程教师信息
        LambdaQueryWrapper<CourseTeacher> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(queryWrapper2);
    }

    /**
     * 保存营销信息
     *
     * @param courseMarket 营销信息
     * @return 保存成功
     */
    private int saveCourseMarket(CourseMarket courseMarket) {
        // 参数的合法性校验
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)) {
            SystemException.cast("收费规则没有选择");
        }
        //收费规则为收费
        if ("201001".equals(charge)) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice() <= 0) {
                SystemException.cast("课程为收费价格不能为空且必须大于0");
            }
        }

        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarket.getId());
        if (courseMarketObj == null) {
            return courseMarketMapper.insert(courseMarket);
        } else {
            BeanUtils.copyProperties(courseMarket, courseMarketObj);
            courseMarketObj.setId(courseMarket.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }

    }
}
