package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Lin
 * @date 2024/2/16 15:27
 */

public class MinioTest {

    MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.190.152:9000")
            .credentials("minioadmin", "minioadmin")
            .build();


    @Test
    public void testUpload() throws Exception {
        // 通过拓展名取出媒体资源类型
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        String mineType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mineType = extensionMatch.getMimeType();
        }

        // 参数信息
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket")
                .filename("E:\\profile\\java\\5.项目实战\\3.黑马程序员Java微服务项目《黑马头条》\\day12-项目部署 持续集成发布Jenkins+Git+Docker\\视频\\Day12-14-后端项目部署-综合测试(1).mp4")
                .object("1.mp4")
                .contentType(mineType)
                .build();

        // 上传文件
        minioClient.uploadObject(uploadObjectArgs);
    }

    @Test
    public void testDelete() throws Exception {
        // 参数信息
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("2.jpg")
                .build();

        // 删除文件
        minioClient.removeObject(removeObjectArgs);
    }

    @Test
    public void testGetFile() throws Exception {
        // 参数信息
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("1.mp4")
                .build();

        // 删除文件
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        FileOutputStream outputStream = new FileOutputStream("E:\\test\1.mp4");
        IOUtils.copy(inputStream, outputStream);

        // 校验文件完整性
        FileInputStream fileInputStream = new FileInputStream("E:\\profile\\java\\5.项目实战\\3.黑马程序员Java微服务项目《黑马头条》\\day12-项目部署 持续集成发布Jenkins+Git+Docker\\视频\\Day12-14-后端项目部署-综合测试(1).mp4");
        String sourceMd5 = DigestUtils.md5DigestAsHex(fileInputStream);
        String localMd5 = DigestUtils.md5DigestAsHex(Files.newInputStream(Paths.get("E:\\test\1.mp4")));
        if (sourceMd5.equals(localMd5)){
            System.out.println("下载成功");
        }
    }
}
