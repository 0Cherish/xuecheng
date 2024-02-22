package com.xuecheng.orders.model.dto;

import com.xuecheng.orders.model.po.XcPayRecord;
import lombok.Data;
import lombok.ToString;

/**
 * 支付记录dto
 *
 * @author Lin
 * @date 2024/2/22 17:07
 */
@Data
@ToString
public class PayRecordDTO extends XcPayRecord {

    /**
     * 二维码
     */
    private String qrcode;

}
