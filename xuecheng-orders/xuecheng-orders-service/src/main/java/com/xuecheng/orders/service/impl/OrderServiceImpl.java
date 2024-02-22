package com.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.SystemException;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.base.utils.QRCodeUtil;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.orders.config.AlipayConfig;
import com.xuecheng.orders.config.PayNotifyConfig;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDTO;
import com.xuecheng.orders.model.dto.PayRecordDTO;
import com.xuecheng.orders.model.dto.PayStatusDTO;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Lin
 * @date 2024/2/22 17:52
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private XcOrdersMapper ordersMapper;

    @Autowired
    private XcOrdersGoodsMapper ordersGoodsMapper;

    @Autowired
    private XcPayRecordMapper payRecordMapper;

    @Autowired
    private OrderService currentProxy;

    @Autowired
    private MqMessageService mqMessageService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${pay.qrcodeurl}")
    private String qrcodeUrl;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PayRecordDTO createOrder(String userId, AddOrderDTO addOrderDTO) {
        // 创建商品订单
        XcOrders orders = saveXcOrders(userId, addOrderDTO);
        if (orders == null) {
            SystemException.cast("订单创建失败");
        }
        if ("600002".equals(orders.getStatus())) {
            SystemException.cast("订单已支付");
        }

        // 生成支付记录
        XcPayRecord payRecord = createPayRecord(orders);

        // 生成二维码
        String qrCode = null;
        try {
            String url = String.format(qrcodeUrl, payRecord.getPayNo());
            qrCode = new QRCodeUtil().createQRCode(url, 200, 200);
        } catch (IOException e) {
            SystemException.cast("生成二维码出错");
        }
        PayRecordDTO payRecordDTO = new PayRecordDTO();
        BeanUtils.copyProperties(payRecord, payRecordDTO);
        payRecordDTO.setQrcode(qrCode);

        return payRecordDTO;
    }

    @Override
    public XcPayRecord getPayRecordByPayNo(String payNo) {
        return payRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
    }

    @Override
    public PayRecordDTO queryPayResult(String payNo) {
        XcPayRecord payRecord = getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            SystemException.cast("请重新点击支付获取二维码");
        }
        //支付状态
        String status = payRecord.getStatus();
        //如果支付成功直接返回
        if ("601002".equals(status)) {
            PayRecordDTO payRecordDto = new PayRecordDTO();
            BeanUtils.copyProperties(payRecord, payRecordDto);
            return payRecordDto;
        }
        //从支付宝查询支付结果
        PayStatusDTO payStatusDto = queryPayResultFromAlipay(payNo);
        //保存支付结果
        currentProxy.saveAliPayStatus(payStatusDto);
        //重新查询支付记录
        payRecord = getPayRecordByPayNo(payNo);
        PayRecordDTO payRecordDto = new PayRecordDTO();
        BeanUtils.copyProperties(payRecord, payRecordDto);

        return payRecordDto;
    }

    /**
     * 请求支付宝查询支付结果
     *
     * @param payNo 支付交易号
     * @return 支付结果
     */
    public PayStatusDTO queryPayResultFromAlipay(String payNo) {
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE); //获得初始化的AlipayClient
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                SystemException.cast("请求支付查询查询失败");
            }
        } catch (AlipayApiException e) {
            log.error("请求支付宝查询支付结果异常:{}", e.getMessage(), e);
            SystemException.cast("请求支付查询查询失败");
        }

        //获取支付结果
        String resultJson = response.getBody();
        //转map
        Map resultMap = JSON.parseObject(resultJson, Map.class);
        Map alipayTradeQueryResponse = (Map) resultMap.get("alipay_trade_query_response");
        //支付结果
        String trade_status = (String) alipayTradeQueryResponse.get("trade_status");
        String total_amount = (String) alipayTradeQueryResponse.get("total_amount");
        String trade_no = (String) alipayTradeQueryResponse.get("trade_no");

        //保存支付结果
        PayStatusDTO payStatusDto = new PayStatusDTO();
        payStatusDto.setOut_trade_no(payNo);
        payStatusDto.setTrade_status(trade_status);
        payStatusDto.setApp_id(APP_ID);
        payStatusDto.setTrade_no(trade_no);
        payStatusDto.setTotal_amount(total_amount);
        return payStatusDto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAliPayStatus(PayStatusDTO payStatusDTO) {
        //支付流水号
        String payNo = payStatusDTO.getOut_trade_no();
        XcPayRecord payRecord = getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            SystemException.cast("支付记录找不到");
        }

        //支付结果
        String trade_status = payStatusDTO.getTrade_status();
        log.debug("收到支付结果:{},支付记录:{}}", payStatusDTO, payRecord);
        if ("TRADE_SUCCESS".equals(trade_status)) {

            //支付金额变为分
            Float totalPrice = payRecord.getTotalPrice() * 100;
            Float total_amount = Float.parseFloat(payStatusDTO.getTotal_amount()) * 100;
            //校验是否一致
            if (!payStatusDTO.getApp_id().equals(APP_ID) || totalPrice.intValue() != total_amount.intValue()) {
                //校验失败
                log.info("校验支付结果失败,支付记录:{},APP_ID:{},totalPrice:{}", payRecord.toString(), payStatusDTO.getApp_id(), total_amount.intValue());
                SystemException.cast("校验支付结果失败");
            }

            log.debug("更新支付结果,支付交易流水号:{},支付结果:{}", payNo, trade_status);
            XcPayRecord payRecord_u = new XcPayRecord();
            //支付成功
            payRecord_u.setStatus("601002");
            payRecord_u.setOutPayChannel("Alipay");
            //支付宝交易号
            payRecord_u.setOutPayNo(payStatusDTO.getTrade_no());
            //通知时间
            payRecord_u.setPaySuccessTime(LocalDateTime.now());
            int update1 = payRecordMapper.update(payRecord_u, new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
            if (update1 > 0) {
                log.info("更新支付记录状态成功:{}", payRecord_u);
            } else {
                log.info("更新支付记录状态失败:{}", payRecord_u);
                SystemException.cast("更新支付记录状态失败");
            }

            //关联的订单号
            Long orderId = payRecord.getOrderId();
            XcOrders orders = ordersMapper.selectById(orderId);
            if (orders == null) {
                log.info("根据支付记录[{}}]找不到订单", payRecord_u);
                SystemException.cast("根据支付记录找不到订单");
            }
            XcOrders order_u = new XcOrders();
            //支付成功
            order_u.setStatus("600002");
            int update = ordersMapper.update(order_u, new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getId, orderId));
            if (update > 0) {
                log.info("更新订单表状态成功,订单号:{}", orderId);
            } else {
                log.info("更新订单表状态失败,订单号:{}", orderId);
                SystemException.cast("更新订单表状态失败");
            }

        }
    }

    @Override
    public void notifyPayResult(MqMessage message) {
        //1、消息体，转json
        String msg = JSON.toJSONString(message);
        //设置消息持久化
        Message msgObj = MessageBuilder.withBody(msg.getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        // 2.全局唯一的消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(message.getId().toString());
        // 3.添加callback
        correlationData.getFuture().addCallback(
                result -> {
                    if(result.isAck()){
                        // 3.1.ack，消息成功
                        log.debug("通知支付结果消息发送成功, ID:{}", correlationData.getId());
                        //删除消息表中的记录
                        mqMessageService.completed(message.getId());
                    }else{
                        // 3.2.nack，消息失败
                        log.error("通知支付结果消息发送失败, ID:{}, 原因{}",correlationData.getId(), result.getReason());
                    }
                },
                ex -> log.error("消息发送异常, ID:{}, 原因{}",correlationData.getId(),ex.getMessage())
        );
        // 发送消息
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_EXCHANGE_FANOUT, "", msgObj,correlationData);

    }

    /**
     * 保存商品订单
     *
     * @param userId      用户id
     * @param addOrderDTO 商品信息
     * @return 订单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public XcOrders saveXcOrders(String userId, AddOrderDTO addOrderDTO) {
        XcOrders order = getOrderByBusinessId(addOrderDTO.getOutBusinessId());
        if (order != null) {
            return order;
        }

        order = new XcOrders();
        // 生成订单号
        long orderId = IdWorkerUtils.getInstance().nextId();
        order.setId(orderId);
        order.setTotalPrice(addOrderDTO.getTotalPrice());
        order.setCreateDate(LocalDateTime.now());
        //未支付
        order.setStatus("600001");
        order.setUserId(userId);
        order.setOrderType(addOrderDTO.getOrderType());
        order.setOrderName(addOrderDTO.getOrderName());
        order.setOrderDetail(addOrderDTO.getOrderDetail());
        order.setOrderDescrip(addOrderDTO.getOrderDescrip());
        //选课记录id
        order.setOutBusinessId(addOrderDTO.getOutBusinessId());
        ordersMapper.insert(order);

        String orderDetailJson = addOrderDTO.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoodsList = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        xcOrdersGoodsList.forEach(goods -> {
            XcOrdersGoods xcOrdersGoods = new XcOrdersGoods();
            BeanUtils.copyProperties(goods, xcOrdersGoods);
            xcOrdersGoods.setOrderId(orderId);//订单号
            ordersGoodsMapper.insert(xcOrdersGoods);
        });

        return order;

    }

    /**
     * 船舰支付交易记录
     *
     * @param orders 订单信息
     * @return 支付交易记录
     */
    public XcPayRecord createPayRecord(XcOrders orders) {
        if (orders == null) {
            SystemException.cast("订单不存在");
        }
        if ("600002".equals(orders.getStatus())) {
            SystemException.cast("订单已支付");
        }

        XcPayRecord payRecord = new XcPayRecord();
        //生成支付交易流水号
        long payNo = IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);
        //商品订单号
        payRecord.setOrderId(orders.getId());
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        //未支付
        payRecord.setStatus("601001");
        payRecord.setUserId(orders.getUserId());
        payRecordMapper.insert(payRecord);

        return payRecord;
    }

    /**
     * 根据业务id查询订单
     *
     * @param businessId 业务id
     * @return 订单
     */
    public XcOrders getOrderByBusinessId(String businessId) {
        return ordersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, businessId));
    }
}
