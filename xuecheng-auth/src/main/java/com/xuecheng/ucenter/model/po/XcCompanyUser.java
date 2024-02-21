package com.xuecheng.ucenter.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Lin
 * @date 2024/2/21 12:59
 */
@Data
@TableName("xc_company_user")
public class XcCompanyUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String companyId;

    private String userId;


}
