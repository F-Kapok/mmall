package com.fans.controller.backend;

import com.fans.common.ServerResponse;
import com.fans.service.interfaces.IOrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName OrderManageController
 * @Description: 订单管理控制层
 * @Author fan
 * @Date 2019-01-10 11:10
 * @Version 1.0
 **/
@RestController
@RequestMapping(value = "/manage/order")
public class OrderManageController {

    @Resource(name = "iOrderService")
    private IOrderService orderService;

    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    public ServerResponse orderList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        return ServerResponse.success(orderService.manageList(pageNum, pageSize));
    }

    @RequestMapping(value = "/detail.do", method = RequestMethod.GET)
    public ServerResponse orderDetail(Long orderNo) {

        return ServerResponse.success(orderService.manageDetail(orderNo));
    }

    @RequestMapping(value = "/search.do", method = RequestMethod.GET)
    public ServerResponse orderSearch(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                      Long orderNo) {

        return ServerResponse.success(orderService.manageSearch(orderNo, pageNum, pageSize));
    }


    @RequestMapping(value = "/send_goods.do", method = RequestMethod.GET)
    public ServerResponse orderSendGoods(Long orderNo) {

        return ServerResponse.success(orderService.manageOrderSendGoods(orderNo));
    }
}
