package com.fans.vo;

import lombok.*;

/**
 * @ClassName ShippingVo
 * @Description:
 * @Author fan
 * @Date 2019-01-09 16:50
 * @Version 1.0
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class ShippingVo {
    private String receiverName;

    private String receiverPhone;

    private String receiverMobile;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;
}
