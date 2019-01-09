package com.fans.pojo;

import com.fans.utils.serializer.Date2LongSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayInfo {
    /**
     * 支付信息表id
     */
    private Integer id;

    /**
     * 对应mmall_user表id
     */
    private Integer userId;

    /**
     * 订单号
     */
    private Long orderNo;

    /**
     * 支付平台 1-支付宝 2-微信
     */
    private Integer payPlatform;

    /**
     * 支付平台的支付流水号
     */
    private String platformNumber;

    /**
     * 支付平台的支付状态
     */
    private String palatformStatus;

    /**
     * 创建时间
     */
    @JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getPayPlatform() {
        return payPlatform;
    }

    public void setPayPlatform(Integer payPlatform) {
        this.payPlatform = payPlatform;
    }

    public String getPlatformNumber() {
        return platformNumber;
    }

    public void setPlatformNumber(String platformNumber) {
        this.platformNumber = platformNumber == null ? null : platformNumber.trim();
    }

    public String getPalatformStatus() {
        return palatformStatus;
    }

    public void setPalatformStatus(String palatformStatus) {
        this.palatformStatus = palatformStatus == null ? null : palatformStatus.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}