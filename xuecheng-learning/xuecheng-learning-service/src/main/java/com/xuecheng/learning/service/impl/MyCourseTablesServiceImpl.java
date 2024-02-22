package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.SystemException;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.XcChooseCourseDTO;
import com.xuecheng.learning.model.dto.XcCourseTablesDTO;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Lin
 * @date 2024/2/22 16:10
 */
@Slf4j
@Service
public class MyCourseTablesServiceImpl implements MyCourseTablesService {

    @Autowired
    private XcChooseCourseMapper xcChooseCourseMapper;

    @Autowired
    private XcCourseTablesMapper xcCourseTablesMapper;

    @Autowired
    private ContentServiceClient contentServiceClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public XcChooseCourseDTO addChooseCourse(String userId, Long courseId) {
        // 查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        String charge = coursepublish.getCharge();

        // 选课记录
        XcChooseCourse chooseCourse;
        // 课程免费
        if ("201000".equals(charge)) {
            // 添加免费课程
            chooseCourse = addFreeCourse(userId, coursepublish);
            // 添加到我的课程表
            XcCourseTables xcCourseTables = addCourseTables(chooseCourse);
        } else {
            // 添加收费课程
            chooseCourse = addChargeCourse(userId, coursepublish);
        }

        XcChooseCourseDTO xcChooseCourseDTO = new XcChooseCourseDTO();
        BeanUtils.copyProperties(chooseCourse, xcChooseCourseDTO);
        // 获取学习资格
        XcCourseTablesDTO xcCourseTablesDTO = getLearningStatus(userId, courseId);
        xcChooseCourseDTO.setLearnStatus(xcCourseTablesDTO.getLearnStatus());

        return xcChooseCourseDTO;
    }

    @Override
    public XcCourseTablesDTO getLearningStatus(String userId, Long courseId) {
        // 查询我的课程表
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        if (xcCourseTables == null) {
            XcCourseTablesDTO xcCourseTablesDTO = new XcCourseTablesDTO();
            // 没有选课或选课后没有支付
            xcCourseTablesDTO.setLearnStatus("702002");
            return xcCourseTablesDTO;
        }

        XcCourseTablesDTO xcCourseTablesDTO = new XcCourseTablesDTO();
        BeanUtils.copyProperties(xcCourseTables, xcCourseTablesDTO);
        // 是否过期
        boolean isExpires = xcCourseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if (!isExpires) {
            // 正常学习
            xcCourseTablesDTO.setLearnStatus("702001");
            return xcCourseTablesDTO;
        } else {
            // 已过期
            xcCourseTablesDTO.setLearnStatus("702003");
            return xcCourseTablesDTO;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveChooseCourseSuccess(String chooseCourseId) {
        // 根据选课id查询选课表
        XcChooseCourse chooseCourse = xcChooseCourseMapper.selectById(chooseCourseId);
        if (chooseCourse == null) {
            log.debug("接收购买课程的消息，根据选课id从数据库中找不到选课记录，选课id：{}", chooseCourseId);
            return false;
        }

        // 选课状态
        String status = chooseCourse.getStatus();
        // 未支付时
        if ("701002".equals(status)) {
            chooseCourse.setStatus("701001");
            int i = xcChooseCourseMapper.updateById(chooseCourse);
            if (i <= 0) {
                log.debug("添加选课记录失败：{}", chooseCourse);
                SystemException.cast("添加选课记录失败");
            }

            // 添加到课程表
            addCourseTables(chooseCourse);
            return true;
        }

        return false;
    }

    /**
     * 添加免费课程
     *
     * @param userId        用户id
     * @param coursePublish 课程信息
     * @return 选课信息
     */
    public XcChooseCourse addFreeCourse(String userId, CoursePublish coursePublish) {
        // 查询过选课记录表中是否存在免费的且选课成功的订单
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, coursePublish.getId())
                // 免费课程
                .eq(XcChooseCourse::getOrderType, "700001")
                // 选课成功
                .eq(XcChooseCourse::getStatus, "701001");
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses != null && !xcChooseCourses.isEmpty()) {
            return xcChooseCourses.get(0);
        }

        // 添加选课记录信息
        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        xcChooseCourse.setCourseId(coursePublish.getId());
        xcChooseCourse.setCourseName(coursePublish.getName());
        //免费课程价格为0
        xcChooseCourse.setCoursePrice(0f);
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursePublish.getCompanyId());
        //免费课程
        xcChooseCourse.setOrderType("700001");
        xcChooseCourse.setCreateDate(LocalDateTime.now());
        //选课成功
        xcChooseCourse.setStatus("701001");

        //免费课程默认365
        xcChooseCourse.setValidDays(365);
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());

        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));
        xcChooseCourseMapper.insert(xcChooseCourse);

        return xcChooseCourse;
    }

    /**
     * 添加收费课程
     *
     * @param userId        用户id
     * @param coursePublish 课程信息
     * @return 选课信息
     */
    public XcChooseCourse addChargeCourse(String userId, CoursePublish coursePublish) {
        // 存在待支付交易记录直接返回
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, coursePublish.getId())
                // 收费订单
                .eq(XcChooseCourse::getOrderType, "700002")
                // 待支付
                .eq(XcChooseCourse::getStatus, "701002");
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(queryWrapper);
        if (xcChooseCourses != null && !xcChooseCourses.isEmpty()) {
            return xcChooseCourses.get(0);
        }

        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        xcChooseCourse.setCourseId(coursePublish.getId());
        xcChooseCourse.setCourseName(coursePublish.getName());
        xcChooseCourse.setCoursePrice(coursePublish.getPrice());
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursePublish.getCompanyId());
        //收费课程
        xcChooseCourse.setOrderType("700002");
        xcChooseCourse.setCreateDate(LocalDateTime.now());
        //待支付
        xcChooseCourse.setStatus("701002");

        xcChooseCourse.setValidDays(coursePublish.getValidDays());
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());
        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursePublish.getValidDays()));
        xcChooseCourseMapper.insert(xcChooseCourse);

        return xcChooseCourse;
    }

    /**
     * 添加到我的课程表
     *
     * @param xcChooseCourse 选课信息
     * @return 课程表
     */
    public XcCourseTables addCourseTables(XcChooseCourse xcChooseCourse) {
        // 选课记录完成且未过期可以添加课程到课程表
        String status = xcChooseCourse.getStatus();
        if (!"701001".equals(status)) {
            SystemException.cast("选课未成功，无法添加到课程表");
        }

        // 查询我的课程表
        XcCourseTables xcCourseTables = getXcCourseTables(xcChooseCourse.getUserId(), xcChooseCourse.getCourseId());
        if (xcCourseTables != null) {
            return xcCourseTables;
        }

        XcCourseTables xcCourseTablesNew = new XcCourseTables();
        xcCourseTablesNew.setChooseCourseId(xcChooseCourse.getId());
        xcCourseTablesNew.setUserId(xcChooseCourse.getUserId());
        xcCourseTablesNew.setCourseId(xcChooseCourse.getCourseId());
        xcCourseTablesNew.setCompanyId(xcChooseCourse.getCompanyId());
        xcCourseTablesNew.setCourseName(xcChooseCourse.getCourseName());
        xcCourseTablesNew.setCreateDate(LocalDateTime.now());
        xcCourseTablesNew.setValidtimeStart(xcChooseCourse.getValidtimeStart());
        xcCourseTablesNew.setValidtimeEnd(xcChooseCourse.getValidtimeEnd());
        xcCourseTablesNew.setCourseType(xcChooseCourse.getOrderType());
        xcCourseTablesMapper.insert(xcCourseTablesNew);

        return xcCourseTablesNew;
    }

    /**
     * 根据课程和用户查询过课程表中的一门课程
     *
     * @param userId   用户id
     * @param courseId 课程id
     * @return 课程表
     */
    public XcCourseTables getXcCourseTables(String userId, Long courseId) {
        return xcCourseTablesMapper.selectOne(
                new LambdaQueryWrapper<XcCourseTables>()
                        .eq(XcCourseTables::getUserId, userId)
                        .eq(XcCourseTables::getCourseId, courseId)
        );
    }
}
