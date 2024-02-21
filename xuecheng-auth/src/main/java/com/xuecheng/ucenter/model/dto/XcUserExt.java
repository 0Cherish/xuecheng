package com.xuecheng.ucenter.model.dto;

import com.xuecheng.ucenter.model.po.XcUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户扩展消息
 *
 * @author Lin
 * @date 2024/2/21 12:59
 */
@Data
public class XcUserExt extends XcUser {
    /**
     * 用户权限
     */
    List<String> permissions = new ArrayList<>();
}
