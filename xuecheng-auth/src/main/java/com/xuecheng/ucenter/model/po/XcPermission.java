package com.xuecheng.ucenter.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Lin
 * @date 2024/2/21 12:59
 */
@Data
@TableName("xc_permission")
public class XcPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String roleId;

    private String menuId;

    private LocalDateTime createTime;


}
