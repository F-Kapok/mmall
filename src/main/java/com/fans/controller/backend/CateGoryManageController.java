package com.fans.controller.backend;

import com.fans.common.CommonConstants;
import com.fans.common.ResponseCode;
import com.fans.common.ServerResponse;
import com.fans.pojo.MmallUser;
import com.fans.service.interfaces.ICateGoryService;
import com.fans.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @ClassName CateGoryManageController
 * @Description: TODO 节点管理控制层
 * @Author fan
 * @Date 2018-12-10 10:06
 * @Version 1.0
 **/
@RestController
@RequestMapping("/manage/category")
public class CateGoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICateGoryService iCateGoryService;

    @RequestMapping(value = "/add_category.do", method = RequestMethod.POST)
    public ServerResponse addCateGory(HttpSession session, String cateGoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        MmallUser user = (MmallUser) session.getAttribute(CommonConstants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        } else {
            if (iUserService.checkAdminRole(user).isSuccess()) {
                //处理分类逻辑
                return iCateGoryService.addCateGory(cateGoryName, parentId);
            } else {
                return ServerResponse.failureMsg("无权限操作，需要管理员权限");
            }
        }
    }

    @RequestMapping(value = "/set_category_name.do", method = RequestMethod.POST)
    public ServerResponse setCateGoryName(HttpSession session, Integer categoryId, String categoryName) {
        MmallUser user = (MmallUser) session.getAttribute(CommonConstants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        } else {
            if (iUserService.checkAdminRole(user).isSuccess()) {
                //处理分类逻辑
                return iCateGoryService.setCateGoryName(categoryId, categoryName);
            } else {
                return ServerResponse.failureMsg("无权限操作，需要管理员权限");
            }
        }
    }

    @RequestMapping(value = "/get_category.do", method = RequestMethod.POST)
    public ServerResponse getChildParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        MmallUser user = (MmallUser) session.getAttribute(CommonConstants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        } else {
            if (iUserService.checkAdminRole(user).isSuccess()) {
                //处理分类逻辑
                return iCateGoryService.getChildParallelCateGory(categoryId);
            } else {
                return ServerResponse.failureMsg("无权限操作，需要管理员权限");
            }
        }
    }

    @RequestMapping(value = "/get_deep_category.do", method = RequestMethod.POST)
    public ServerResponse getCareGoryAndChildById(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        MmallUser user = (MmallUser) session.getAttribute(CommonConstants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        } else {
            if (iUserService.checkAdminRole(user).isSuccess()) {
                //处理分类逻辑
                return iCateGoryService.getCateGoryAndChildById(categoryId);
            } else {
                return ServerResponse.failureMsg("无权限操作，需要管理员权限");
            }
        }
    }
}
