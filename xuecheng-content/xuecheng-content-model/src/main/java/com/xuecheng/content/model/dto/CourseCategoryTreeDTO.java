package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Lin
 * @date 2024/2/8 13:28
 */
@Data
public class CourseCategoryTreeDTO extends CourseCategory implements Serializable {

    List<CourseCategoryTreeDTO> childrenTreeNodes;
}
