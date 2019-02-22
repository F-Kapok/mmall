package com.fans.controller.portal;

import com.fans.common.CommonConstants;
import com.fans.common.RequestHolder;
import com.fans.common.ServerResponse;
import com.fans.pojo.MmallUser;
import com.fans.service.interfaces.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName CartController
 * @Description:
 * @Author fan
 * @Date 2019-01-08 10:33
 * @Version 1.0
 **/
@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    private ICartService iCartService;


    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    public ServerResponse list() {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iCartService.list(user.getId());
    }


    @RequestMapping(value = "/add.do", method = RequestMethod.GET)
    public ServerResponse addCart(Integer productId, Integer count) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iCartService.addCart(user.getId(), productId, count);
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ServerResponse updateCart(Integer productId, Integer count) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iCartService.updateCart(user.getId(), productId, count);
    }

    @RequestMapping(value = "/delete.do", method = RequestMethod.GET)
    public ServerResponse deleteCart(String productIds) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iCartService.deleteCart(user.getId(), productIds);
    }

    @RequestMapping(value = "/select.do", method = RequestMethod.GET)
    public ServerResponse select(Integer productId) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iCartService.selectOrUnSelect(user.getId(), productId, CommonConstants.Cart.CHECKED);
    }

    @RequestMapping(value = "/un_select.do", method = RequestMethod.GET)
    public ServerResponse unSelect(Integer productId) {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iCartService.selectOrUnSelect(user.getId(), productId, CommonConstants.Cart.UN_CHECKED);
    }

    @RequestMapping(value = "/select_all.do", method = RequestMethod.GET)
    public ServerResponse selectAll() {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iCartService.selectOrUnSelect(user.getId(), null, CommonConstants.Cart.CHECKED);
    }

    @RequestMapping(value = "/un_select_all.do", method = RequestMethod.GET)
    public ServerResponse unSelectAll() {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iCartService.selectOrUnSelect(user.getId(), null, CommonConstants.Cart.UN_CHECKED);
    }


    @RequestMapping(value = "/get_cart_product_count.do", method = RequestMethod.GET)
    public ServerResponse getCartProductCount() {
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        return iCartService.getCartProductCount(user.getId());
    }
}
