package com.xuecheng.media.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 媒资文件查询请求模型类
 *
 * @author Lin
 * @date 2024/2/16 17:36
 */
@Data
public class QueryMediaParamsDTO {
    @ApiModelProperty("媒资文件名称")
    private String filename;
    @ApiModelProperty("媒资类型")
    private String fileType;
    @ApiModelProperty("审核状态")
    private String auditStatus;
}
