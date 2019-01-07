package com.fans.vo;

import com.fans.pojo.MmallProduct;
import com.fans.utils.PropertiesUtil;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

/**
 * @ClassName ProductListVo
 * @Description:
 * @Author fan
 * @Date 2019-01-07 10:05
 * @Version 1.0
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductListVo {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private BigDecimal price;

    private Integer status;
    private String imageHost;

    public static ProductListVo adapt(MmallProduct product) {
        ProductListVo productListVo = ProductListVo.builder().build();
        BeanUtils.copyProperties(product, productListVo);
        productListVo.setImageHost(PropertiesUtil.loadProperties("ftp", "url", "http://192.168.242.128/img"));
        return productListVo;
    }
}
