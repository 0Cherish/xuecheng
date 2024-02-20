package com.xuecheng.search.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 搜索课程参数dtl
 *
 * @author Lin
 * @date 2024/2/20 16:15
 */
@Data
@ToString
public class SearchCourseParamDTO {


    /**
     * 关键字
     */
    private String keywords;

    /**
     * 大分类
     */
    private String mt;

    /**
     * 小分类
     */
    private String st;

    /**
     * 难度等级
     */
    private String grade;


}
