package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDTO;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author Lin
 * @date 2024/2/7 18:02
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /**
     * 查询课程计划
     *
     * @param courseId 课程id
     * @return 查询结果
     */
    List<TeachplanDTO> selectTreeNodes(long courseId);
}
