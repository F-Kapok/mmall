package com.fans.service.interfaces;

import com.fans.common.ServerResponse;

import java.util.Map;

/**
 * @InterfaceName IOrderService
 * @Description: 订单服务接口
 * @Author fan
 * @Date 2019-01-09 11:48
 * @Version 1.0
 **/
public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse aliPayCallBack(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);
}
