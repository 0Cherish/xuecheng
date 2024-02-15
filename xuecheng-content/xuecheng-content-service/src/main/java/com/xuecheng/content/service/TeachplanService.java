package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDTO;
import com.xuecheng.content.model.dto.TeachplanDTO;

import java.util.List;

/**
 * @author Lin
 * @date 2024/2/15 14:06
 */
public interface TeachplanService {

    /**
     * 查询过课程计划树形结构
     *
     * @param courseId 课程id
     * @return 查询结果
     */
    List<TeachplanDTO> findTeachplanTree(long courseId);

    /**
     * 保存课程计划
     *
     * @param teachplanDTO 课程计划
     */
    void saveTeachplan(SaveTeachplanDTO teachplanDTO);

    /**
     * 删除课程计划
     *
     * @param id 课程计划id
     */
    void deleteTeachplan(Long id);

    /**
     * 课程计划排序
     * @param up 是否向上
     * @param id 课程计划id
     */
    void sortTeachplan(boolean up, Long id);
}
