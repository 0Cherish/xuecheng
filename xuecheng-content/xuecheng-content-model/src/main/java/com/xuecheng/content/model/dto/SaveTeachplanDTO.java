package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 保存课程计划
 *
 * @author Lin
 * @date 2024/2/15 14:17
 */
@Data
@ToString
public class SaveTeachplanDTO {
    /**
     * 教学计划id
     */
    private Long id;
    /**
     * 课程计划名称
     */
    private String pname;
    /**
     * 课程计划父级id
     */
    private Long parentid;
    /**
     * 层级：1、2、3
     */
    private Integer grade;
    /**
     * 课程类型：1视频、2文档
     */
    private String mediaType;
    /**
     * 课程标识
     */
    private Long courseId;
    /**
     * 课程发布标识
     */
    private Long coursePubId;
    /**
     * 是否支持试看
     */
    private String isPreview;
}
