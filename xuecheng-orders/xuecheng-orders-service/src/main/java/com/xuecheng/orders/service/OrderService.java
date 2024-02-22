package com.xuecheng.orders.service;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.orders.model.dto.AddOrderDTO;
import com.xuecheng.orders.model.dto.PayRecordDTO;
import com.xuecheng.orders.model.dto.PayStatusDTO;
import com.xuecheng.orders.model.po.XcPayRecord;

/**
 * @author Lin
 * @date 2024/2/22 17:50
 */
public interface OrderService {

    /**
     * 创建商品订单
     *
     * @param userId      用户id
     * @param addOrderDTO 订单信息
     * @return 支付交易记录
     */
    PayRecordDTO createOrder(String userId, AddOrderDTO addOrderDTO);

    /**
     * 查询支付交易记录
     *
     * @param payNo 交易记录号
     * @return 支付记录
     */
    XcPayRecord getPayRecordByPayNo(String payNo);

    /**
     * 查询过支付结果
     *
     * @param payNo 交易记录号
     * @return 支付记录
     */
    PayRecordDTO queryPayResult(String payNo);

    /**
     * 保存支付宝支付结果
     *
     * @param payStatusDTO 支付结果信息
     */
    void saveAliPayStatus(PayStatusDTO payStatusDTO);

    /**
     * 发送通知结果
     *
     * @param message 消息
     */
    void notifyPayResult(MqMessage message);

}
