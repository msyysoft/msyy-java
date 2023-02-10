package io.github.msyysoft.java.utiltools;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Title: 日期操作的工具类<br>
 * Description: 对日期对象或者字符串处理的工具类，开发人员优先使用该日期工具类完成日期相关操作。如该工具不能完成的需求，可以使用第三方commons-lang中的DateUtils<br>
 * Copyright (c) 2018 dyfc <br>
 */
public class DateUtil {
    public final static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String TIME_FORMAT = "HH:mm:ss";

    public static Calendar getNewCalendar(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal;
    }

    public static java.sql.Date utilDate2SqlDate(java.util.Date date) {
        if (date != null)
            return new java.sql.Date(date.getTime());
        return null;
    }

    public static java.sql.Timestamp utilDate2SqlTimestamp(java.util.Date date) {
        if (date != null)
            return new java.sql.Timestamp(date.getTime());
        return null;
    }

    public static java.util.Date sqlDate2UtilDate(java.sql.Date date) {
        if (date != null)
            return new java.util.Date(date.getTime());
        return null;
    }

    public static java.util.Date sqlTimestamp2UtilDate(java.sql.Timestamp date) {
        if (date != null)
            return new java.util.Date(date.getTime());
        return null;
    }

    public static Date getNowDate(String format) {
        if (StringUtils.isEmpty(format)) {
            return new Date();
        }
        try {
            return DateUtils.parseDate(DateFormatUtils.format(Calendar.getInstance().getTimeInMillis(), format), new String[]{format});
        } catch (Exception e) {
            return new Date();
        }
    }

    public static java.sql.Date getNowSqlDate() {
        return utilDate2SqlDate(getNowDate(DATE_FORMAT));
    }

    public static Timestamp getNowSqlTimestamp() {
        return utilDate2SqlTimestamp(getNowDate(null));
    }

    /**
     * 返回日期的星期几的中文字符串，例如返回“星期三”
     *
     * @param date，需要处理的日期
     * @return String 返回日期的星期几的中文字符串
     * @CreateTime: 2012-8-2
     * @ModifyTime: 2012-8-2
     */
    public static String getWeekdayChineseStrByDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("E").format(date);
    }

    /**
     * 比较两个时间戳是否相等，仅比较年月日时分秒
     *
     * @param o1，比较时间1
     * @param o2，比较时间2
     * @CreateTime: 2017-6-15
     * @ModifyTime: 2017-6-15
     */
    public static boolean equalsTimestamp(Timestamp o1, Timestamp o2) {
        if (o1 == null && o2 == null)
            return true;
        if (o1 != null && o2 != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (df.format(o1).equals(df.format(o2)))
                return true;
        }
        return false;
    }

    /**
     * 获取格式化后的时间串
     *
     * @param date     date
     * @param template the pattern, such as "yyyyMMddHHmmss"
     * @return formatTime
     */
    public static String getFormatTime(Date date, String template) {
        if (date == null || template == null)
            throw new NullPointerException("template must not null");
        return new SimpleDateFormat(template, Locale.getDefault())
                .format(date);
    }

    /**
     * 获取格式化后的时间串
     *
     * @param calendar calendar
     * @param template the pattern, such as "yyyyMMddHHmmss"
     * @return formatTime
     */
    public static String getFormatTime(Calendar calendar, String template) {
        if (calendar == null || template == null)
            throw new NullPointerException("template must not null");
        return new SimpleDateFormat(template, Locale.getDefault())
                .format(calendar.getTime());
    }

    /**
     */
    public static String getFormatTime(String template) {
        return getFormatTime(Calendar.getInstance(), template);
    }

    /**
     */
    public static String getFormatTime(String sourceStr, String template) {
        Calendar calendar = getCalendar(sourceStr);
        if (calendar != null) {
            return getFormatTime(calendar, template);
        }
        return sourceStr;
    }

    /**
     * 获取Calendar
     *
     * @param sourceStr the sourceStr, such as "20151024102424",the length must is 14
     * @return calendar or null
     */
    public static Calendar getCalendar(String sourceStr) {
        if (sourceStr != null) {
            sourceStr = StringUtil.getNumberSTR(sourceStr);
            if (sourceStr.length() >= 12) {
                try {
                    int year = Integer.parseInt(sourceStr.substring(0, 4));
                    int month = Integer.parseInt(sourceStr.substring(4, 6)) - 1;
                    int day = Integer.parseInt(sourceStr.substring(6, 8));
                    int hourOfDay = Integer.parseInt(sourceStr.substring(8, 10));
                    int minute = Integer.parseInt(sourceStr.substring(10, 12));
                    int second = sourceStr.length() >= 14 ? Integer.parseInt(sourceStr.substring(12, 14)) : 0;
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day, hourOfDay, minute, second);
                    return calendar;
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    /**
     * 获取Calendar
     *
     * @param timeInMills
     * @return calendar
     */
    public static Calendar getCalendar(long timeInMills) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMills);
        return cal;
    }

    /**
     * 3天前的时间常用格式
     *
     * @param timeInMillis
     * @param template
     * @return
     */
    public static String getFormatTimeAgo(int days, long timeInMillis, String template) {
        Calendar nowCal = Calendar.getInstance();
        long nowTimeInMillis = nowCal.getTimeInMillis();
        long passTimeInMillis = nowTimeInMillis - timeInMillis;
        if (passTimeInMillis < 0)
            passTimeInMillis = 0;
        if (passTimeInMillis < 86400000l * days)//1天=86400000
        {
            if (passTimeInMillis < 3600000)//一小时
                return Math.max(passTimeInMillis / 60000l,1) + "分钟前";
            else if (passTimeInMillis < 86400000)//一天
                return passTimeInMillis / 3600000l + "小时前";
            else
                return passTimeInMillis / 86400000l + "天前";
        } else {
            nowCal.setTimeInMillis(timeInMillis);
            return getFormatTime(nowCal, template);
        }
    }

    /**
     * 获取Week
     *
     * @param calendar calendar
     * @return 1 ~ 7
     */
    public static int getWeekInt(Calendar calendar) {
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return calendar.get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7 : calendar
                .get(Calendar.DAY_OF_WEEK) - 1;
    }

    // -------------------------------------------------------------------------------------
    // value

    /**
     * 获取Week
     *
     * @param calendar calendar
     * @return 一, 二, 三, 四, 五, 六, 日
     */
    public static String getWeekString(Calendar calendar) {
        int now_weekI = getWeekInt(calendar);
        switch (now_weekI) {
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            default:
                return "日";
        }
    }
}
