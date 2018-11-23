package com.fans.common;

import lombok.Getter;

/**
 * @EnumName ResponseCode
 * @Description: TODO 响应代码
 * @Author fan
 * @Date 2018-11-23 11:29
 * @Version 1.0
 **/
@Getter
public enum ResponseCode {
    SUCCESS(0, "Response successfully"),
    ERROR(1, "Response error"),
    NEED_LOGIN(10, "Response need login"),
    ILLEGAL_ARGUMENT(2, "Response argument illegality");

    private final Integer code;
    private final String desc;

    ResponseCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
