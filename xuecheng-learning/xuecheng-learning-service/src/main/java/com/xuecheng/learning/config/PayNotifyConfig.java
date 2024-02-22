package com.xuecheng.learning.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lin
 * @date 2024/2/22 19:08
 */
@Slf4j
@Configuration
public class PayNotifyConfig {

    /**
     * 交换机
     */
    public static final String PAYNOTIFY_EXCHANGE_FANOUT = "paynotify_exchange_fanout";
    /**
     * 支付结果通知消息类型
     */
    public static final String MESSAGE_TYPE = "payresult_notify";
    /**
     * 支付通知队列
     */
    public static final String PAYNOTIFY_QUEUE = "paynotify_queue";

    /**
     * 声明交换机，且持久化
     */
    @Bean(PAYNOTIFY_EXCHANGE_FANOUT)
    public FanoutExchange payNotifyExchangeFanout() {
        // 三个参数：交换机名称、是否持久化、当没有queue与其绑定时是否自动删除
        return new FanoutExchange(PAYNOTIFY_EXCHANGE_FANOUT, true, false);
    }

    /**
     * 支付通知队列,且持久化
     */
    @Bean(PAYNOTIFY_QUEUE)
    public Queue coursePublishQueue() {
        return QueueBuilder.durable(PAYNOTIFY_QUEUE).build();
    }

    /**
     * 交换机和支付通知队列绑定
     *
     * @param queue    队列
     * @param exchange 交换机
     */
    @Bean
    public Binding bindingCoursePublishQueue(@Qualifier(PAYNOTIFY_QUEUE) Queue queue, @Qualifier(PAYNOTIFY_EXCHANGE_FANOUT) FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

}
