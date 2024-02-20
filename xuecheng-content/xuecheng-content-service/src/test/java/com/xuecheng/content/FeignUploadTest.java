package com.xuecheng.content;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author Lin
 * @date 2024/2/20 14:55
 */
@SpringBootTest
public class FeignUploadTest {

    @Autowired
    private MediaServiceClient mediaServiceClient;

    @Test
    public void test() {

        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(
                new File("E:\\test\\1.html"));
        mediaServiceClient.upload(multipartFile, "course/test.html");
    }

}
