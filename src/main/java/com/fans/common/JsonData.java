package com.fans.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName JsonData
 * @Description: TODO 自定义交互数据定义类
 * @Author fan
 * @Date 2018-11-20 09:44
 * @Version 1.0
 **/
@Getter
@Setter
public class JsonData {
    private Boolean ret;

    private String msg;

    private Object data;

    public JsonData(Boolean ret) {
        this.ret = ret;
    }

    public static JsonData success(String msg, Object data) {
        JsonData jsonData = new JsonData(true);
        jsonData.msg = msg;
        jsonData.data = data;
        return jsonData;
    }

    public static JsonData success(Object data) {
        JsonData jsonData = new JsonData(true);
        jsonData.data = data;
        return jsonData;
    }

    public static JsonData success(String msg) {
        JsonData jsonData = new JsonData(true);
        jsonData.msg = msg;
        return jsonData;
    }

    public static JsonData success() {

        return new JsonData(true);
    }

    public static JsonData fail(String msg) {
        JsonData jsonData = new JsonData(false);
        jsonData.msg = msg;
        return jsonData;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ret", ret);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }
}
