package com.mfw.themis.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * @author liuqi
 */
public class DateFormatUtils {

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

    /**
     * 日志格式化为rfc3339
     *
     * @param date
     * @return
     */
    public static String formatToRfc3339(Date date) {
        if (date == null) {
            return null;
        }
        DateTime dateTime = new DateTime(date, DateTimeZone.UTC);
        DateTimeFormatter dateFormatter = ISODateTimeFormat.dateTime();
        return dateFormatter.print(dateTime);
    }

    public static String formatToRfc3339China(Date date) {
        if (date == null) {
            return null;
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return df.format(date);
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

    private static SimpleDateFormat createSimpleDateFormat(String pattern) {

        return new SimpleDateFormat(pattern);
    }
}
