package com.xuecheng.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.system.model.po.Dictionary;

import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author Lin
 * @date 2024/2/8 12:09
 */
public interface DictionaryService extends IService<Dictionary> {

    /**
     * 查询过所有数据字典
     *
     * @return 查询结果
     */
    List<Dictionary> queryAll();

    /**
     * 根据code查询数据字典
     *
     * @param code 数据字典code
     * @return 查询结果
     */
    Dictionary getByCode(String code);
}
