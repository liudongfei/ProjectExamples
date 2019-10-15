package com.liu.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理工具类.
 * @Auther: liudongfei
 * @Date: 2019/9/5 21:54
 * @Description:
 */
public class DateUtil {

    // 日
    public static final int INTERVAL_DAY = 1;
    // 周
    public static final int INTERVAL_WEEK = 2;
    // 月
    public static final int INTERVAL_MONTH = 3;
    // 年
    public static final int INTERVAL_YEAR = 4;
    // 时
    public static final int INTERVAL_HOUR = 5;
    // 分
    public static final int INTERVAL_MINUTE = 6;
    // 秒
    public static final int INTERVAL_SECOND = 7;
    // date = 1901-01-01
    public static final Date tempDate = new Date(new Long("-2177481952000"));

    /**
     * 测试是否是当天.
     * @param date date
     * @return true-今天, false-不是
     */
    @SuppressWarnings("deprecation")
    public static boolean isToday(Date date) {
        Date now = new Date();
        boolean result = true;
        result &= date.getYear() == now.getYear();
        result &= date.getMonth() == now.getMonth();
        result &= date.getDate() == now.getDate();
        return result;
    }

    /**
     * 两个日期相减，取天数.
     * @param date1 date1
     * @param date2 date2
     * @return
     */
    public static long daysBetween(Date date1, Date date2) {
        if (date2 == null) {
            date2 = new Date();
        }
        long day = (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * 比较两个日期 if date1<=date2 return true.
     * @param date1 date1
     * @param date2 date2
     * @return
     */
    public static boolean compareDate(String date1, String date2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = format.parse(date1);
            Date d2 = format.parse(date2);
            return !d1.after(d2);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 字符型转换成日期型.
     * @param date date
     * @param dateFormat dateFormat
     * @return
     */
    public static Date dateFormat(String date, String dateFormat) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        if (date != null) {
            try {
                return format.parse(date);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 字符型转换成日期型，使用默认格式 yyyy-MM-dd HH:mm:ss.
     * @param date date
     * @return
     */
    public static Date dateFormat(String date) {
        return dateFormat(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 日期型转换成字符串.
     * @param date date
     * @param dateFormat dateFormat
     * @return
     */
    public static String dateFormat(Date date, String dateFormat) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        if (date != null) {
            return format.format(date);
        }
        return "";
    }

    /**
     * 日期型转换成字符串，使用默认格式 yyyy-MM-dd HH:mm:ss.
     * @param date date
     * @return
     */
    public static String dateFormat(Date date) {
        return dateFormat(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取昨天的日期.
     * @return
     */
    public static Date getYesterday() {
        Date date = new Date();
        long time = (date.getTime() / 1000) - 60 * 60 * 24;
        date.setTime(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(format.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }




    /**
     * 得到昨天的日期字符串，格式为yyyy-MM-dd.
     * @return
     */
    public static String getYesterdayStr() {
        Date date = new Date();
        long time = (date.getTime() / 1000) - 60 * 60 * 24;
        date.setTime(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(format.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormat(date, "yyyy-MM-dd");
    }

    /**
     * 得到传入日期字符串前一天的日期字符串，格式为yyyy-MM-dd.
     * @param specifiedDay specifiedDay，格式为yy-MM-dd
     * @return
     */
    public static String getSpecifiedDayBefore(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, -1);
        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }

    /**
     * 得到传入日期字符串前一天的日期字符串，格式为yyyy-MM-dd.
     * @param specifiedDay specifiedDay，格式为yyyy-MM-dd
     * @return
     */
    public static String getSpecifiedDayBefore2(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, -1);
        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }

    /**
     * 得到当前日期的前一月份的日期，格式为yyyy-MM.
     * @return
     */
    public static String getSpecifiedMouBefore() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);
        String dayBefore = new SimpleDateFormat("yyyy-MM").format(c.getTime());
        return dayBefore;
    }

    /**
     * 得到当前日期的前一周的日期，格式为yyyy-MM-dd.
     * @return
     */
    public static Date getWeekAgo() {
        Date date = new Date();
        long time = (date.getTime() / 1000) - 7 * 60 * 60 * 24;
        date.setTime(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(format.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 得到当前日期的几天前的日期，格式为yyyy-MM-dd.
     * @param interval interval
     * @return
     */
    public static String getDaysAgo(int interval) {
        Date date = new Date();
        long time = (date.getTime() / 1000) - interval * 60 * 60 * 24;
        date.setTime(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 得到当前日期的后一天的日期，格式为yyyy-MM-dd.
     * @return
     */
    public static Date getTomorrow() {
        Date date = new Date();
        long time = (date.getTime() / 1000) + 60 * 60 * 24;
        date.setTime(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(format.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 得到当前这一周的开始时间.
     * @return
     */
    public static Date getThisWeekStartTime() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.DAY_OF_WEEK, today.getFirstDayOfWeek());
        Calendar weekFirstDay = Calendar.getInstance();
        weekFirstDay.clear();
        weekFirstDay.set(Calendar.YEAR, today.get(Calendar.YEAR));
        weekFirstDay.set(Calendar.MONTH, today.get(Calendar.MONTH));
        weekFirstDay.set(Calendar.DATE, today.get(Calendar.DATE));
        return weekFirstDay.getTime();
    }

    /**
     * 得到指定年月的开始时间.
     * @param year year
     * @param month month
     * @return
     */
    public static Date getStartDay(int year, int month) {
        Calendar today = Calendar.getInstance();
        today.clear();
        today.set(Calendar.YEAR, year);
        today.set(Calendar.MONTH, month - 1);
        today.set(Calendar.DAY_OF_MONTH, 1);
        return today.getTime();
    }

    /**
     * 对给定的时间增加减少指定时间.
     * @param interval 单位，[INTERVAL_DAY,INTERVAL_WEEK,INTERVAL_MONTH,INTERVAL_YEAR,INTERVAL_HOUR,INTERVAL_MINUTE]
     * @param date date
     * @param n 数量，可以为负数
     * @return
     */
    public static Date dateAdd(int interval, Date date, int n) {
        long time = (date.getTime() / 1000); // 单位秒
        switch (interval) {
            case INTERVAL_DAY:
                time = time + n * 86400;// 60 * 60 * 24;
                break;
            case INTERVAL_WEEK:
                time = time + n * 604800;// 60 * 60 * 24 * 7;
                break;
            case INTERVAL_MONTH:
                time = time + n * 2678400;// 60 * 60 * 24 * 31;
                break;
            case INTERVAL_YEAR:
                time = time + n * 31536000;// 60 * 60 * 24 * 365;
                break;
            case INTERVAL_HOUR:
                time = time + n * 3600;// 60 * 60 ;
                break;
            case INTERVAL_MINUTE:
                time = time + n * 60;
                break;
            case INTERVAL_SECOND:
                time = time + n;
                break;
            default:
        }

        Date result = new Date();
        result.setTime(time * 1000);
        return result;
    }

    /**
     * 计算两个时间在指定单位上的相差间隔.
     * @param interval 单位，[INTERVAL_DAY,INTERVAL_WEEK,INTERVAL_MONTH,INTERVAL_YEAR,INTERVAL_HOUR,INTERVAL_MINUTE]
     * @param begin begin
     * @param end end
     * @return
     */
    public static int dateDiff(int interval, Date begin, Date end) {
        long beginTime = (begin.getTime() / 1000); // 单位：秒
        long endTime = (end.getTime() / 1000); // 单位: 秒
        long tmp = 0;
        if (endTime == beginTime) {
            return 0;
        }
        // 确定endTime 大于 beginTime 结束时间秒数 大于 开始时间秒数
        if (endTime < beginTime) {
            tmp = beginTime;
            beginTime = endTime;
            endTime = tmp;
        }

        long intervalTime = endTime - beginTime;
        long result = 0;
        switch (interval) {
            case INTERVAL_DAY:
                result = intervalTime / 86400;// 60 * 60 * 24;
                break;
            case INTERVAL_WEEK:
                result = intervalTime / 604800;// 60 * 60 * 24 * 7;
                break;
            case INTERVAL_MONTH:
                result = intervalTime / 2678400;// 60 * 60 * 24 * 31;
                break;
            case INTERVAL_YEAR:
                result = intervalTime / 31536000;// 60 * 60 * 24 * 365;
                break;
            case INTERVAL_HOUR:
                result = intervalTime / 3600;// 60 * 60 ;
                break;
            case INTERVAL_MINUTE:
                result = intervalTime / 60;
                break;
            case INTERVAL_SECOND:
                result = intervalTime / 1;
                break;
            default:
        }
        // 做过交换
        if (tmp > 0) {
            result = 0 - result;
        }
        return (int) result;
    }

    /**
     * 当前年份.
     * @return
     */
    public static int getTodayYear() {
        int yyyy = Integer.parseInt(dateFormat(new Date(), "yyyy"));
        return yyyy;
    }

    /**
     * 把给定日期转换成格式为rss格式兼容的字符串.
     * @param date date
     * @return
     */
    public static String dateFormatRss(Date date) {
        if (date != null) {
            return dateFormat(date, "E, d MMM yyyy H:mm:ss") + " GMT";
        }
        return "";
    }

    /**
     * 判断当前日期是否在两个日期之间.
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    public static boolean betweenStartDateAndEndDate(Date startDate, Date endDate) {
        boolean bool = false;
        Date curDate = new Date();
        if (curDate.after(startDate) && curDate.before(DateUtil.dateAdd(INTERVAL_DAY, endDate, 1))) {
            bool = true;
        }
        return bool;
    }

    /**
     * 判断当前时间是否在date之后.
     * @param date date
     * @return
     */
    public static boolean nowDateAfterDate(Date date) {
        boolean bool = false;
        Date curDate = new Date();
        if (curDate.after(date)) {
            bool = true;
        }
        return bool;
    }



    /**
     * 取得指定长度日期时间字符串{不含格式}.
     * @param format 时间格式由常量决定 8: 　YYMMDDHH 8位 10:　YYMMDDHHmm 10位 12:　YYMMDDHHmmss
     *            12位 14:　YYYYMMDDHHmmss 14位 15:　YYMMDDHHmmssxxx 15位 (最后的xxx
     *            是毫秒)
     */
    public static String getTime(int format) {
        StringBuffer cTime = new StringBuffer(10);
        Calendar time = Calendar.getInstance();
        int miltime = time.get(Calendar.MILLISECOND);
        int second = time.get(Calendar.SECOND);
        int minute = time.get(Calendar.MINUTE);
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int day = time.get(Calendar.DAY_OF_MONTH);
        int month = time.get(Calendar.MONTH) + 1;
        int year = time.get(Calendar.YEAR);
        if (format != 14) {
            if (year >= 2000) {
                year = year - 2000;
            } else {
                year = year - 1900;
            }
        }
        if (format >= 2) {
            if (format == 14) {
                cTime.append(year);
            } else {
                cTime.append(getFormatTime(year, 2));
            }
        }
        if (format >= 4) {
            cTime.append(getFormatTime(month, 2));
        }
        if (format >= 6) {
            cTime.append(getFormatTime(day, 2));
        }
        if (format >= 8) {
            cTime.append(getFormatTime(hour, 2));
        }
        if (format >= 10) {
            cTime.append(getFormatTime(minute, 2));
        }
        if (format >= 12) {
            cTime.append(getFormatTime(second, 2));
        }
        if (format >= 15) {
            cTime.append(getFormatTime(miltime, 3));
        }
        return cTime.toString();
    }

    /**
     *　产生任意位的字符串.
     * @param time 要转换格式的时间
     * @param format 转换的格式
     * @return
     */
    private static String getFormatTime(int time, int format) {
        StringBuffer numm = new StringBuffer();
        int length = String.valueOf(time).length();
        if (format < length) {
            return null;
        }
        for (int i = 0; i < format - length; i++) {
            numm.append("0");
        }
        numm.append(time);
        return numm.toString().trim();
    }

    /**
     * 根据生日取年龄.
     * @param birthday birthday
     * @return int
     */
    public static int getUserAge(Date birthday) {
        if (birthday == null) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthday)) {
            return 0;
        }
        int yearNow = cal.get(Calendar.YEAR);
        cal.setTime(birthday);// 给时间赋值
        int yearBirth = cal.get(Calendar.YEAR);
        return yearNow - yearBirth;
    }

    /**
     * 将int型时间(1970年至今的秒数)转换成Date型时间.
     * @param unixTime 1970年至今的秒数
     * @return
     */
    public static Date getDateByUnixTime(int unixTime) {
        return new Date(unixTime * 1000L);
    }

    /**
     * 将Date型时间转换成int型时间(1970年至今的秒数).
     * @param date date
     * @return
     */
    public static int getUnixTimeByDate(Date date) {
        return (int) (date.getTime() / 1000);
    }

    /**
     * 将长整型时间转换为格式为yyyy-MM-dd HH:mm:ss的string类型的时间.
     * @param dateLong 长整型时间
     * @return
     */
    public static String getLongDateToString(long dateLong) {
        Calendar logTsCalendar = Calendar.getInstance();
        logTsCalendar.setTimeInMillis(dateLong);
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(logTsCalendar.getTime());
        return dateString;
    }

    public static void main(String[] args) {

    }
}
