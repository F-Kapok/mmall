package com.fans.service.impl;

import com.fans.common.ResponseCode;
import com.fans.common.ServerResponse;
import com.fans.dao.MmallCategoryMapper;
import com.fans.dao.MmallProductMapper;
import com.fans.pojo.MmallCategory;
import com.fans.pojo.MmallProductWithBLOBs;
import com.fans.service.interfaces.IProductService;
import com.fans.utils.DateUtils;
import com.fans.utils.PropertiesUtil;
import com.fans.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName ProductServiceImpl
 * @Description: TODO 产品服务实现层
 * @Author fan
 * @Date 2018-12-17 14:47
 * @Version 1.0
 **/
@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private MmallProductMapper productMapper;
    @Autowired
    private MmallCategoryMapper categoryMapper;

    @Override
    public ServerResponse saveOrUpdateProduct(MmallProductWithBLOBs product) {
        if (product != null) {
            //设置主图
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] imageArray = product.getSubImages().split(",");
                if (imageArray.length > 0) {
                    product.setMainImage(imageArray[0]);
                }
            }
            if (product.getId() != null) {
                int result = productMapper.updateByPrimaryKey(product);
                if (result > 0) {
                    return ServerResponse.successMsg("更新产品成功！！！");
                } else {
                    return ServerResponse.successMsg("更新产品失败！！！");
                }
            } else {
                int result = productMapper.insert(product);
                if (result > 0) {
                    return ServerResponse.successMsg("新增产品成功！！！");
                } else {
                    return ServerResponse.successMsg("新增产品失败！！！");
                }
            }

        }
        return ServerResponse.failureMsg("请检查入参！！！");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        MmallProductWithBLOBs product = new MmallProductWithBLOBs();
        product.setId(productId);
        product.setStatus(status);
        int result = productMapper.updateByPrimaryKeySelective(product);
        if (result > 0) {
            return ServerResponse.successMsg("修改产成品状态成功！！！");
        } else {
            return ServerResponse.successMsg("修改产成品状态失败！！！");
        }
    }

    @Override
    public ServerResponse<ProductDetailVo> proDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        MmallProductWithBLOBs product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.failureMsg("商品已下架或者不存在！！！");
        }
        ProductDetailVo detailVo = adapterProduct(product);
        return ServerResponse.success(detailVo);
    }


    private ProductDetailVo adapterProduct(MmallProductWithBLOBs product) {
        ProductDetailVo detailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product, detailVo);
        //imageHost
        String fileName = "ftp";
        detailVo.setImageHost(PropertiesUtil.loadProperties(fileName, "url", "http://192.168.242.128/img"));
        //parentCategoryId
        MmallCategory category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            detailVo.setParentCategoryId(0);
        } else {
            detailVo.setParentCategoryId(category.getParentId());
        }
        //createTime
        detailVo.setCreateTime(DateUtils.getYYYYMMddHHMMss(product.getCreateTime()));
        //updateTime
        detailVo.setUpdateTime(DateUtils.getYYYYMMddHHMMss(product.getUpdateTime()));
        return detailVo;
    }
}
