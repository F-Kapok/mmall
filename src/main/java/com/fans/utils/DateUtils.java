package com.fans.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName DateUtils
 * @Description: TODO 时间格式化工具
 * @Author fan
 * @Date 2018-09-10 13:45
 * @Version 1.0
 **/
public class DateUtils {
    private static SimpleDateFormat simpleDateFormat;

    /**
     * @Description: TODO YYYY_MM_DD格式
     * @Param: [date]
     * @return: java.lang.String
     * @Author: fan
     * @Date: 2018/09/10 15:40
     **/
    public static String getYYYY_MM_DD(Date date) {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = simpleDateFormat.format(date);
        return formatDate;
    }

    public static String getHH_MM_SS(Date date) {
        simpleDateFormat = new SimpleDateFormat("HH:MM:ss");
        String formatDate = simpleDateFormat.format(date);
        return formatDate;
    }

    public static String getYYYY_MM_DD_HH_MM_SS(Date date) {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        String formatDate = simpleDateFormat.format(date);
        return formatDate;
    }

    public static String getIncrDay(Integer day) {
        String formatDate = new DateTime().plusDays(3).toString("yyyy-MM-dd");
        return formatDate;
    }
}
