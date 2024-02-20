package com.xuecheng.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 课程预览数据模型
 *
 * @author Lin
 * @date 2024/2/19 14:11
 */
@Data
public class CoursePreviewDTO {
    /**
     * 课程基本信息，课程营销信息
     */
    CourseBaseInfoDTO courseBase;
    /**
     * 课程计划信息
     */
    List<TeachplanDTO> teachplans;

}
