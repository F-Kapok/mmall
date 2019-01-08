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
}
