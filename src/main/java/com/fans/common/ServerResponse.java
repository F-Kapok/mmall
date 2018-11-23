package com.fans.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName ServerResponse
 * @Description: TODO 自定义交互数据定义类
 * @Author fan
 * @Date 2018-11-20 09:44
 * @Version 1.0
 **/


@Getter
//保证序列化json的时候,如果是null的对象,key也会消失
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse<T> implements Serializable {

    private Integer status;

    private String msg;

    private T data;

    private ServerResponse(Integer status) {
        this.status = status;
    }

    private ServerResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(Integer status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }


    @JsonIgnore
    //使之不在json序列化结果当中
    public boolean isSuccess() {
        return this.status.equals(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> success() {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> successMsg(String msg) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> success(T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> success(String msg, T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> failure() {
        return new ServerResponse<>(ResponseCode.ERROR.getCode());
    }

    public static <T> ServerResponse<T> failureMsg(String msg) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), msg);
    }

    public static <T> ServerResponse<T> failureCodeMessage(Integer errorCode, String errorMessage) {
        return new ServerResponse<>(errorCode, errorMessage);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("status", status);
        map.put("msg", msg);
        map.put("data", data);
        return map;
    }
}
