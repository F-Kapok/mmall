package com.fans.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.fans.common.CommonConstants;
import com.fans.common.ServerResponse;
import com.fans.dao.OrderItemMapper;
import com.fans.dao.OrderMapper;
import com.fans.dao.PayInfoMapper;
import com.fans.pojo.AliPay;
import com.fans.pojo.Order;
import com.fans.pojo.OrderItem;
import com.fans.pojo.PayInfo;
import com.fans.service.interfaces.IOrderService;
import com.fans.utils.BigDecimalUtil;
import com.fans.utils.DateUtils;
import com.fans.utils.FtpUtil;
import com.fans.utils.PropertiesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.awt.geom.Crossings;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName OrderServiceImpl
 * @Description:
 * @Author fan
 * @Date 2019-01-09 11:49
 * @Version 1.0
 **/
@Service(value = "iOrderService")
@Slf4j
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    /**
     * 支付宝当面付2.0服务
     */
    private static AlipayTradeService tradeService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("classpath:properties/zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Override
    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.failureMsg("用户无该订单");
        }
        resultMap.put("orderNo", orderNo.toString());

        AliPay aliPay = AliPay.builder()
                .outTradeNo(order.getOrderNo().toString())
                .subject(new StringBuilder().append("东升销售-扫码支付,订单号: ").append(order.getOrderNo().toString()).toString())
                .totalAmount(order.getPayment().toString())
                .undiscountableAmount(BigInteger.ZERO.toString())
                .sellerId(StringUtils.EMPTY)
                .body(new StringBuilder().append("订单").append(order.getOrderNo().toString()).append("购买商品共").append(order.getPayment().toString()).append("元").toString())
                .operatorId(userId.toString())
                .storeId("kapok")
                .timeoutExpress("120m")
                .build();
        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = Lists.newArrayList();
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
        for (OrderItem item : orderItemList) {
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
            GoodsDetail goodsDetail = GoodsDetail.newInstance(item.getProductId().toString(),
                    item.getProductName(),
                    BigDecimalUtil.mul(item.getCurrentUnitPrice().doubleValue(), 100d).longValue(),
                    item.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goodsDetail);
        }
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(aliPay.getSubject()).setTotalAmount(aliPay.getTotalAmount()).setOutTradeNo(aliPay.getOutTradeNo())
                .setUndiscountableAmount(aliPay.getUndiscountableAmount()).setSellerId(aliPay.getSellerId()).setBody(aliPay.getBody())
                .setOperatorId(aliPay.getOperatorId()).setStoreId(aliPay.getStoreId()).setExtendParams(extendParams)
                .setTimeoutExpress(aliPay.getTimeoutExpress())
                //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setNotifyUrl(PropertiesUtil.loadProperties("config", "alipay.callback.url"))
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File file = new File(path);
                if (!file.exists()) {
                    file.setWritable(true);
                    file.mkdirs();
                }
                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(path, qrFileName);
                String qrUrl = "";
                try {
                    qrUrl = FtpUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    log.error("上传二维码异常", e);
                }
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.success(resultMap);
            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.failureMsg("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.failureMsg("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.failureMsg("不支持的交易状态，交易返回异常!!!");
        }
    }

    @Override
    public ServerResponse aliPayCallBack(Map<String, String> params) {
        Long orderNo = Long.valueOf(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.failureMsg("非本商城的订单,回调忽略");
        }
        if (order.getStatus() >= CommonConstants.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.success("支付宝重复调用");
        }
        if (CommonConstants.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateUtils.getYYYYMMddHHMMss(params.get("gmt_payment")));
            order.setStatus(CommonConstants.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = PayInfo.builder()
                .userId(order.getUserId())
                .orderNo(order.getOrderNo())
                .payPlatform(CommonConstants.PayPlatformEnum.ALIPAY.getCode())
                .platformNumber(tradeNo)
                .palatformStatus(tradeStatus)
                .build();
        payInfoMapper.insert(payInfo);
        return ServerResponse.success("回调响应生效");
    }

    @Override
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.failureMsg("用户无该订单");
        }
        if (order.getStatus() >= CommonConstants.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.success();
        }
        return ServerResponse.failure();
    }

    /**
     * @Description: 简单打印应答
     * @Param: [response]
     * @return: void
     * @Author: fan
     * @Date: 2019/01/09 12:35
     **/
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (org.apache.commons.lang.StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
}
