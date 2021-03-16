package com.xdtech.project.lot.util;

/**
 * Created by Administrator on 2020/3/9.
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CornUtil {

    private static final String TRANS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String TRANS_CRON_WORK_DAY = "ss mm HH ? * 2,3,4,5,6";//工作日（周1-周5）

    private static final String TRANS_CRON_FORMAT_DAY = "ss mm HH  * * ?";//每天


    /**
     * 生成每月或每周或每天执行的cron
     */
    public static String getCron(String period, String startDateStr) throws ParseException {
        String date = "2020-01-01 " + startDateStr;
        SimpleDateFormat format = new SimpleDateFormat(TRANS_DATE_FORMAT);
        Date startDate = format.parse(date);
        StringBuffer cronStringBuffer = new StringBuffer();
        if ("workday".equals(period)) {
            String startDateCron = fmtDateToStr(startDate, TRANS_CRON_WORK_DAY).trim();
            cronStringBuffer.append(startDateCron);
        }
        else if ("day".equals(period)) {
            String startDateCron = fmtDateToStr(startDate, TRANS_CRON_FORMAT_DAY).trim();
            cronStringBuffer.append(startDateCron);
        }
        return cronStringBuffer.toString();
    }

    /**
     * 格式转换 日期-字符串
     */
    public static String fmtDateToStr(Date date, String dtFormat) {
        if (date == null)
            return "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dtFormat);
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}