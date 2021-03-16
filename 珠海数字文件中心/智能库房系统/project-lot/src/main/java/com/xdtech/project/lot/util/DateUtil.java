package com.xdtech.project.lot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by RonJiang on 2017/12/27 0027.
 */
public class DateUtil {

	private final static Logger logger = LoggerFactory.getLogger(DateUtil.class);

	/**
	 * @param startdate
	 *            起始日期
	 * @param days
	 *            增加天数
	 * @return 起始日期增加days天后的日期
	 */
	public static String getExpirationDate(String startdate, int days) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			calendar.setTime(sdf.parse(startdate));
			calendar.add(Calendar.DAY_OF_YEAR, days);
		} catch (ParseException e) {
			logger.info(e.getMessage());
		}
		return sdf.format(calendar.getTime());
	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
}