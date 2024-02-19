package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Lin
 * @date 2024/2/19 11:21
 */
@Data
@ApiModel(value = "BindTeachplanMediaDTO", description = "教学计划-媒资绑定提交数据")
public class BindTeachplanMediaDTO {

    @ApiModelProperty(value = "媒资文件id",required = true)
    private String mediaId;

    @ApiModelProperty(value = "媒资文件名称",required = true)
    private String fileName;

    @ApiModelProperty(value = "课程计划标识",required = true)
    private Long teachplanId;
}
