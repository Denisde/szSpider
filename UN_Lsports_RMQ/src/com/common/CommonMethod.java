package com.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import jregex.Replacer;

import org.apache.log4j.Logger;

public class CommonMethod {
	public static Logger logger = Logger.getLogger(CommonMethod.class.getName());
	
	
	public static boolean bIfExistFile(String sFileName) {
		boolean bExits = false;
		File fFile = new File(sFileName);
		if (fFile.exists()) {
			bExits = true;
		}
		return bExits;
	}
	
	public String readFile(String sPathFileName) {

		String sHTML = "";
		sHTML = this.readFileGBK(sPathFileName);
		
		return sHTML;
	}

	public String readFileGBK(String fileName) {
		try {
			java.io.File objFile;
			java.io.FileInputStream objFileReader;
			objFile = new java.io.File(fileName);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100];
			int rc = 0;
			objFileReader = new FileInputStream(objFile);
			while ((rc = objFileReader.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] in_b = swapStream.toByteArray();
			return new String(in_b, "utf-8");
		} catch (Exception e) {
			logger.error("read file " + fileName + " find wrong\r\n"
					+ e.toString());
			return null;
		}

	}
	
	public static Matcher getMatcherStrGroup(String strContent, String strPattern) {
		Pattern pattern = Pattern.compile(strPattern, Pattern.DOTALL
				| Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(strContent);
		return matcher;
	}

	public static String getValueByPatter(String sIn, String strPattern) {
		String sOut = "";
		// String strPattern = ".*?pour(.*?)&euro;.*?";
		Matcher matcher = getMatcherStrGroup(sIn, strPattern);
		if (matcher.find()) {
			sOut = matcher.group(1).trim();
		}
		matcher=null;

		return sOut;
	}
	
	public boolean isNotEmpty(String s){
		boolean bVal = false;
		try {
			if (s!=null && !s.equals("") && !s.equals(" ") && s.length()>0){
				bVal = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return bVal;
	}
	
	public boolean isNumber(String sIn) {
		boolean bFlag = false;
		for (int i = 0; i < sIn.length(); i++) {
			if (sIn.charAt(i) >= '0' && sIn.charAt(i) <= '9')
				bFlag = true;
			else
				bFlag = false;
		}
		return bFlag;
	}
	
	public String getAddDay(int iDay) {
		String datetime = getCurrentTime();
		int year = Integer.parseInt(datetime.substring(0,4));
		int month = Integer.parseInt(datetime.substring(5,7));
		int date = Integer.parseInt(datetime.substring(8,10));
		int[] aMonth = { Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH,
				Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY,
				Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER,
				Calendar.NOVEMBER, Calendar.DECEMBER };
		
		GregorianCalendar oGC=new GregorianCalendar(year,aMonth[month - 1],date);
		oGC.add(Calendar.DATE,iDay);
		year=oGC.get(Calendar.YEAR);
		month=oGC.get(Calendar.MONTH)+1;
		date=oGC.get(Calendar.DATE);
		String sYear=year+"";
		String sMonth=month+"";
		String sDate=date+"";
		sMonth= sMonth.length()==1?"0"+sMonth:sMonth;
		sDate= sDate.length()==1?"0"+sDate:sDate;
		return sYear + sMonth + sDate;
	}
    
	public String getAddDay(int iDay,String datetime) {
		int year = Integer.parseInt(datetime.substring(0,4));
		int month = Integer.parseInt(datetime.substring(5,7));
		int date = Integer.parseInt(datetime.substring(8,10));
		int[] aMonth = { Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH,
				Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY,
				Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER,
				Calendar.NOVEMBER, Calendar.DECEMBER };
		
		GregorianCalendar oGC=new GregorianCalendar(year,aMonth[month - 1],date);
		oGC.add(Calendar.DATE,iDay);
		year=oGC.get(Calendar.YEAR);
		month=oGC.get(Calendar.MONTH)+1;
		date=oGC.get(Calendar.DATE);
		String sYear=year+"";
		String sMonth=month+"";
		String sDate=date+"";
		sMonth= sMonth.length()==1?"0"+sMonth:sMonth;
		sDate= sDate.length()==1?"0"+sDate:sDate;
		return sYear + sMonth + sDate;
	}
	
	public String getCurrentTime() {
		SimpleDateFormat oFormatter;
		String sTimeFormat = "yyyy-MM-dd HH:mm:ss";
		oFormatter = new SimpleDateFormat(sTimeFormat);
		return oFormatter.format(new Date());
	}
	
	public Vector getHistoryDate(String sFromDate,String sToDate) {
    	int fYear=Integer.parseInt(sFromDate.substring(0,4));
    	int fMonth=Integer.parseInt(sFromDate.substring(4,6));
    	int fDay=Integer.parseInt(sFromDate.substring(6,8));
		int tYear=Integer.parseInt(sToDate.substring(0,4));
		int tMonth=Integer.parseInt(sToDate.substring(4,6));
		int tDay=Integer.parseInt(sToDate.substring(6,8));
    	
		Vector<String> vHistroyDate = new Vector<String>();
		int Year;
		int DAY;
		int Month;
		String DateTime;
		int[] aMonth = { Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH,
				Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY,
				Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER,
				Calendar.NOVEMBER, Calendar.DECEMBER };

		if (fMonth <= 0 || fMonth > 12) {
			System.out.println("input date error and exit programe");
		}
		GregorianCalendar gcFrom = new GregorianCalendar(fYear,
				aMonth[fMonth - 1], fDay);
		GregorianCalendar gcTo = new GregorianCalendar(tYear,
				aMonth[tMonth - 1], tDay);
		
		int tyear = gcTo.get(Calendar.YEAR);
		int tmonth = gcTo.get(Calendar.MONTH) + 1;
		int tday = gcTo.get(Calendar.DAY_OF_MONTH);
		String sMonth=String.valueOf(tmonth);
		String sDate=String.valueOf(tday);
		
		sMonth= sMonth.length()==1?"0"+sMonth:sMonth;
		sDate= sDate.length()==1?"0"+sDate:sDate;
		
		String toDateTime = ""+tyear + sMonth  + sDate;
		do {
			Year = gcFrom.get(Calendar.YEAR);
			Month = gcFrom.get(Calendar.MONTH) + 1;
			DAY = gcFrom.get(Calendar.DAY_OF_MONTH);
			
			sMonth=String.valueOf(Month);
			sDate=String.valueOf(DAY);
			sMonth= sMonth.length()==1?"0"+sMonth:sMonth;
			sDate= sDate.length()==1?"0"+sDate:sDate;
			DateTime ="" + Year  + sMonth + sDate;
			vHistroyDate.add(DateTime);
			gcFrom.add(Calendar.DAY_OF_YEAR, 1);
		} while (!DateTime.equals(toDateTime));
		return vHistroyDate;
	}
    
	
	public Vector getTable(String sHTML) {
		Vector<String> vTable = new Vector<String>();
		try {
			if (sHTML.length() > 0) {
				String sPattern = "<table.*?>.*?</table>";
				Pattern pattern = Pattern.compile(sPattern,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher matcher = pattern.matcher(sHTML);
				while (matcher.find()) {
					vTable.add(matcher.group(0).trim());
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			//e.printStackTrace();
		}
		return vTable;
	}
	
	public Vector getTR(String sTable) {
		Vector<String> vTable = new Vector<String>();
		try {

			if (sTable.length() > 0) {
				String sPattern = "<tr.*?>.*?</tr>";
				Pattern pattern = Pattern.compile(sPattern,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher matcher = pattern.matcher(sTable);
				while (matcher.find()) {
					vTable.add(matcher.group(0).trim());
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return vTable;
	}

	public Vector getTD(String sTR) {
		Vector<String> vTable = new Vector<String>();
		try {

			if (sTR.length() > 0) {
				String sPattern = "<td.*?>(.*?)</td>";
				Pattern pattern = Pattern.compile(sPattern,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher matcher = pattern.matcher(sTR);
				while (matcher.find()) {
					vTable.add(matcher.group(1).trim());
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return vTable;
	}
	
	public String deleteTag(String sOld){
		String sNew = sOld;
		sNew = replaceSymbol(sOld, "<.*?>", "");
		return sNew;
	}

	private String replaceSymbol(String sIn, String sSyb, String sReplace) {
		return sIn.replaceAll(sSyb, sReplace).trim();
//		jregex.Pattern p = new jregex.Pattern(sSyb);
//		Replacer r = p.replacer(sReplace);
//		sIn = r.replace(sIn);
//		return sIn.trim();
	}
	
	public void sendEmail(String text){
		try {
			MailSend oMailSend=new MailSend();
			String sStartTime=String.valueOf(new Date());	
			oMailSend.sendMail("FrRace ",sStartTime,true,text);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
		
	public String  outStr(String content){
		String patter1 = "<textarea.*?>(.*?)</textarea>";
		String patter2="<div style=.*?>(.*?)</div>";
		Matcher matcher1 = getMatcherStrGroup(content,patter1);
		Matcher matcher2=getMatcherStrGroup(content,patter2);
		String str=null;
		if(matcher1.find()||matcher2.find()){
			str=matcher1.group(1).trim();
		}
		return str;
	}
	
	public static void main(String[] args) {

	}
}
