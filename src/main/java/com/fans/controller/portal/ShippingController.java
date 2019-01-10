package com.fans.controller.portal;

import com.fans.common.RequestHolder;
import com.fans.common.ServerResponse;
import com.fans.pojo.MmallUser;
import com.fans.pojo.Shipping;
import com.fans.service.interfaces.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ShippingController
 * @Description: 收货地址控制层
 * @Author fan
 * @Date 2019-01-08 15:52
 * @Version 1.0
 **/
@RestController
@RequestMapping(value = "/shipping")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;

    @RequestMapping(value = "/add.do", method = RequestMethod.GET)
    public ServerResponse add(Shipping shipping) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iShippingService.add(user.getId(), shipping);
    }

    @RequestMapping(value = "/delete.do", method = RequestMethod.GET)
    public ServerResponse delete(Integer shippingId) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iShippingService.delete(user.getId(), shippingId);
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ServerResponse update(Shipping shipping) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iShippingService.update(user.getId(), shipping);
    }

    @RequestMapping(value = "/select.do", method = RequestMethod.GET)
    public ServerResponse select(Integer shippingId) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iShippingService.select(user.getId(), shippingId);
    }

    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    public ServerResponse list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iShippingService.list(user.getId(), pageNum, pageSize);
    }
}
