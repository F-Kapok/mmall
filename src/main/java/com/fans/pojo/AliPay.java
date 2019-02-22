package com.fans.pojo;

import lombok.*;

/**
 * @ClassName AliPay
 * @Description:
 * @Author fan
 * @Date 2019-01-09 12:00
 * @Version 1.0
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class AliPay {
    /**
     * (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
     * 需保证商户系统端不能重复，建议通过数据库sequence生成，
     */
    String outTradeNo;

    /**
     * (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
     */
    String subject;

    /**
     * (必填) 订单总金额，单位为元，不能超过1亿元
     * 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
     */
    String totalAmount;

    /**
     * (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
     * 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
     */
    String undiscountableAmount;

    /**
     * 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
     * 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
     */
    String sellerId;

    /**
     * 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
     */
    String body;

    /**
     * 商户操作员编号，添加此参数可以为商户操作员做销售统计
     */
    String operatorId;

    /**
     * (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
     */
    String storeId;

    /**
     * 支付超时，定义为120分钟
     */
    String timeoutExpress;
}
