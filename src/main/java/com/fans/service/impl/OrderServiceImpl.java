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
import com.fans.dao.*;
import com.fans.pojo.*;
import com.fans.service.interfaces.IOrderService;
import com.fans.utils.BigDecimalUtil;
import com.fans.utils.DateUtils;
import com.fans.utils.FtpUtil;
import com.fans.utils.PropertiesUtil;
import com.fans.vo.OrderItemVo;
import com.fans.vo.OrderVo;
import com.fans.vo.ShippingVo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.awt.geom.Crossings;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private MmallProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        //从购物车中获取数据
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        //计算这个订单的总价
        ServerResponse serverResponse = this.getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        //生成订单
        Order order = this.assembleOrder(userId, shippingId, payment);
        if (order == null) {
            return ServerResponse.failureMsg("生成订单错误！！！");
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.failureMsg("购物车为空！！！");
        }
        //生成详情
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        orderItemMapper.insertBatch(orderItemList);

        //生成成功,我们要减少产品的库存
        this.reduceProductStock(orderItemList);
        //清空一下购物车
        this.cleanCart(cartList);
        //返回给前端数据
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServerResponse.success(orderVo);
    }

    /**
     * @Description: 组件订单返回给前端
     * @Param: [order, orderItemList]
     * @return: com.fans.vo.OrderVo
     * @Author: fan
     * @Date: 2019/01/09 16:51
     **/
    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPamentType());
        orderVo.setPaymentTypeDesc(CommonConstants.PaymentTypeEnum.codeOf(order.getPamentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(CommonConstants.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }
        orderVo.setPaymentTime(DateUtils.getYYYYMMddHHMMss(order.getPaymentTime()));
        orderVo.setSendTime(DateUtils.getYYYYMMddHHMMss(order.getSendTime()));
        orderVo.setEndTime(DateUtils.getYYYYMMddHHMMss(order.getEndTime()));
        orderVo.setCreateTime(DateUtils.getYYYYMMddHHMMss(order.getCreateTime()));
        orderVo.setCloseTime(DateUtils.getYYYYMMddHHMMss(order.getCloseTime()));
        orderVo.setImageHost(PropertiesUtil.loadProperties("ftp", "url"));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    /**
     * @Description: 订单详情前端展示拼装
     * @Param: [orderItem]
     * @return: com.fans.vo.OrderItemVo
     * @Author: fan
     * @Date: 2019/01/09 17:02
     **/
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = OrderItemVo.builder()
                .orderNo(orderItem.getOrderNo())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .productImage(orderItem.getProductImage())
                .currentUnitPrice(orderItem.getCurrentUnitPrice())
                .quantity(orderItem.getQuantity())
                .totalPrice(orderItem.getTotalPrice())
                .createTime(DateUtils.getYYYYMMddHHMMss(orderItem.getCreateTime()))
                .build();
        return orderItemVo;
    }

    /**
     * @Description: 收货地址前端展示拼装
     * @Param: [shipping]
     * @return: com.fans.vo.ShippingVo
     * @Author: fan
     * @Date: 2019/01/09 16:58
     **/
    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = ShippingVo.builder()
                .receiverName(shipping.getReceiverName())
                .receiverAddress(shipping.getReceiverAddress())
                .receiverProvince(shipping.getReceiverProvince())
                .receiverCity(shipping.getReceiverCity())
                .receiverDistrict(shipping.getReceiverDistrict())
                .receiverMobile(shipping.getReceiverMobile())
                .receiverZip(shipping.getReceiverZip())
                .receiverPhone(shipping.getReceiverPhone())
                .build();
        return shippingVo;
    }

    /**
     * @Description: 清空购物车
     * @Param: [cartList]
     * @return: void
     * @Author: fan
     * @Date: 2019/01/09 16:47
     **/
    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }

    }

    /**
     * @Description: 减少产品的库存
     * @Param: [orderItemList]
     * @return: void
     * @Author: fan
     * @Date: 2019/01/09 16:47
     **/
    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            MmallProductWithBLOBs product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            product.setUpdateTime(new Date());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    /**
     * @Description: 拼装订单
     * @Param: [userId, shippingId, payment]
     * @return: com.fans.pojo.Order
     * @Author: fan
     * @Date: 2019/01/09 16:10
     **/
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        long orderNo = this.generateOrderNo();
        Order order = Order.builder()
                .userId(userId)
                .orderNo(orderNo)
                .status(CommonConstants.OrderStatusEnum.NO_PAY.getCode())
                .shippingId(shippingId)
                //免费包邮
                .postage(0)
                .pamentType(CommonConstants.PaymentTypeEnum.ONLINE_PAY.getCode())
                .payment(payment)
                //todo 发货时间等等
                //todo 付款时间等等
                .build();
        int rowCount = orderMapper.insert(order);
        if (rowCount > 0) {
            return order;
        }
        return null;
    }

    /**
     * @Description: 生成订单编号
     * @Param: []
     * @return: long
     * @Author: fan
     * @Date: 2019/01/09 16:13
     **/
    private long generateOrderNo() {
        long orderNo = System.currentTimeMillis();
        return orderNo + new Random().nextInt(100);
    }

    /**
     * @Description: 生成订单总价
     * @Param: [orderItemList]
     * @return: java.math.BigDecimal
     * @Author: fan
     * @Date: 2019/01/09 16:10
     **/
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal amount = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItemList) {
            amount = BigDecimalUtil.add(amount.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return amount;
    }

    /**
     * @Description: 通过购物车生成订单详情
     * @Param: [userId, cartList]
     * @return: com.fans.common.ServerResponse
     * @Author: fan
     * @Date: 2019/01/09 15:57
     **/
    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.failureMsg("购物车为空");
        }
        //校验购物车的数据,包括产品的状态和数量
        for (Cart cart : cartList) {
            MmallProductWithBLOBs product = productMapper.selectByPrimaryKey(cart.getProductId());
            //商品是否在售
            if (CommonConstants.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
                return ServerResponse.failureMsg("产品" + product.getName() + "不是在线售卖状态");
            }
            //商品库存检验
            if (product.getStatus() < cart.getQuantity()) {
                return ServerResponse.failureMsg("产品" + product.getName() + "库存不足");
            }
            OrderItem orderItem = OrderItem.builder()
                    .userId(userId)
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImage(product.getMainImage())
                    .currentUnitPrice(product.getPrice())
                    .quantity(cart.getQuantity())
                    .totalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()))
                    .build();
            orderItemList.add(orderItem);
        }
        return ServerResponse.success(orderItemList);
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
