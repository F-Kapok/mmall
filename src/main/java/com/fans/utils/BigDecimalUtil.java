package com.fans.utils;

import java.math.BigDecimal;

/**
 * @ClassName BigDecimalUtil
 * @Description:
 * @Author fan
 * @Date 2019-01-08 13:11
 * @Version 1.0
 **/
public class BigDecimalUtil {
    private BigDecimalUtil() {

    }

    public static BigDecimal add(Double v1, Double v2) {

        return new BigDecimal(v1.toString()).add(new BigDecimal(v2.toString()));
    }

    public static BigDecimal sub(Double v1, Double v2) {

        return new BigDecimal(v1.toString()).subtract(new BigDecimal(v2.toString()));
    }

    public static BigDecimal mul(Double v1, Double v2) {

        return new BigDecimal(v1.toString()).multiply(new BigDecimal(v2.toString()));
    }

    public static BigDecimal divide(Double v1, Double v2) {

        return new BigDecimal(v1.toString()).divide(new BigDecimal(v2.toString()), 2, BigDecimal.ROUND_HALF_UP);
    }
}
