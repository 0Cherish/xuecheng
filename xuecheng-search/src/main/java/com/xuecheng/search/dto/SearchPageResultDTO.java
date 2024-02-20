package com.xuecheng.search.dto;

import com.xuecheng.base.model.PageResult;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Lin
 * @date 2024/2/20 16:15
 */
@Data
@ToString
public class SearchPageResultDTO<T> extends PageResult<T> {

    /**
     * 大分类列表
     */
    List<String> mtList;
    /**
     * 小分类列表
     */
    List<String> stList;

    public SearchPageResultDTO(List<T> items, long counts, long page, long pageSize) {
        super(items, counts, page, pageSize);
    }

}
