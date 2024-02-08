package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDTO;
import com.xuecheng.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author Lin
 * @date 2024/2/7 18:02
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * 使用递归查询分类
     *
     * @param id 节点id
     * @return 查询结果
     */
    List<CourseCategoryTreeDTO> selectTreeNodes(String id);
}
