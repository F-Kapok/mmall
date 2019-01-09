package com.fans.common;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * @ClassName CommonConstants
 * @Description: 共用Key枚举
 * @Author fan
 * @Date 2018-11-23 11:23
 * @Version 1.0
 **/

public class CommonConstants {
    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface Role {
        //普通用户
        Integer ROLE_CUSTOMER = 0;
        //管理员
        Integer ROLE_ADMIN = 1;
    }

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    @Getter
    @AllArgsConstructor
    public enum OrderBy {
        /**
         * 降序
         */
        DESC("desc", "降序"),
        /**
         * 升序
         */
        ASC("asc", "升序"),
        ;
        private String code;
        private String value;

    }

    public enum ProductStatusEnum {
        /**
         * 商品在线状态码 1
         */
        ON_SALE(1, "在线");
        private String value;
        private int code;

        ProductStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public interface Cart {
        /**
         * 即购物车选中状态
         */
        int CHECKED = 1;
        /**
         * 购物车中未选中状态
         */
        int UN_CHECKED = 0;

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    @Getter
    @AllArgsConstructor
    public enum OrderStatusEnum {
        /**
         * 已取消
         */
        CANCELED(0, "已取消"),
        /**
         * 未支付
         */
        NO_PAY(10, "未支付"),
        /**
         * 已付款
         */
        PAID(20, "已付款"),
        /**
         * 已发货
         */
        SHIPPED(40, "已发货"),
        /**
         * 订单完成
         */
        ORDER_SUCCESS(50, "订单完成"),
        /**
         * 订单关闭
         */
        ORDER_CLOSE(60, "订单关闭");

        private int code;
        private String value;

    }

    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    @Getter
    @AllArgsConstructor
    public enum PayPlatformEnum {
        /**
         * 支付宝
         */
        ALIPAY(1, "支付宝");

        private int code;
        private String value;
    }

    @Getter
    @AllArgsConstructor
    public enum PaymentTypeEnum {
        /**
         * 在线支付
         */
        ONLINE_PAY(1, "在线支付");

        private int code;
        private String value;
    }
}
