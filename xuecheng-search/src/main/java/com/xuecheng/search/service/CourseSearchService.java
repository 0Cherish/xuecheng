package com.xuecheng.search.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.search.dto.SearchCourseParamDTO;
import com.xuecheng.search.dto.SearchPageResultDTO;
import com.xuecheng.search.po.CourseIndex;

/**
 * @author Lin
 * @date 2024/2/20 16:16
 */
public interface CourseSearchService {

    /**
     * 搜索课程列表
     *
     * @param pageParams           分页参数
     * @param searchCourseParamDTO 搜索条件
     * @return 搜索结果
     */
    SearchPageResultDTO<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDTO searchCourseParamDTO);

}
