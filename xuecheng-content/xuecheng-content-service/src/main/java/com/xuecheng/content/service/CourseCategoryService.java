package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDTO;

import java.util.List;

/**
 * @author Lin
 * @date 2024/2/8 16:56
 */
public interface CourseCategoryService {

    /**
     * 课程分类树形结构查询
     *
     * @param id 根节点
     * @return 查询结果
     */
    List<CourseCategoryTreeDTO> queryTreeNodes(String id);
}
