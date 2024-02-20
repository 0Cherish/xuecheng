package com.xuecheng.messagesdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.messagesdk.model.po.MqMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author Lin
 * @date 2024/2/20 13:02
 */
public interface MqMessageMapper extends BaseMapper<MqMessage> {

    /**
     * 扫描消息表记录
     *
     * @param shardIndex  分片序号
     * @param shardTotal  分片总数
     * @param messageType 消息类型
     * @param count       扫描记录数
     * @return 消息记录
     */
    @Select("SELECT t.* FROM mq_message t WHERE t.id % #{shardTotal} = #{shardIndex} and t.state='0' and t.message_type=#{messageType} limit #{count}")
    List<MqMessage> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("messageType") String messageType, @Param("count") int count);

}
