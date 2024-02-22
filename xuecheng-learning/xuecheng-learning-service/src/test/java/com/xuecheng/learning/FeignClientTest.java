package com.xuecheng.learning;

import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lin
 * @date 2024/2/22 15:52
 */
@SpringBootTest
public class FeignClientTest {

    @Autowired
    ContentServiceClient contentServiceClient;


    @Test
    public void testContentServiceClient() {
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(18L);
        Assertions.assertNotNull(coursepublish);
    }
}