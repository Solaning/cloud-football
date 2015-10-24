package com.kinth.football.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

public class DateUtil {
	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		FastDateFormat formatter = FastDateFormat
				.getInstance("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取value秒后的时间
	 * 
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDateAfterSecond(int value) {
		Date currentTime = new Date();
		currentTime.setTime(System.currentTimeMillis() + value * 1000);
		FastDateFormat formatter = FastDateFormat
				.getInstance("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	
	/**
	 * 把毫秒转成yyyy-MM-dd HH:mm E格式的时间 有星期几
	 * @param value
	 * @return
	 */
	public static String parseTimeInMillis_hadweek(long value){
		FastDateFormat formatter = FastDateFormat
				.getInstance("yyyy-MM-dd HH:mm E");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(value);
		return formatter.format(calendar);
	}
	/**
	 * 把毫秒转成yyyy-MM-dd HH:mm:ss格式的时间
	 * @param value
	 * @return
	 */
	public static String parseTimeInMillis(long value){
		FastDateFormat formatter = FastDateFormat
				.getInstance("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(value);
		return formatter.format(calendar);
	}
	
	/**
	 * 把毫秒转成yyyy-MM-dd格式的时间
	 * @param value
	 * @return
	 */
	public static String parseTimeInMillis_only_nyr(long value){
		FastDateFormat formatter = FastDateFormat
				.getInstance("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(value);
		return formatter.format(calendar);
	}
	/**
	 * 把毫秒转成yyyy-MM-dd HH:mm格式的时间
	 * @param value
	 * @return
	 */
	public static String parseTimeInMillis_no_ss(long value){
		FastDateFormat formatter = FastDateFormat
				.getInstance("yyyy-MM-dd HH:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(value);
		return formatter.format(calendar);
	}
	
	/**
	 * 把时间转为Date
	 * @param value
	 * @return
	 */
	public static Date parseTimeToDate(long value){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(value);
		return calendar.getTime();
	}
	
	/**
	 * 把时间转成毫秒，否则返回目前的时间
	 * @return
	 */
	public static long parseTimeToMillis(String date){
//		FastDateFormat formatter = FastDateFormat
//		.getInstance("yyyy-MM-dd"); //HH:mm:ss TODO
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //yyyy-MM-dd HH:mm TODO
		Date now = null;
		try {
			now = formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			now = new Date();
		}
		return now.getTime();
	}
	
	/**
	 * 把时间转成毫秒，否则返回目前的时间
	 * @return
	 */
	public static long parseTimeToMillis2(String date){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm TODO
		Date now = null;
		try {
			now = formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			now = new Date();
		}
		return now.getTime();
	}
	
	
	/**
	 * 日期跟今天相比，> 0 ：在今天之后 < 0：在今天之前
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 * @throws ParseException
	 */
	public static int compareDateWithToday(String date) throws ParseException {
//		FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date specialDateTime = dateFormat.parse(date);//报错？？
		
		String dateStr = dateFormat.format(new Date());//重点
		Date mydate = dateFormat.parse(dateStr);
		
		int i = specialDateTime.compareTo(mydate);
		return i;
	}
	
	public static Date stringToDate(String dateString) {
		ParsePosition position = new ParsePosition(0);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		Date dateValue = simpleDateFormat.parse(dateString, position);
		return dateValue;
	}
	
    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     * 
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
    
    public static long getNowDate(){
    	Date now = new Date();
    	return now.getTime();
    }
    
	/**
	 * 通过生日计算年龄
	 */
	public static int calcAgeByBirthday(long birthday){
		int age = (int) ((new Date().getTime() - birthday) / (365L * 24 * 60 * 60 * 1000));
		return age;
	}
}