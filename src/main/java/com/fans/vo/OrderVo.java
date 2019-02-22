package com.fans.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName OrderVo
 * @Description:
 * @Author fan
 * @Date 2019-01-09 16:48
 * @Version 1.0
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class OrderVo {
    private Long orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    private String paymentTypeDesc;

    private Integer postage;

    private Integer status;

    private String statusDesc;

    private String paymentTime;

    private String sendTime;

    private String endTime;

    private String closeTime;

    private String createTime;
    //订单的明细
    private List<OrderItemVo> orderItemVoList;

    private String imageHost;

    private Integer shippingId;

    private String receiverName;

    private ShippingVo shippingVo;
}
