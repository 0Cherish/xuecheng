package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.SystemException;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.model.dto.CourseIndex;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author Lin
 * @date 2024/2/20 13:59
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    private CoursePublishService coursePublishService;

    @Autowired
    private CoursePublishMapper coursePublishMapper;

    @Autowired
    private SearchServiceClient searchServiceClient;

    /**
     * 任务调度入口
     */
    @XxlJob("CoursePublishJonHandler")
    public void coursePublishJobHandler() {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex：{}，shardTotal：{}", shardIndex, shardTotal);
        process(shardIndex, shardTotal, "course_publish", 30, 60);
    }

    /**
     * 课程发布任务处理
     *
     * @param mqMessage 执行任务内容
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        String businessKey1 = mqMessage.getBusinessKey1();
        int courseId = Integer.parseInt(businessKey1);
        // 课程静态化
        generateCourseHtml(mqMessage, courseId);

        // 课程索引
        saveCourseIndex(mqMessage, courseId);

        // 课程缓存
        saveCourseCache(mqMessage, courseId);
        return true;
    }

    /**
     * 保存课程索引信息
     *
     * @param mqMessage 消息
     * @param courseId  课程id
     */
    public void saveCourseCache(MqMessage mqMessage, int courseId) {
        log.debug("课程信息缓存至redis，课程id：{}", courseId);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将课程消息缓存至redis
     *
     * @param mqMessage 消息
     * @param courseId  课程id
     */
    public void saveCourseIndex(MqMessage mqMessage, long courseId) {
        log.debug("保存课程索引信息，课程id：{}", courseId);

        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageTwo = mqMessageService.getStageTwo(id);
        if (stageTwo > 0) {
            log.debug("课程索引已处理，课程id：{}", courseId);
            return;
        }
        Boolean result = saveCourseIndex(courseId);
        if (result) {
            mqMessageService.completedStageTwo(id);
        }
    }

    private Boolean saveCourseIndex(Long courseId) {
        // 取出课程发布信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        // 拷贝至课程索引对象
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);

        // 远程调用搜索服务添加课程学信息到缩影
        Boolean result = searchServiceClient.add(courseIndex);
        if (!result) {
            SystemException.cast("添加索引失败");
        }
        return true;
    }

    /**
     * 生成课程静态化页面并上传至文件系统
     *
     * @param mqMessage 消息
     * @param courseId  课程id
     */
    public void generateCourseHtml(MqMessage mqMessage, long courseId) {
        log.debug("开始课程静态化，课程id：{}", courseId);
        Long id = mqMessage.getId();

        MqMessageService mqMessageService = this.getMqMessageService();
        int stageOne = mqMessageService.getStageOne(id);
        if (stageOne > 0) {
            log.debug("课程静态化已处理，课程id：{}", courseId);
            return;
        }

        // 生成静态化页面
        File file = coursePublishService.generateCourseHtml(courseId);
        // 上传静态化页面
        if (file != null) {
            coursePublishService.uploadCourseHtml(courseId, file);
        }

        // 保存第一阶段状态
        mqMessageService.completedStageOne(id);
    }
}
