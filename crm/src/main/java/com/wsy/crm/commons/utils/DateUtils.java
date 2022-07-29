package com.wsy.crm.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 对Date类型数据进行处理的工具类
 */
public class DateUtils {

    /**
     * 将Date类型日期转换为String类型yyyy-MM-dd HH:mm:ss格式的日期
     * @param date
     * @return
     */
    public static String formatDateTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        return str;
    }
    /**
     * 将Date类型日期转换为String类型yyyy-MM-dd格式的日期
     * @return
     */
    public static String formatDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(date);
        return str;
    }

}
