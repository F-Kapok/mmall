package com.fans.service.interfaces;

import com.fans.common.ServerResponse;
import com.fans.vo.CartVo;

/**
 * @InterfaceName ICartService
 * @Description:
 * @Author fan
 * @Date 2019-01-08 10:39
 * @Version 1.0
 **/
public interface ICartService {

    ServerResponse addCart(Integer userId, Integer productId, Integer count);

    ServerResponse updateCart(Integer userId, Integer productId, Integer count);

    ServerResponse deleteCart(Integer userId, String productIds);

    ServerResponse selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ServerResponse getCartProductCount(Integer userId);

    ServerResponse list(Integer userId);
}
