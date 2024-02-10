package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Lin
 * @date 2024/2/10 13:52
 */
@Data
@ApiModel(value = "EditCourseDTO", description = "修改课程基本信息")
public class EditCourseDTO extends AddCourseDTO {

    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
}
