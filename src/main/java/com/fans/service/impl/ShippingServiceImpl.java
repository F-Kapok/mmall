package com.fans.service.impl;

import com.fans.common.ResponseCode;
import com.fans.common.ServerResponse;
import com.fans.dao.ShippingMapper;
import com.fans.pojo.Shipping;
import com.fans.service.interfaces.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ShippingServiceImpl
 * @Description: 收货地址服务实现层
 * @Author fan
 * @Date 2019-01-08 16:00
 * @Version 1.0
 **/
@Service(value = "iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        if (userId == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0) {
            Map<String, Object> result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.success("添加收货地址成功！！！", result);
        }
        return ServerResponse.failureMsg("添加收货地址失败！！！");
    }

    @Override
    public ServerResponse delete(Integer userId, Integer shippingId) {
        if (userId == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        int rowCount = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        if (rowCount > 0) {
            return ServerResponse.success("删除收货地址成功！！！");
        }
        return ServerResponse.failureMsg("删除收货地址失败！！！");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        if (userId == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        Shipping select = shippingMapper.selectByPrimaryKey(shipping.getId());
        if (select != null) {
            shipping.setCreateTime(select.getCreateTime());
        }
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByPkAndUserId(shipping);
        if (rowCount > 0) {
            return ServerResponse.success("更新收货地址成功！！！");
        }
        return ServerResponse.failureMsg("更新收货地址失败！！！");
    }

    @Override
    public ServerResponse select(Integer userId, Integer shippingId) {
        if (userId == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        Shipping shipping = shippingMapper.selectByPkAndUserId(userId, shippingId);
        if (shipping != null) {
            return ServerResponse.success("查询收货地址成功！！！", shipping);
        }
        return ServerResponse.failureMsg("无法查询此收货地址！！！");
    }

    @Override
    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize) {
        if (userId == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        if (shippingList.size() != 0) {
            PageInfo pageInfo = PageInfo.of(shippingList);
            return ServerResponse.success("查询收货地址清单成功！！！", pageInfo);
        }
        return ServerResponse.failureMsg("暂无收货地址！！！");
    }
}
