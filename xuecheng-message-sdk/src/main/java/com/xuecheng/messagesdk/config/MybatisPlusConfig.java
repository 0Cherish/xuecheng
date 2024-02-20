package com.xuecheng.messagesdk.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;


/**
 * @author Lin
 * @date 2024/2/20 13:02
 */
@Configuration("messagesdk_mpconfig")
@MapperScan("com.xuecheng.messagesdk.mapper")
public class MybatisPlusConfig {


}