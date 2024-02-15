package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 课程计划树形结构
 *
 * @author Lin
 * @date 2024/2/15 13:15
 */
@Data
@ToString
public class TeachplanDTO extends Teachplan {

    /**
     * 课程计划关联的媒资信息
     */
    TeachplanMedia teachplanMedia;

    /**
     * 子节点
     */
    List<TeachplanDTO> teachPlanTreeNodes;
}
