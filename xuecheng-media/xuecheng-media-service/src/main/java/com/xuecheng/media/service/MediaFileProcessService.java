package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * @author Lin
 * @date 2024/2/18 15:38
 */
public interface MediaFileProcessService {

    /**
     * 获取待处理任务
     *
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count      获取记录数
     * @return 查询结果
     */
    List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * 开启一个任务
     *
     * @param id 任务id
     * @return 开启结果
     */
    boolean startTask(long id);

    /**
     * 保存任务结果
     *
     * @param taskId     任务id
     * @param status     任务状态
     * @param fileId     文件id
     * @param url        url
     * @param errMessage 错误信息
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errMessage);
}
