package com.fans.vo;

import lombok.*;

import java.math.BigDecimal;

/**
 * @ClassName OrderItemVo
 * @Description:
 * @Author fan
 * @Date 2019-01-09 16:49
 * @Version 1.0
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class OrderItemVo {

    private Long orderNo;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String createTime;
}
