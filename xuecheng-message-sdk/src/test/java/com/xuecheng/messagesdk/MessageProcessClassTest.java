package com.xuecheng.messagesdk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * @author Lin
 * @date 2024/2/20 13:22
 */
@SpringBootTest
public class MessageProcessClassTest {

    @Autowired
    private MessageProcessClass messageProcessClass;

    @Test
    public void test() throws InterruptedException {
        System.out.println("开始执行：" + LocalDateTime.now());
        messageProcessClass.process(1, 2, "test", 5, 30);
        System.out.println("结束执行：" + LocalDateTime.now());
        Thread.sleep(9000000);
    }
}
