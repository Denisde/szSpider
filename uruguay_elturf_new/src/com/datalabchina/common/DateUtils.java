package com.datalabchina.common;
/**
 * @version 1.7
 */
import java.util.*;
import java.io.File;
import java.text.*;

public class DateUtils {

	
	public static Date getDate(String date, String pattern, Locale locale) {
		try {
			SimpleDateFormat formatter1 = new SimpleDateFormat(pattern,locale);
			ParsePosition pos = new ParsePosition(0);
			return formatter1.parse(date, pos);	
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 
	 * @param date like 'Saturday, 1 Mar 2008'
	 * @return Date
	 */
	public static Date getDate(String date) {
		return getDate(date, "EEE, d MMM yyyy", Locale.US);
	}
	
	/**
	 * @param field	Calendar.HOUR...
	 * @param amount
	 * @return
	 */
	public static Date add(Date date, int field, int amount){
		GregorianCalendar d = new GregorianCalendar();
		d.setTime(date);
		d.add(field, amount);
		return d.getTime();		
	}
	
	public static Integer getDate(Date date, int field) {
		GregorianCalendar d = new GregorianCalendar();
		d.setTime(date);
		return d.get(field);
	}
	/**
	 * 取年份 
	 */	
	public static Integer getYear() {
		return getYear(new Date());
	}	
	/**
	 * 取年份 
	 */	
	public static Integer getYear(Date date){
		GregorianCalendar d = new GregorianCalendar();
		d.setTime(date);
		return d.get(Calendar.YEAR);
	}	
	/**
	 * 取月份 1-12
	 */	
	public static Integer getMonth() {
		return getMonth(new Date());
	}
	/**
	 * 取月份 1-12
	 */	
	public static Integer getMonth(Date date) {
		GregorianCalendar d = new GregorianCalendar();
		d.setTime(date);
		return d.get(Calendar.MONTH) + 1;
	}
	/**
	 * 取月份 01-12
	 */	
	public static String getMonth0() {
		return getMonth0(new Date());
	}
	/**
	 * 取月份 01-12
	 */	
	public static String getMonth0(Date date) {
		GregorianCalendar d = new GregorianCalendar();
		d.setTime(date);
		String m = String.valueOf(d.get(Calendar.MONTH) + 1);
		if (m.length() == 1) m = "0" + m;
		return m;
	}		
	/**
	 * 取天数 1-31
	 */
	public static Integer getDay() {
		return getDay(new Date());
	}	
	/**
	 * 取天数 1-31
	 */
	public static Integer getDay(Date date) {
		GregorianCalendar d = new GregorianCalendar();
		d.setTime(date);
		return d.get(Calendar.DATE);
	}
	/**
	 * 取天数 01-31
	 */
	public static String getDay0() {
		return getDay0(new Date());
	}	
	/**
	 * 取天数 01-31
	 */
	public static String getDay0(Date date) {
		GregorianCalendar d = new GregorianCalendar();
		d.setTime(date);
		String day = String.valueOf(d.get(Calendar.DATE));
		if (day.length() == 1) day = "0" + day;		
		return day;
	}		
	/**
	 * 取小时 
	 */
	public static Integer getHour() {
		return getHour(new Date());
	}	
	/**
	 * 取小时 
	 */
	public static Integer getHour(Date date) {
		GregorianCalendar d = new GregorianCalendar();
		d.setTime(date);
		return d.get(Calendar.HOUR_OF_DAY);
	}	
	/**
	 * 取分钟 
	 */	
	public static Integer getMinute() {
		return getMinute(new Date());
	}	
	/**
	 * 取分钟 
	 */	
	public static Integer getMinute(Date date) {
		GregorianCalendar d = new GregorianCalendar();
		d.setTime(date);
		return d.get(Calendar.MINUTE);
	}	
	/**
	 * 取秒数 
	 */
	public static Integer getSecond() {
		return getSecond(new Date());
	}		
	/**
	 * 取秒数 
	 */
	public static Integer getSecond(Date date) {
		GregorianCalendar d = new GregorianCalendar();
		d.setTime(date);
		return d.get(Calendar.SECOND);
	}
	/**
	 * 取日期为路径：/2008/05/ 或 \2008\05\
	 */	
	public static String getPathYYYY_MM(Date date) {
		String YYYY = getYear(date).toString();
		String MM = getMonth(date).toString();
		if (MM.length() == 1) MM = "0" + MM;
		return File.separator + YYYY + File.separator + MM + File.separator;
	}
	/**
	 * 取当前日期为路径：/2008/05/ 或 \2008\05\
	 */
	public static String getPathYYYY_MM() {
		return getPathYYYY_MM(new Date());
	}
	/**
	 * 取日期为路径：/2008/05/08/ 或 \2008\05\08\
	 */	
	public static String getPathYYYY_MM_DD(Date date) {
		String YYYY = getYear(date).toString();
		String MM = getMonth0(date);
		String DD = getDay0();
		return File.separator + YYYY + File.separator + MM + File.separator + DD + File.separator;
	}
	/**
	 * 取当前日期为路径：/2008/05/08/ 或 \2008\05\08\
	 */
	public static String getPathYYYY_MM_DD() {
		return getPathYYYY_MM_DD(new Date());
	}	
	/**
	 * 获取当前星期（中国, 如：星期日,星期一,星期二）
	 */
	public static String getWeekCS() {
		Calendar c = GregorianCalendar.getInstance();
		c.setFirstDayOfWeek(Calendar.SUNDAY);
		String[] s = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五","星期六" };
		return s[c.get(Calendar.DAY_OF_WEEK) - 1];
	}
	/**
	 * 将英语单词转换为2位数的月份
	 * @param str Sep,sep,September ... 
	 * @return 09
	 */
	public static String getMonth(String str) {
		String MM = str.toLowerCase();
		if (MM.startsWith("jan")) { //January
			MM = "01";
		} else if (MM.startsWith("feb")) { //February
			MM = "02";
		} else if (MM.startsWith("mar")) {	//March
			MM = "03";
		} else if (MM.startsWith("apr")) {	//April
			MM = "04";
		} else if (MM.startsWith("may")) {	//May
			MM = "05";
		} else if (MM.startsWith("jun")) {		//June
			MM = "06";
		} else if (MM.startsWith("jul")) {	//July
			MM = "07";
		} else if (MM.startsWith("aug")) {	//August
			MM = "08";
		} else if (MM.startsWith("sep")) {	//September
			MM = "09";
		} else if (MM.startsWith("oct")) {	//October
			MM = "10";
		} else if (MM.startsWith("nov")) {	//November
			MM = "11";
		} else if (MM.startsWith("dec")) {	//December
			MM = "12";
		}
		if (MM.length() == 2) {
			return MM;
		} else {
			return null;
		}
	}	
	
	/**
	 * 获取当前日期（中国,yyyy年MM月dd日）
	 */
	public static String getDateCS() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取当前时间的长字符串形式 "yyyy-MM-dd HH:mm:ss"
	 */
	public static String getLongStr() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	
	public static String getLongStr1() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}	

	/**
	 * 获得d天后的现在时刻；长字符串形式 "yyyy-MM-dd HH:mm:ss"
	 * @param d d天后
	 */
	public static String getLongStrAfter(int d) {
		// Date currentTime = new Date();
		Calendar c = GregorianCalendar.getInstance();
		c.add(Calendar.DATE, d);// 获得d天后的现在时刻
		Date date = c.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		return dateString;
	}
	
	/**
	 * //获得d天前的现在时刻；长字符串形式 "yyyy-MM-dd HH:mm:ss"
	 * @param d d天后
	 */
	public static String getLongStrBefore(int d) {
		// Date currentTime = new Date();
		Calendar c = GregorianCalendar.getInstance();
		c.add(Calendar.DATE, -d);// 获得d天后的现在时刻
		Date date = c.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		return dateString;
	}	
	/**
	 * 获取给定时间的长字符串形式 "yyyy-MM-dd HH:mm:ss"
	 */
	public static String getLongStr(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		return dateString;
	}
	public static String getMonthStr(String str) {
		if (str == null) return null;
		if (str.indexOf("des") >= 0) return str.replaceAll("des", "12");;		
		if (str.indexOf("nov") >= 0) return str.replaceAll("nov", "11");;
		if (str.indexOf("okt") >= 0) return str.replaceAll("okt", "10");		
		if (str.indexOf("sep") >= 0) return str.replaceAll("sep", "09");		
		if (str.indexOf("aug") >= 0) return str.replaceAll("aug", "08");
		if (str.indexOf("jul") >= 0) return str.replaceAll("jul", "07");
		if (str.indexOf("jun") >= 0) return str.replaceAll("jun", "06");
		if (str.indexOf("mai") >= 0) return str.replaceAll("mai", "05");		
		if (str.indexOf("apr") >= 0) return str.replaceAll("apr", "04");		
		if (str.indexOf("mar") >= 0) return str.replaceAll("mar", "03");
		if (str.indexOf("feb") >= 0) return str.replaceAll("feb", "02");
		if (str.indexOf("jan") >= 0) return str.replaceAll("jan", "01");
		return str;
	}
	/**
	 * 获取当前时间的短字符串形式 "yyyy-MM-dd"	必须大写MM(月份)
	 */
	public static String getShortStr() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	/**
	 * 获取当前日期(+/- d天)作为路径 "yyyy/MM/dd" or yyyy\MM\dd
	 */
	public static String getFilePath(int d) {
		Calendar c = GregorianCalendar.getInstance();
		c.add(Calendar.DATE, d);// 获得d天后的现在时刻
		Date date = c.getTime();
		SimpleDateFormat formatter = null;
		if (File.separator.equals("\\")) {
			formatter = new SimpleDateFormat("yyyy\\MM\\dd");
		} else if (File.separator.equals("/")) {
			formatter = new SimpleDateFormat("yyyy/MM/dd");
		} else {
			return null;
		}
		String dateString = formatter.format(date);
		return dateString;
	}
	
	/**
	 * 获取当前日期作为路径 "yyyy/MM/dd" or yyyy\MM\dd
	 */
	public static String getFilePath() {
		return getFilePath(0);
	}	
	/**
	 * 获取给定时间的短字符串形式 "dd.MM.yyyy"	必须大写MM(月份)
	 */
	public static String getShortStr(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		String dateString = formatter.format(date);
		return dateString;
	}
	
	public static String getShortStr1(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dateString = formatter.format(date);
		return dateString;
	}	

	/**
	 * 将字符串转换为一般时间的长格式:yyyy-MM-dd HH:mm:ss
	 */
	public static Date toLongDate(String strDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = formatter.parse(strDate, pos);
			return strtodate;
		} catch (Exception e) {
			return null;
		}		
	}

	
	/**
	 * 将字符串转换为一般时间的长格式:yyyyMMdd HH:mm:ss
	 */
	public static Date toLongDate1(String strDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = formatter.parse(strDate, pos);
			return strtodate;
		} catch (Exception e) {
			return null;
		}			
	}
	
	/**
	 * 将字符串转换为一般时间的长格式:08/10/2007 17:40:08
	 */
	public static Date toLongDate2(String strDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = formatter.parse(strDate, pos);
			return strtodate;
		} catch (Exception e) {
			return null;
		}
	}	

	/**
	 * 将字符串转换为一般时间的短格式;yyyy-MM-dd
	 */
	public static Date toShortDate(String strDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = formatter.parse(strDate, pos);
			return strtodate;
		} catch (Exception e) {
			return null;
		}			
	}

	/**
	 * 将字符串转换为一般时间的短格式;yyyyMMdd
	 */
	public static Date toShortDate1(String strDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = formatter.parse(strDate, pos);
			return strtodate;
		} catch (Exception e) {
			return null;
		}			
	}

	/**
	 * 将日期字符串加天数转换成新日期字符串 *
	 * @param strDate	原日期字符串:yyyy-MM-dd
	 * @param days	增加的天数
	 */
	public static String Adddate(String strDate, int days) {
		try {
			String[] date = strDate.split("-"); // 将要转换的日期字符串拆分成年月日
			int year, month, day;
			year = Integer.parseInt(date[0]);
			month = Integer.parseInt(date[1]) - 1;
			day = Integer.parseInt(date[2]);
	
			GregorianCalendar d = new GregorianCalendar(year, month, day);
			d.add(Calendar.DATE, days);
			Date dd = d.getTime();
			DateFormat df = DateFormat.getDateInstance();
			String adddate = df.format(dd);
			return adddate;
		} catch (Exception e) {
			return null;
		}		
	}

	/**
	 * 获取当前的时间
	 */
	public static Date getNow() {
		Date currentTime = new Date();
		return currentTime;
	}

	/**
	 * 用当前日期作为文件名,如：1199764545649。
	 * 取到的值是从1970年1月1日00:00:00开始算起所经过的微秒数。
	 */
	public static String getFileName1() {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		String filename = String.valueOf(calendar.getTimeInMillis());// 文件名
		return filename;
	}
	
	/**
	 * 用当前日期作为文件名: yyyy-MM-dd_HH-mm-ss
	 */
	public static String getFileName() {
		try {
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String dateString = formatter.format(currentTime);
			return dateString;
		} catch (Exception e) {
			return null;
		}		
	}	

	/**
	 * 用当前日期作为文件名,如：20080108115505264
	 */
	public static String getDateId() {
		Date currentTime = new Date();
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
		"yyyyMMddHHmmssSSS");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	public static void main(String agrs[]) {
		System.err.println(DateUtils.getDate("2001-May-01", "yyyy-MMM-dd", Locale.US));
	}
}
