package com.fans.service.impl;

import com.fans.common.CommonConstants;
import com.fans.common.ResponseCode;
import com.fans.common.ServerResponse;
import com.fans.dao.MmallCategoryMapper;
import com.fans.dao.MmallProductMapper;
import com.fans.pojo.MmallCategory;
import com.fans.pojo.MmallProduct;
import com.fans.pojo.MmallProductWithBLOBs;
import com.fans.service.interfaces.ICateGoryService;
import com.fans.service.interfaces.IProductService;
import com.fans.utils.DateUtils;
import com.fans.utils.PropertiesUtil;
import com.fans.vo.ProductDetailVo;
import com.fans.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ProductServiceImpl
 * @Description: 产品服务实现层
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
    @Autowired
    private ICateGoryService iCateGoryService;

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

    @Override
    public ServerResponse getProductList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<MmallProduct> productList = productMapper.selectByExample(null);
        List<ProductListVo> productListVoList = productList.stream()
                .map(ProductListVo::adapt)
                .collect(Collectors.toList());
        PageInfo pageInfo = PageInfo.of(productListVoList);
        return ServerResponse.success(pageInfo);
    }

    @Override
    public ServerResponse getProductByNameOrId(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = "%" + productName + "%";
        }
        List<MmallProduct> productList = productMapper.searchByNameOrId(productName, productId);
        List<ProductListVo> result = productList.stream()
                .map(ProductListVo::adapt)
                .collect(Collectors.toList());
        PageInfo pageInfo = PageInfo.of(result);
        return ServerResponse.success(pageInfo);
    }

    @Override
    public ServerResponse getProductByKeyWordCategory(Integer pageNum, Integer pageSize, String orderBy, Integer categoryId, String keyword) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> cateGoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            MmallCategory category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //返回空结果集
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = PageInfo.of(productListVoList);
                return ServerResponse.success(pageInfo);
            }
            cateGoryIdList = iCateGoryService.getCateGoryAndChildById(categoryId).getData();
        }
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + keyword + "%";
        }
        PageHelper.startPage(pageNum, pageSize);
        //排序处理

        //1. 利用 pageHelper排列
        if (StringUtils.isNotBlank(orderBy)) {
            if (CommonConstants.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<MmallProduct> productList = productMapper.searchByKeyWordOrCateGoryIds(
                StringUtils.isBlank(keyword) ? null : keyword,
                cateGoryIdList.size() == 0 ? null : cateGoryIdList
        );
        //2. 利用集合排序排列
//        if (orderBy.indexOf(CommonConstants.OrderBy.ASC.getCode()) > 0) {
//            //升序
//            productList.sort(Comparator.comparing(MmallProduct::getPrice));
//        } else {
//            //降序
//            productList.sort((o1, o2) -> o2.getPrice().compareTo(o1.getPrice()));
//        }
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (MmallProduct product : productList) {
            ProductListVo productListVo = ProductListVo.adapt(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = PageInfo.of(productListVoList);
        return ServerResponse.success(pageInfo);
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
