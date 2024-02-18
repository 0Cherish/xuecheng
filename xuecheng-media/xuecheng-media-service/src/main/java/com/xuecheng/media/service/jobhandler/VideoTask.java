package com.xuecheng.media.service.jobhandler;

import com.j256.simplemagic.ContentType;
import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * 视频处理任务类
 *
 * @author Lin
 * @date 2024/2/18 16:14
 */
@Slf4j
@Component
public class VideoTask {

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegPath;

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private MediaFileProcessService mediaFileProcessService;

    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        // cpu核心数
        int processors = Runtime.getRuntime().availableProcessors();

        // 查询待处理任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, processors);
        int size = mediaProcessList.size();
        if (size == 0) {
            return;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(size);
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            // 将任务加入线程池
            executorService.execute(() -> {
                try {
                    // 开启任务
                    Long taskId = mediaProcess.getId();
                    String fileId = mediaProcess.getFileId();
                    boolean b = mediaFileProcessService.startTask(taskId);
                    if (!b) {
                        log.debug("抢占任务失败，任务id：{}", taskId);
                        return;
                    }

                    // 视频转码
                    String bucket = mediaProcess.getBucket();
                    String filePath = mediaProcess.getFilePath();

                    File file = mediaFileService.downloadFileFromMinio(bucket, filePath);
                    if (file == null) {
                        log.debug("下载视频失败，任务id：{}，bucket：{}，objectName：{}", taskId, bucket, filePath);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载视频失败");
                        return;
                    }

                    String videoPath = file.getAbsolutePath();
                    String mp4Name = fileId + ".mp4";
                    File mp4File;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件失败：{}", e.getMessage());
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件失败");
                        return;
                    }
                    String mp4Path = mp4File.getAbsolutePath();

                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath, videoPath, mp4Name, mp4Path);
                    String result = videoUtil.generateMp4();
                    if (!"success".equals(result)) {
                        log.debug("视频转码失败，原因：{}，bucket：{}，objectName：{}", result, bucket, filePath);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, result);
                    }

                    // 上传到minio
                    String objectName = getFilePathByMd5(fileId, ".mp4");
                    String url = "/" + bucket + "/" + objectName;
                    boolean b1 = mediaFileService.addMediaFilesToMinio(mp4Path, ContentType.MP4A.getMimeType(), bucket, objectName);
                    if (!b1) {
                        log.debug("上传mp4到minio失败，taskId：{}，bucket：{}，objectName：{}", taskId, bucket, objectName);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "上传mp4到minio失败");
                    }

                    // 更新任务状态

                    mediaFileProcessService.saveProcessFinishStatus(taskId, "2", fileId, url, null);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        // 阻塞
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    /**
     * 获取转码后的文件路径
     *
     * @param fileMd5   文件md5
     * @param extension 文件拓展名
     */
    private String getFilePathByMd5(String fileMd5, String extension) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + extension;
    }
}
