package com.fans.controller.backend;

import com.fans.common.MmallCommon;
import com.fans.common.ServerResponse;
import com.fans.service.interfaces.ICateGoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @ClassName CateGoryManageController
 * @Description:  节点管理控制层
 * @Author fan
 * @Date 2018-12-10 10:06
 * @Version 1.0
 **/
@RestController
@RequestMapping("/manage/category")
public class CateGoryManageController {
    @Autowired
    private ICateGoryService iCateGoryService;

    @RequestMapping(value = "/add_category.do", method = RequestMethod.POST)
    public ServerResponse addCateGory(String cateGoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        ServerResponse result = MmallCommon.checkUser();
        if (result.isSuccess()) {
            //处理分类逻辑
            return iCateGoryService.addCateGory(cateGoryName, parentId);
        }
        return result;
    }

    @RequestMapping(value = "/set_category_name.do", method = RequestMethod.POST)
    public ServerResponse setCateGoryName(Integer categoryId, String categoryName) {
        ServerResponse result = MmallCommon.checkUser();
        if (result.isSuccess()) {
            //处理分类逻辑
            return iCateGoryService.setCateGoryName(categoryId, categoryName);
        }
        return result;
    }

    @RequestMapping(value = "/get_category.do", method = RequestMethod.POST)
    public ServerResponse getChildParallelCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        ServerResponse result = MmallCommon.checkUser();
        if (result.isSuccess()) {
            //处理分类逻辑
            return iCateGoryService.getChildParallelCateGory(categoryId);
        }
        return result;
    }

    @RequestMapping(value = "/get_deep_category.do", method = RequestMethod.POST)
    public ServerResponse getCareGoryAndChildById(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        ServerResponse result = MmallCommon.checkUser();
        if (result.isSuccess()) {
            //处理分类逻辑
            return iCateGoryService.getCateGoryAndChildById(categoryId);
        }
        return result;
    }

}
