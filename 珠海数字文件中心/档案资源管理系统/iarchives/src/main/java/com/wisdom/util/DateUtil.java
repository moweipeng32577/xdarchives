package com.wisdom.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by RonJiang on 2017/12/27 0027.
 */
public class DateUtil {


    private static String datePattern = "yyyy-MM-dd";

    private static String timePattern = datePattern + " HH:mm:ss";
    private static String[] altFormats = {"yyyyMMdd", "yyyyMM", "yyyy"};//标准化日期格式
    /**
     * @param startdate 起始日期
     * @param days      增加天数
     * @return 起始日期增加days天后的日期
     */
    public static String getExpirationDate(String startdate, int days) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            calendar.setTime(sdf.parse(startdate));
            calendar.add(Calendar.DAY_OF_YEAR, days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(calendar.getTime());
    }

    /**
     * 获取当前日期
     * @return
     */
    public static String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 获取当前日期
     * @return
     */
    public static String getCurrentTimeStr() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(date);
    }

    /**
     * @param startdate 起始日期
     * @param year      增加年度
     * @return 起始日期增加year后的日期
     */
    public static String getAddYearDate(String startdate, int year) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            calendar.setTime(sdf.parse(startdate));
            calendar.add(Calendar.YEAR, year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(calendar.getTime());
    }

    /**
     * This method returns the current date time in the format: MM/dd/yyyy HH:MM
     * a
     *
     * @param theTime
     *            the current time
     * @return the current date/time
     */
    public static String getTimeStandardStr(Date theTime) {

        return getDateTime(timePattern, theTime);
    }


    /**
     * This method generates a string representation of a date's date/time in
     * the format you specify on input
     *
     * @param aMask
     *            the date pattern the string is in
     * @param aDate
     *            a date object
     * @return a formatted string representation of the date
     *
     * @see java.text.SimpleDateFormat
     */
    public static final String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            System.out.println("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

}
