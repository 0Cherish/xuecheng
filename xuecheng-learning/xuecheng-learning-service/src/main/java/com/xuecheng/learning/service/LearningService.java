package com.xuecheng.learning.service;

import com.xuecheng.base.model.RestResponse;

/**
 * @author Lin
 * @date 2024/2/23 13:10
 */
public interface LearningService {

    /**
     * 获取教学视频
     *
     * @param userId      用户id
     * @param courseId    课程id
     * @param teachplanId 课程计划id
     * @param mediaId     视频id
     * @return 视频链接
     */
    RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}
