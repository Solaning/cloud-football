package com.kinth.football.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.TextUtils;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	
	public final static String FORMAT_YEAR = "yyyy";
	public final static String FORMAT_MONTH_DAY = "MM月dd日";
	
	public final static String FORMAT_DATE = "yyyy-MM-dd";
	public final static String FORMAT_TIME = "HH:mm";
	public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日  hh:mm";
	
	public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
	public final static String FORMAT_DATE1_TIME = "yyyy/MM/dd HH:mm";
	public final static String FORMAT_DATE_TIME_SECOND = "yyyy/MM/dd HH:mm:ss";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	private static final int YEAR = 365 * 24 * 60 * 60;// 年
	private static final int MONTH = 30 * 24 * 60 * 60;// 月
	private static final int DAY = 24 * 60 * 60;// 天
	private static final int HOUR = 60 * 60;// 小时
	private static final int MINUTE = 60;// 分钟
	
	// 静态化缓存  new SimpleDateFormat()需要花费太多时间
	private static SimpleDateFormat formatHourAndMin = new SimpleDateFormat("HH:mm");
	private static SimpleDateFormat formatTime = new SimpleDateFormat("yy-MM-dd HH:mm");
	private static SimpleDateFormat formatDay = new SimpleDateFormat("dd");

	/**
	 * 根据时间戳获取描述性时间，如3分钟前，1天前
	 * 
	 * @param timestamp
	 *            时间戳 单位为毫秒
	 * @return 时间字符串
	 */
	public static String getDescriptionTimeFromTimestamp(long timestamp) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
		String timeStr = null;
		if (timeGap > YEAR) {
			timeStr = timeGap / YEAR + "年前";
		} else if (timeGap > MONTH) {
			timeStr = timeGap / MONTH + "个月前";
		} else if (timeGap > DAY) {// 1天以上
			timeStr = timeGap / DAY + "天前";
		} else if (timeGap > HOUR) {// 1小时-24小时
			timeStr = timeGap / HOUR + "小时前";
		} else if (timeGap > MINUTE) {// 1分钟-59分钟
			timeStr = timeGap / MINUTE + "分钟前";
		} else {// 1秒钟-59秒钟
			timeStr = "刚刚";
		}
		return timeStr;
	}

	/**
	 * 获取当前日期的指定格式的字符串
	 * 
	 * @param format
	 *            指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
	 * @return
	 */
	public static String getCurrentTime(String format) {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_DATE_TIME);
		} else {
			sdf.applyPattern(format);
		}
		return sdf.format(new Date());
	}

	// date类型转换为String类型
 	// formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
 	// data Date类型的时间
 	public static String dateToString(Date data, String formatType) {
 		return new SimpleDateFormat(formatType).format(data);
 	}
 
 	// long类型转换为String类型
 	// currentTime要转换的long类型的时间
 	// formatType要转换的string类型的时间格式
 	public static String longToString(long currentTime, String formatType){
 		String strTime="";
		Date date = longToDate(currentTime, formatType);// long类型转成Date类型
		strTime = dateToString(date, formatType); // date类型转成String 
 		return strTime;
 	}
 
 	// string类型转换为date类型
 	// strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
 	// HH时mm分ss秒，
 	// strTime的时间格式必须要与formatType的时间格式相同
 	public static Date stringToDate(String strTime, String formatType){
 		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
 		Date date = null;
 		try {
			date = formatter.parse(strTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		return date;
 	}
 
 	// long转换为Date类型
 	// currentTime要转换的long类型的时间
 	// formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
 	public static Date longToDate(long currentTime, String formatType){
 		Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
 		String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
 		Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
 		return date;
 	}
 
 	// string类型转换为long类型
 	// strTime要转换的String类型的时间
 	// formatType时间格式
 	// strTime的时间格式和formatType的时间格式必须相同
 	public static long stringToLong(String strTime, String formatType){
 		Date date = stringToDate(strTime, formatType); // String类型转成date类型
 		if (date == null) {
 			return 0;
 		} else {
 			long currentTime = dateToLong(date); // date类型转成long类型
 			return currentTime;
 		}
 	}
 
 	// date类型转换为long类型
 	// date要转换的date类型的时间
 	public static long dateToLong(Date date) {
 		return date.getTime();
 	}
	 	
	public static String getTime(long time) {
		return formatTime.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		return formatHourAndMin.format(new Date(time));
	}

	/** 获取聊天时间：因为sdk的时间默认到秒故应该乘1000
	  * @Title: getChatTime
	  * @Description: TODO
	  * @param @param timesamp
	  * @param @return 
	  * @return String
	  * @throws
	  */
	public static String getChatTime(long timesamp) {
//		long clearTime = timesamp*1000;
		long clearTime = timesamp;
		String result = "";
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(clearTime);
		int temp = Integer.parseInt(formatDay.format(today))
				- Integer.parseInt(formatDay.format(otherDay));

		switch (temp) {
		case 0:
			result = "今天 " + getHourAndMin(clearTime);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(clearTime);
			break;
		case 2:
			result = "前天 " + getHourAndMin(clearTime);
			break;

		default:
			result = getTime(clearTime);
			break;
		}

		return result;
	}
	
	public static String TransTime(long inTime, boolean full){
		if(inTime == 0l){
			return "";
		}
		Date toDate = DateUtil.parseTimeToDate(inTime);
		return TransTime(toDate, full);
	}
	
    public static String TransTime(String inTime, boolean full) {
        if (TextUtils.isEmpty(inTime))
            return "";

        // 传入的目标时间
        Date toDate = DateUtil.strToDateLong(inTime);
        return TransTime(toDate, full);
    }

    @SuppressWarnings("deprecation")
	public static String TransTime(Date toDate, boolean full){
		String reDes = "";
		// 当前时间
        Date fromDate = new Date();
    		
        int toHour = toDate.getHours();
        int toMin = toDate.getMinutes();
        int toDay = toDate.getDate();

        int toWeekDay = toDate.getDay();
        int toMon = toDate.getMonth() + 1;
        int toYear = toDate.getYear() + 1900;

        int fromHour = fromDate.getHours();
        int fromDay = fromDate.getDate();
        int fromMon = fromDate.getMonth() + 1;
        int fromYear = fromDate.getYear() + 1900;

        if (toYear == fromYear && toMon == fromMon && toDay == fromDay) {
            if (toHour < 12 && fromHour > 12) {
                reDes = String.format("上午 %02d:%02d", toHour, toMin);
            } else {
                reDes = String.format("%02d:%02d", toHour, toMin);
            }
        } else {
            Date tmpToDate = new Date();
            tmpToDate.setYear(toYear);
            tmpToDate.setMonth(toMon);
            tmpToDate.setDate(toDay);

            Date tmpFromDate = new Date();
            tmpFromDate.setYear(fromYear);
            tmpFromDate.setMonth(fromMon);
            tmpFromDate.setDate(fromDay);

            long diffSeconds = (fromDate.getTime() - toDate.getTime()) / (1000);
            long daySeconds = 24 * 60 * 60;
            long WeekSeconds = 7 * daySeconds;

            if (diffSeconds <= daySeconds) {
                if (full) {
                    reDes = String.format("昨天 %02d:%02d", toHour, toMin);
                } else {
                    if (toHour < 12) {
                        reDes = "昨天上午";
                    } else {
                        reDes = String.format("昨天 %02d:%02d", toHour, toMin);
                    }
                }
            } else if (diffSeconds < WeekSeconds) {
                reDes = weekDayStringWithWeekDay(toWeekDay);
                if (full) {
                    reDes = String.format("%s %02d:%02d", reDes, toHour, toMin);
                }
            } else if (toMon == fromMon && toYear == fromYear) {
                if (full) {
                    reDes = String.format("%d月%d日 %02d:%02d", toMon, toDay, toHour, toMin);
                } else {
                    reDes = String.format("%d月%d日", toMon, toDay);
                }
            } else {

                SimpleDateFormat otherFormatter = null;// 设置日期格式
                if (full) {
                    otherFormatter = new SimpleDateFormat("yy-M-d H:mm");
                } else {
                    otherFormatter = new SimpleDateFormat("yy-M-d");
                }
                reDes = otherFormatter.format(toDate);
            }
        }
        return reDes;
    }
    
    private static String weekDayStringWithWeekDay(int weekDay) {
        String returnString = "";
        switch (weekDay) {
            case 1:
                returnString = "星期一";
                break;
            case 2:
                returnString = "星期二";
                break;
            case 3:
                returnString = "星期三";
                break;
            case 4:
                returnString = "星期四";
                break;
            case 5:
                returnString = "星期五";
                break;
            case 6:
                returnString = "星期六";
                break;
            case 7:
                returnString = "星期天";
                break;
            default:
                returnString = "";
                break;
        }
        return returnString;
    }
	
}