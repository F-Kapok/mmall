package com.fans.utils;

import java.util.Map;

/**
 * @ClassName Test
 * @Description:
 * @Author fan
 * @Date 2018-12-17 17:35
 * @Version 1.0
 **/
public class Test {

    public static void main(String[] args) {
        Map<String, String> context = PropertiesUtil.loadProperties("ftp");
        System.out.println(context);
        assert context != null;
        System.out.println(context.size());
        String host = PropertiesUtil.loadProperties("ftp", "host");
        System.out.println(host);
        String aaa=PropertiesUtil.loadProperties("ftp","ass","qwe");
        System.out.println(aaa);
    }
}
