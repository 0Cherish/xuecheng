package com.xuecheng.messagesdk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.messagesdk.model.po.MqMessage;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Lin
 * @date 2024/2/20 13:10
 */
public interface MqMessageService extends IService<MqMessage> {

    /**
     * 扫描消息表记录
     *
     * @param shardIndex  分片序号
     * @param shardTotal  分片总数
     * @param messageType 消息类型
     * @param count       扫描记录数
     * @return 消息记录
     */
    List<MqMessage> getMessageList(int shardIndex, int shardTotal, String messageType, int count);

    /**
     * 添加消息
     *
     * @param messageType  消息类型
     * @param businessKey1 业务id
     * @param businessKey2 业务id
     * @param businessKey3 业务id
     * @return 消息内容
     */
    MqMessage addMessage(String messageType, String businessKey1, String businessKey2, String businessKey3);

    /**
     * 完成任务
     *
     * @param id 消息id
     * @return 更新成功
     */
    int completed(long id);

    /**
     * 完成阶段任务1
     *
     * @param id 消息id
     * @return 更新成功
     */
    int completedStageOne(long id);

    /**
     * 完成阶段任务2
     *
     * @param id 消息id
     * @return 更新成功
     */
    int completedStageTwo(long id);

    /**
     * 完成阶段任务3
     *
     * @param id 消息id
     * @return 更新成功
     */
    int completedStageThree(long id);

    /**
     * 完成阶段任务4
     *
     * @param id 消息id
     * @return 更新成功
     */
    int completedStageFour(long id);

    /**
     * 查询阶段状态1
     *
     * @param id 消息id
     * @return 状态
     */
    int getStageOne(long id);

    /**
     * 查询阶段状态2
     *
     * @param id 消息id
     * @return 状态
     */
    int getStageTwo(long id);

    /**
     * 查询阶段状态3
     *
     * @param id 消息id
     * @return 状态
     */
    int getStageThree(long id);

    /**
     * 查询阶段状态4
     *
     * @param id 消息id
     * @return 状态
     */
    int getStageFour(long id);

}
