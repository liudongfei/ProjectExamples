package com.liu.common;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字类型的工具类.
 * @Auther: liudongfei
 * @Date: 2019/9/6 10:22
 * @Description:
 */
public class NumberUtil {

    /**
     * 判断输入的是否是数字.
     * @param str 输入的字符串
     * @return 数字结果
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否是数字，包括带小数点的数字.
     * @param str str
     * @return
     */
    public static boolean isNumberic(String str) {
        if (str == null || "".equals(str.trim())) {
            return false;
        }
        for (int i = str.length(); --i >= 0;) {
            int chr = str.charAt(i);
            if ((chr < 48 || chr > 57) && chr != 46) {
                return false;
            }
        }
        return true;
    }

    /**
     * 提供精确的小数位四舍五入处理.
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        System.out.println(NumberUtil.isNumberic("23.4"));
        System.out.println(NumberUtil.isNumeric("23"));
        System.out.println(NumberUtil.round(23.4, 0));

    }
}
