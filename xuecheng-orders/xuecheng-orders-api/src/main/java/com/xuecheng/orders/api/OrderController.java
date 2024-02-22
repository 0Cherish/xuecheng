package com.xuecheng.orders.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.xuecheng.base.exception.SystemException;
import com.xuecheng.orders.config.AlipayConfig;
import com.xuecheng.orders.model.dto.AddOrderDTO;
import com.xuecheng.orders.model.dto.PayRecordDTO;
import com.xuecheng.orders.model.dto.PayStatusDTO;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import com.xuecheng.orders.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lin
 * @date 2024/2/22 17:45
 */
@Api(value = "订单支付接口", tags = "订单支付接口")
@Slf4j
@Controller

public class OrderController {

    @Autowired
    private OrderService orderService;

    @Value("${pay.alipay.APP_ID}")
    private String APP_ID;

    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    private String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    private String ALIPAY_PUBLIC_KEY;

    @ApiOperation("生成支付二维码")
    @PostMapping("/generatepaycode")
    @ResponseBody
    public PayRecordDTO generatePayCode(@RequestBody AddOrderDTO addOrderDTO) {
        // 登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            SystemException.cast("请登录后继续支付");
        }
        return orderService.createOrder(user.getId(), addOrderDTO);
    }

    @ApiOperation("扫码下单接口")
    @GetMapping("/requestpay")
    public void requestPay(String payNo, HttpServletResponse httpServletResponse) throws IOException {
        XcPayRecord payRecord = orderService.getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            SystemException.cast("请重新点击支付获取二维码");
        }

        // 支付状态
        String status = payRecord.getStatus();
        if ("601002".equals(status)) {
            SystemException.cast("订单已支付，请勿重复支付");
        }

        // 构造sdk客户端对象
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);
        ;//创建API对应的request
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        alipayRequest.setNotifyUrl("http://tjxt-user-t.itheima.net/xuecheng/orders/paynotify");
        //填充业务参数
        alipayRequest.setBizContent("{" +
                " \"out_trade_no\":\"" + payRecord.getPayNo() + "\"," +
                " \"total_amount\":\"" + payRecord.getTotalPrice() + "\"," +
                " \"subject\":\"" + payRecord.getOrderName() + "\"," +
                " \"product_code\":\"QUICK_WAP_PAY\"" +
                " }");
        String form = "";
        try {
            //请求支付宝下单接口,发起http请求
            form = client.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpServletResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        //直接将完整的表单html输出到页面
        httpServletResponse.getWriter().write(form);
        httpServletResponse.getWriter().flush();
        httpServletResponse.getWriter().close();
    }

    @ApiOperation("查询支付结果")
    @GetMapping("/payresult")
    @ResponseBody
    public PayRecordDTO payResult(String payNo) {
        return orderService.queryPayResult(payNo);
    }

    @ApiOperation("接收支付结果通知")
    @PostMapping("/receivenotify")
    public void receivenotify(HttpServletRequest request, HttpServletResponse out) throws UnsupportedEncodingException, AlipayApiException {
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Object o : requestParams.keySet()) {
            String name = (String) o;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //验签
        boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");

        //验证成功
        if (verify_result) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //appid
            String app_id = new String(request.getParameter("app_id").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //total_amount
            String total_amount = new String(request.getParameter("total_amount").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            //交易成功处理
            if ("TRADE_SUCCESS".equals(trade_status)) {
                PayStatusDTO payStatusDto = new PayStatusDTO();
                payStatusDto.setOut_trade_no(out_trade_no);
                payStatusDto.setTrade_status(trade_status);
                payStatusDto.setApp_id(app_id);
                payStatusDto.setTrade_no(trade_no);
                payStatusDto.setTotal_amount(total_amount);

                orderService.saveAliPayStatus(payStatusDto);
            }
        }
    }
}

