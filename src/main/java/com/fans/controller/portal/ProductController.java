package com.fans.controller.portal;

import com.fans.common.ServerResponse;
import com.fans.service.interfaces.IProductService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ProductController
 * @Description:
 * @Author fan
 * @Date 2019-01-07 16:06
 * @Version 1.0
 **/
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping(value = "/detail.do", method = RequestMethod.GET)
    public ServerResponse productDetail(Integer productId) {
        return iProductService.proDetail(productId);
    }

    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    public ServerResponse productList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                      @RequestParam(value = "orderBy", defaultValue = "") String orderBy,
                                      @RequestParam(value = "categoryId") Integer categoryId,
                                      @RequestParam(value = "keyword") String keyword) {

        return iProductService.getProductByKeyWordCategory(pageNum, pageSize, orderBy, categoryId, keyword);
    }
}
