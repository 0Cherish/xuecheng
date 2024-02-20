package com.xuecheng.search.service;

/**
 * @author Lin
 * @date 2024/2/20 16:16
 */
public interface IndexService {

    /**
     * 添加索引
     *
     * @param indexName 索引名称
     * @param id        主键
     * @param object    索引对象
     * @return 添加成功
     */
    Boolean addCourseIndex(String indexName, String id, Object object);


    /**
     * 更新索引
     *
     * @param indexName 索引名称
     * @param id        主键
     * @param object    索引对象
     * @return 更新成功
     */
    Boolean updateCourseIndex(String indexName, String id, Object object);

    /**
     * 删除索引
     *
     * @param indexName 索引名称
     * @param id        主键
     * @return 删除成功
     */
    Boolean deleteCourseIndex(String indexName, String id);

}
