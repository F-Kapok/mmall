package com.fans.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName OrderProductVo
 * @Description:
 * @Author fan
 * @Date 2019-01-10 10:08
 * @Version 1.0
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;

    private String imageHost;

    private BigDecimal productTotalPrice;
}
