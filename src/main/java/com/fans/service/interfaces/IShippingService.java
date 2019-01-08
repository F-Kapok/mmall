package com.fans.service.interfaces;

import com.fans.common.ServerResponse;
import com.fans.pojo.Shipping;

/**
 * @InterfaceName IShippingService
 * @Description: 收货地址服务接口
 * @Author fan
 * @Date 2019-01-08 15:59
 * @Version 1.0
 **/
public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse delete(Integer userId, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse select(Integer userId, Integer shippingId);

    ServerResponse list(Integer userId, Integer pageNum, Integer pageSize);
}
