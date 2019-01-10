package com.fans.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.fans.common.CommonConstants;
import com.fans.common.MmallCommon;
import com.fans.common.RequestHolder;
import com.fans.common.ServerResponse;
import com.fans.pojo.MmallUser;
import com.fans.service.interfaces.IOrderService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName OrderController
 * @Description: 订单控制层
 * @Author fan
 * @Date 2019-01-09 11:44
 * @Version 1.0
 **/
@RestController
@RequestMapping(value = "/order")
@Slf4j
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping(value = "/pay.do", method = RequestMethod.GET)
    public ServerResponse pay(Long orderNo, HttpServletRequest request) {
        ServerResponse result = MmallCommon.checkUser();
        String path = request.getSession().getServletContext().getRealPath("upload");
        if (result.isSuccess()) {
            MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
            return iOrderService.pay(orderNo, user.getId(), path);
        }
        return result;
    }

    @RequestMapping(value = "/alipay_callback.do", method = RequestMethod.POST)
    public Object aliPayCallBack(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();
        Map requestMap = request.getParameterMap();

        for (Iterator it = requestMap.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            String[] values = (String[]) requestMap.get(key);
            String value = "";
            for (int i = 0; i < values.length; i++) {
                value = (i == values.length - 1) ? value + values[i] : value + values[i] + ",";
            }
            params.put(key, value);
        }
        log.info("--> 支付宝回调: sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());

        params.remove("sign_type");
        try {
            boolean aliPayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), StandardCharsets.UTF_8.name(), Configs.getSignType());
            if (!aliPayRSACheckedV2) {
                return ServerResponse.failureMsg("非法请求！！！！");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常");
        }
        //todo 验证各种数据

        //验证通过后存储支付信息
        ServerResponse serverResponse = iOrderService.aliPayCallBack(params);
        if (serverResponse.isSuccess()) {
            return CommonConstants.AlipayCallback.RESPONSE_SUCCESS;
        }
        return CommonConstants.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping(value = "/query_order_pay_status.do", method = RequestMethod.GET)
    public ServerResponse queryOrderPayStatus(Long orderNo) {
        ServerResponse result = MmallCommon.checkUser();
        if (result.isSuccess()) {
            MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
            result = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
            if (result.isSuccess()) {
                return ServerResponse.success(true);
            }
            return ServerResponse.success(false);
        }
        return result;
    }

    @RequestMapping(value = "/create.do", method = RequestMethod.GET)
    public ServerResponse createOrder(Integer shippingId) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    @RequestMapping(value = "/cancel.do", method = RequestMethod.GET)
    public ServerResponse cancelOrder(Long orderNo) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iOrderService.cancelOrder(user.getId(), orderNo);
    }

    @RequestMapping(value = "/get_order_cart_product.do", method = RequestMethod.GET)
    public ServerResponse getOrderCartProduct() {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping(value = "/detail.do", method = RequestMethod.GET)
    public ServerResponse orderDetail(Long orderNo) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    public ServerResponse orderList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }
}
