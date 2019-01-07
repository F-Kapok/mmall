package com.fans.controller.backend;

import com.fans.common.MmallCommon;
import com.fans.common.ServerResponse;
import com.fans.pojo.MmallProductWithBLOBs;
import com.fans.service.interfaces.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ProductManageController
 * @Description:  产品管理后台控制层
 * @Author fan
 * @Date 2018-12-17 14:40
 * @Version 1.0
 **/
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public ServerResponse productSave(MmallProductWithBLOBs product) {
        ServerResponse result = MmallCommon.checkUser();
        if (result.isSuccess()) {
            return iProductService.saveOrUpdateProduct(product);
        }
        return result;
    }

    @RequestMapping(value = "/set_sale_status.do", method = RequestMethod.POST)
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        ServerResponse result = MmallCommon.checkUser();
        if (result.isSuccess()) {
            return iProductService.setSaleStatus(productId, status);
        }
        return result;
    }


    @RequestMapping(value = "/detail.do", method = RequestMethod.POST)
    public ServerResponse proDetail(Integer productId) {
        ServerResponse result = MmallCommon.checkUser();
        if (result.isSuccess()) {
            return iProductService.proDetail(productId);
        }
        return result;
    }

    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    public ServerResponse getProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        ServerResponse result = MmallCommon.checkUser();
        if (result.isSuccess()) {
            return iProductService.getProductList(pageNum, pageSize);
        }
        return result;
    }

    @RequestMapping(value = "/search.do", method = RequestMethod.GET)
    public ServerResponse searchProduct(@RequestParam(value = "productName") String productName,
                                        @RequestParam(value = "productId") Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        ServerResponse result = MmallCommon.checkUser();
        if (result.isSuccess()) {
            return iProductService.getProductByNameOrId(productName, productId, pageNum, pageSize);
        }
        return result;
    }

    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    public ServerResponse upload(MultipartFile file, HttpServletRequest request) {

        return ServerResponse.success();
    }
}
