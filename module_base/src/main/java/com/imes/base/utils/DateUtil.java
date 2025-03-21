package com.imes.base.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * author : quintus
 * date : 2021/6/25 14:30
 * description :
 */
public class DateUtil {
    private static SimpleDateFormat defaultSdf = new SimpleDateFormat("yyyy年MM月dd日HH时",Locale.CHINA);

    /**
     * 格式化当前时间
     * @return
     */
    public static String formatNow(){
        return defaultSdf.format(new Date());
    }

    /**
     * 指定格式格式化当前时间
     * @param pattern
     * @return
     */
    public static String formatNow(String pattern){
        return new SimpleDateFormat(pattern, Locale.CHINA).format(new Date());
    }

    /**
     * 格式化指定时间
     * @param mills
     * @return
     */
    public static String formatDate(long mills){
        if (mills != 0){
            return defaultSdf.format(mills);
        }
        return "";
    }

    /**
     * 指定格式 格式化指定时间
     * @param mills
     * @param pattern
     * @return
     */
    public static String formatDate(long mills,String pattern){
        if (mills != 0L){
            return new SimpleDateFormat(pattern,Locale.CHINA).format(mills);
        }
        return "";
    }

    /**
     * 获取当前年份
     * @return
     */
    public static int getYear(){
        Calendar startCalendar = Calendar.getInstance();
        return startCalendar.get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     * @return
     */
    public static int getMonth(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 获取当前日期
     */
    public static int getDayOfMonth(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

}
