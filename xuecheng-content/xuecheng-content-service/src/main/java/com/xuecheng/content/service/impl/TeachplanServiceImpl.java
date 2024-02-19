package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.SystemException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDTO;
import com.xuecheng.content.model.dto.SaveTeachplanDTO;
import com.xuecheng.content.model.dto.TeachplanDTO;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lin
 * @date 2024/2/15 14:07
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDTO> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveTeachplan(SaveTeachplanDTO teachplanDTO) {
        // 课程计划id
        Long id = teachplanDTO.getId();
        // 修改课程计划
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDTO, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {
            // 取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDTO.getCourseId(), teachplanDTO.getParentid());
            Teachplan teachplan = new Teachplan();
            // 设置排序号
            teachplan.setOrderby(count + 1);
            BeanUtils.copyProperties(teachplanDTO, teachplan);
            teachplanMapper.insert(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        // 删除第二级别的小章节
        if (teachplan.getGrade() == 2) {
            // 删除课程计划
            teachplanMapper.deleteById(id);
            // 删除媒资信息
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId, id);
            teachplanMediaMapper.delete(queryWrapper);
        } else {
            // 删除第一级别的大章节
            int count = getTeachplanCount(teachplan.getCourseId(), teachplan.getId());
            if (count > 0) {
                SystemException.cast("课程计划信息还有子级信息，无法操作");
            }
            teachplanMapper.deleteById(id);
            // 删除子章节及其媒资
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, id);
            List<Teachplan> teachplanList = teachplanMapper.selectList(queryWrapper);
            List<Long> ids = teachplanList.stream().map(Teachplan::getId).collect(Collectors.toList());

            if (!ids.isEmpty()) {
                teachplanMapper.deleteBatchIds(ids);

                LambdaQueryWrapper<TeachplanMedia> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.in(TeachplanMedia::getTeachplanId, ids);
                teachplanMediaMapper.delete(queryWrapper1);
            }
        }
    }

    @Override
    public void sortTeachplan(boolean up, Long id) {
        // 查询需要调换的课程计划
        Teachplan teachplan = teachplanMapper.selectById(id);
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
        if (up) {
            queryWrapper.lt(Teachplan::getOrderby, teachplan.getOrderby());
            queryWrapper.orderByDesc(Teachplan::getOrderby);
        } else {
            queryWrapper.gt(Teachplan::getOrderby, teachplan.getOrderby());
            queryWrapper.orderByAsc(Teachplan::getOrderby);
        }
        queryWrapper.last("limit 1");
        Teachplan teachplan1 = teachplanMapper.selectOne(queryWrapper);

        // 交换各自的排序字段
        if (teachplan1 != null) {
            Integer temp = teachplan.getOrderby();
            teachplan.setOrderby(teachplan1.getOrderby());
            teachplan1.setOrderby(temp);
            teachplanMapper.updateById(teachplan);
            teachplanMapper.updateById(teachplan1);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void associationMedia(BindTeachplanMediaDTO bindTeachplanMediaDTO) {
        Long teachplanId = bindTeachplanMediaDTO.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null) {
            SystemException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if (grade != 2) {
            SystemException.cast("只允许第二级教学计划绑定媒资文件");
        }
        Long courseId = teachplan.getCourseId();

        // 删除原来该教学计划绑定的媒资
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
        teachplanMediaMapper.delete(queryWrapper);

        // 添加教学计划和媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDTO.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDTO.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
    }

    @Override
    public void unAssociationMedia(Long teachPlanId, String mediaId) {
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId);
        queryWrapper.eq(TeachplanMedia::getMediaId, mediaId);
        teachplanMediaMapper.delete(queryWrapper);
    }

    /**
     * 获取最新的排序号
     *
     * @param courseId 课程id
     * @param parentId 父课程id
     * @return 最新排序号
     */
    private int getTeachplanCount(long courseId, long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        return teachplanMapper.selectCount(queryWrapper);
    }
}
