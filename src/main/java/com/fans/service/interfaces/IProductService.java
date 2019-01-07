package com.fans.service.interfaces;

import com.fans.common.ServerResponse;
import com.fans.pojo.MmallProductWithBLOBs;
import com.fans.vo.ProductDetailVo;

/**
 * @InterfaceName IProductService
 * @Description: TODO 产品服务层接口
 * @Author fan
 * @Date 2018-12-17 14:46
 * @Version 1.0
 **/
public interface IProductService {

    ServerResponse saveOrUpdateProduct(MmallProductWithBLOBs product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> proDetail(Integer productId);

    ServerResponse getProductList(Integer pageNum, Integer pageSize);

    ServerResponse getProductByNameOrId(String productName, Integer productId, Integer pageNum, Integer pageSize);
}
