package com.fans.utils;

import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @Description: TODO 读取properties配置文件工具
 * @Param:
 * @return:
 * @Author: fan
 * @Date: 2018/11/20 9:59
 **/
public class PropertiesUtil {
    public static Map<String, String> loadProperties(String fileName) {
        URL url = PropertiesUtil.class.getClassLoader().getResource("properties");
        String path = url.getPath() + fileName;
        Map<String, String> map = new LinkedHashMap<>();
        try {
            System.out.println(PropertiesUtil.class.getClassLoader().toString());
            InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
            if (is == null) {
                is = new FileInputStream(path);
            }
            Reader w = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(w);

            String line = br.readLine();

            while (line != null) {
                line = line.trim();
                if (!line.startsWith("#")) {
                    String name = line.substring(0, line.indexOf("="));
                    String value = line.substring(line.indexOf("=") + 1, line.length());

                    if ((StringUtils.hasLength(name)) && (StringUtils.hasLength(value))) {
                        map.put(name.trim(), value.trim());
                    }
                }
                line = br.readLine();
            }
            return map;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
