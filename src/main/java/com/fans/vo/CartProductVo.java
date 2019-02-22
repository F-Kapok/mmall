package com.fans.vo;

import lombok.*;

import java.math.BigDecimal;

/**
 * @ClassName CartProductVo
 * @Description:
 * @Author fan
 * @Date 2019-01-08 10:26
 * @Version 1.0
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CartProductVo {

    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    private BigDecimal productTotalPrice;
    private Integer productStock;
    private Integer productChecked;
    private String limitQuantity;
}
