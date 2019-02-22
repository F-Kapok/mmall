package com.fans.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName CartVo
 * @Description:
 * @Author fan
 * @Date 2019-01-08 10:29
 * @Version 1.0
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CartVo {

    private List<CartProductVo> cartProductVoList;

    private boolean allChecked;

    private BigDecimal cartTotalPrice;

    private String imageHost;
}
