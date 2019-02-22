package com.fans.controller.backend;

import com.fans.common.ServerResponse;
import com.fans.pojo.MmallProductWithBLOBs;
import com.fans.service.interfaces.IFileService;
import com.fans.service.interfaces.IProductService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName ProductManageController
 * @Description: 产品管理后台控制层
 * @Author fan
 * @Date 2018-12-17 14:40
 * @Version 1.0
 **/
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public ServerResponse productSave(MmallProductWithBLOBs product) {
        return iProductService.saveOrUpdateProduct(product);
    }

    @RequestMapping(value = "/set_sale_status.do", method = RequestMethod.POST)
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        return iProductService.setSaleStatus(productId, status);
    }


    @RequestMapping(value = "/detail.do", method = RequestMethod.POST)
    public ServerResponse proDetail(Integer productId) {
        return iProductService.proDetail(productId);
    }

    @RequestMapping(value = "/list.do", method = RequestMethod.GET)
    public ServerResponse getProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return iProductService.getProductList(pageNum, pageSize);
    }

    @RequestMapping(value = "/search.do", method = RequestMethod.GET)
    public ServerResponse searchProduct(@RequestParam(value = "productName") String productName,
                                        @RequestParam(value = "productId") Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return iProductService.getProductByNameOrId(productName, productId, pageNum, pageSize);
    }

    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    public ServerResponse upload(@RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpServletRequest request) throws IOException {
        String path = request.getSession().getServletContext().getRealPath("upload");
        String remoteFilePath = iFileService.upload(file, path);
        if (StringUtils.isBlank(remoteFilePath)) {
            return ServerResponse.failureMsg("上传失败！！！");
        }
        Map<String, String> result = Maps.newHashMap();
        result.put("uri", remoteFilePath.substring(remoteFilePath.lastIndexOf("/") + 1));
        result.put("url", remoteFilePath);
        return ServerResponse.success(result);
    }

    @RequestMapping(value = "/richtext_img_upload.do", method = RequestMethod.POST)
    public Map<String, Object> richTextUpload(@RequestParam(value = "upload_file", required = false) MultipartFile file,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        String path = request.getSession().getServletContext().getRealPath("upload");
        String remoteFilePath = iFileService.upload(file, path);
        if (StringUtils.isBlank(remoteFilePath)) {
            resultMap.put("success", false);
            resultMap.put("msg", "上传失败！！！");
            return resultMap;
        }
        resultMap.put("success", true);
        resultMap.put("msg", "上传成功！！！");
        resultMap.put("file_path", remoteFilePath);
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return resultMap;
    }
}
