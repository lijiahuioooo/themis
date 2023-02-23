package com.mfw.themis.dependent.util;

import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DateTimeUtil {

    public static final String YYYY_MM_DD_PATTERN = "yyyy-MM-dd";

    public static final String MM_DOT_DD_PATTERN = "MM.dd";

    public static final String YYYY_PATTERN = "yyyy";

    public static final String YYYYMMDD_PATTERN = "yyyyMMdd";

    public static final String YYYYMM_PATTERN = "yyyyMM";

    public static final String YYYY_YEAR_MM_MONTH_DD_DAY_PATTERN = "yyyy年MM月dd日";

    public static final String YYYY_MM_DD_HH_MM_SS_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD_HH_MM_SS_SSS_PATTERN = "yyyyMMddHHmmssSSS";

    public static final String SS_SSS_PATTERN = "ssSSS";

    public static final String HH_MM_SS_PATTERN = "HH:mm:ss";

    public static final String YYYY_MM_DD_HH_PATTERN = "yyyy-MM-dd_HH";

    public static final String HH_MM_PATTERN = "HH:mm";

    public static final String YYYY_MM_DD_T_HH_MM_SS_XXX_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";


    private static SimpleDateFormat createSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static Date currentDate() {
        return new Date();
    }

    public static String currentDate(String format) {
        Calendar calenda = Calendar.getInstance();
        SimpleDateFormat parser;
        if (StringUtils.isBlank(format)) {
            parser = createSimpleDateFormat(YYYY_MM_DD_PATTERN);
        } else {
            parser = createSimpleDateFormat(format);
        }
        return parser.format(calenda.getTime());
    }

    /**
     * 将日期转换为字符串
     */
    public static String date2Str(Date date, String pattern) {
        if (null == date)
            return "";
        SimpleDateFormat parser = createSimpleDateFormat(pattern);
        return parser.format(date);
    }

    /**
     * 字符串转化为日期
     */
    public static Date str2Date(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        SimpleDateFormat parser = createSimpleDateFormat(pattern);
        try {
            return parser.parse(dateStr);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static Date addDay(Date inDate, int day) {
        Calendar calendar = new java.util.GregorianCalendar();
        calendar.setTime(inDate);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    public static Date addHour(Date inDate, int hours) {
        Calendar calendar = new java.util.GregorianCalendar();
        calendar.setTime(inDate);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

    public static Date addMinute(Date inDate, int minutes) {
        Calendar calendar = new java.util.GregorianCalendar();
        calendar.setTime(inDate);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    /**
     * 得到当前日期前X天日期
     *
     * @param before
     * @param format
     * @return
     */
    public static String getDayBefore(int before, String format) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -before);
        SimpleDateFormat parser = createSimpleDateFormat(format);
        return parser.format(cal.getTime());
    }

    private static Date getDateBegin(int add) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, add);
        return cal.getTime();
    }

    private static Date getDateEnd(int add) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, add);
        return cal.getTime();
    }

    public static Date getTodayBegin() {
        return getDateBegin(-1);
    }

    public static Date getTodayEnd() {
        return getDateEnd(-1);
    }

    public static void main(String[] args) {
        System.out.println(getDateBegin(0));
        System.out.println(getDateEnd(0));
    }

    /**
     * 获得count天之前的0点时间
     *
     * @param date
     * @param count
     * @return
     */
    public static Date getDayStartOfBefore(Date date, int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -count);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获得count天之后的0点时间
     *
     * @param date
     * @param count
     * @return
     */
    public static Date getDayStartOfAfter(Date date, int count) {
        return getDayStartOfBefore(date, -count);
    }

    /**
     * 获得count天之前的23.59分的时间
     *
     * @param date
     * @param count
     * @return
     */
    public static Date getDayEndOfBefore(Date date, int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -count);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获得count天之后的23.59分的时间
     *
     * @param date
     * @param count
     * @return
     */
    public static Date getDayEndOfAfter(Date date, int count) {
        return getDayEndOfBefore(date, -count);
    }

    public static Date getBeginTime(Date beginDate) {
        Calendar calendar = Calendar.getInstance();
        if (beginDate != null) {
            calendar.setTime(beginDate);
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }
        return calendar.getTime();
    }

    public static Date getEndTime(Date endDate, int day) {
        Calendar calendar = Calendar.getInstance();
        if (endDate != null) {
            calendar.setTime(endDate);
        }
        calendar.add(Calendar.DAY_OF_YEAR, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 计算两个日期相差的天数，如果date2 > date1 返回正数，否则返回负数
     */
    public static long dayDiff(Date date1, Date date2) {
        return (date2.getTime() - date1.getTime()) / 86400000;
    }

    /**
     * 获取两个日期的小时差
     *
     * @param one
     * @param two
     * @return
     */
    public static int getDistanceTime(Date one, Date two) {
        long day = 0;
        int hour = 0;
        try {
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            hour = (int) (diff / (60 * 60 * 1000));
        } catch (Exception e) {
            log.error("getDistanceTime error", e);
        }
        return hour;
    }

    /**
     * whether startTime and endTime is in current month
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isInCurrentMonth(Date startTime,
                                           Date endTime) {
        final String currentYearMonth = DateTimeUtil.currentDate(DateTimeUtil.YYYYMM_PATTERN);
        final String startYearMonth = DateTimeUtil.date2Str(startTime, DateTimeUtil.YYYYMM_PATTERN);
        final String endYearMonth = DateTimeUtil.date2Str(endTime, DateTimeUtil.YYYYMM_PATTERN);
        return currentYearMonth.equals(startYearMonth) && currentYearMonth.equals(endYearMonth);
    }


}
