package com.datalabchina.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class CommonMethod {
	private static Logger logger = Logger.getLogger("CommonMethod");
	
	public static boolean isBeforeToday(String date){
		 boolean flag =false;
		 Date today1 = new Date();
		 SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd");
		 String today2 = d.format(today1);
		if(Integer.parseInt(date)<Integer.parseInt(today2))
			flag =true;
		 return flag;
	 }
	
	public boolean removeFile(String fileName,String pathName){
		boolean  flag = false;
		try {  
            File afile = new File(fileName); 
            if (afile.renameTo(new File(pathName+afile.getName()))) {  
            	logger.info("File is moved successful!");  
                flag = true;
            } else {  
            	logger.info("File is failed to move!");  
            }  
        } catch (Exception e) {  
        	logger.error("",e);
        }
        return flag;
	}
	
	 public static boolean createDir(String destDirName) {  
	        File dir = new File(destDirName);  
//	        if (dir.exists()) {  
//	            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");  
//	            return false;  
//	        }  
	        if (!destDirName.endsWith(File.separator)) {  
	            destDirName = destDirName + File.separator;  
	        }  
	        //创建目录  
	        if (dir.mkdirs()) {  
	            System.out.println("创建目录" + destDirName + "成功！");  
	            return true;  
	        } else {  
	            System.out.println("创建目录" + destDirName + "失败！");  
	            return false;  
	        }  
	    }  
	
 public boolean writeFileNameToListFile(String filename){
	 boolean flag = false;
	 try {
		if(!isExistFile(filename)){
			WriteToFile("listfile.txt",filename);
			flag = false;
		}else {
			flag = true;
			logger.info("The File is already Parse !!!!!!!!!!!!!");
		}
	} catch (Exception e) {
		 logger.error("",e);
	}
	return flag;
 }
	
	public boolean isExistFile(String str) {
		boolean flag = false;
		try {
			List<String> urlList = readLocalListFile();
			for(int i=0;i<urlList.size();i++){
				if(urlList.get(i).equals(str)){
					flag = true;
				}
			}
			return flag;
		} catch (Exception e) {
			logger.error("",e);
		}
		return flag;
	}
	
	public static List<String> readLocalListFile() {
		List<String> existV = new ArrayList<String>();
		try {
		if(new File("listfile.txt").exists()) {
				BufferedReader br = new BufferedReader(new FileReader("listfile.txt"));
				String line = "";
				while((line = br.readLine())!=null){
						existV.add(line);
				}
			}
		} catch (FileNotFoundException e) {
				logger.error("",e);
		} catch (IOException e) {
			   logger.error("",e);
		} 	
		return existV;
	}
	
	   public static String  covertString(String str){
	    	if(str==null||str.trim().length()<1)
	    		return null;
	    	else
	    		return str.trim();
	    }
	   
	public static void WriteToFile(String fileName, String content){   
        try {   
        	File file=new File(fileName);
            BufferedWriter out=new BufferedWriter(new FileWriter(file,true));
            out.write(content);
            out.newLine();
            out.close();
            out=null;
            file=null;
        } catch (IOException e) {   
        	 logger.error("",e);
        }   
     } 
	
	
	public static List<String> readLocalFilelist() {
		List<String> existV = new ArrayList<String>();
		try {
		if(new File("listfile.txt").exists()) {
				BufferedReader br = new BufferedReader(new FileReader("listfile.txt"));
				String line = "";
				while((line = br.readLine())!=null){
						existV.add(line);
				}
			}
		} catch (FileNotFoundException e) {
				logger.error("",e);
		} catch (IOException e) {
			   logger.error("",e);
		} 	
		return existV;
	}
	
	public boolean isFileRead(String str) {
		boolean flag = false;
		try {
			List<String> urlList = readLocalListFile();
			for(int i=0;i<urlList.size();i++){
				if(urlList.get(i).equals(str)){
					flag = true;
				}
			}
			return flag;
		} catch (Exception e) {
			logger.error("",e);
		}
		return flag;
	}
	
	public  void WriteFile(String fileName, String content){   
        try {   
        	File file=new File(fileName);
            BufferedWriter out=new BufferedWriter(new FileWriter(file,true));
            out.write(content);
            out.newLine();
            out.close();
            out=null;
            file=null;
        } catch (IOException e) {  
        	 logger.error("",e);
        }   
     } 
	
	public String getTime(String time){
		if(time==null) return null;
	     String hms = "";
	     if(time.trim().indexOf(" ")>-1){
	      hms =  " "+time.split(" ")[1];
	      time= time.split(" ")[0];
	     }
	        String date[] = time.split("-");
	        if(date.length!=3){
	            date = time.split("\\/");
	            if(date.length!=3)return null;
	           }
	        String yy = date[2];
	        String mm = date[1];
	        String dd = date[0];
	        
	        mm = mm.length()<2?"0"+mm:mm;
	        dd = dd.length()<2?"0"+dd:dd;     
	        return yy+"-"+mm+"-"+dd+hms;
	    }
	
	  public String getValueByPatter(String sIn, String strPattern) {
		  String sOut = null;
		  // String strPattern = ".*?pour(.*?)&euro;.*?";
		  Matcher matcher = getMatcherStrGroup(sIn, strPattern);
		  if (matcher.find()) {
			  sOut = matcher.group(1).trim();
		  }
		  if(sOut!=null&&sOut.equals(""))sOut=null;
		  matcher=null;
		  return sOut;
		 }
	
	public Vector<String> getHistoryDate(String sFromDate, String sToDate) {
		int fYear = Integer.parseInt(sFromDate.substring(0, 4));
		int fMonth = Integer.parseInt(sFromDate.substring(4, 6));
		int fDay = Integer.parseInt(sFromDate.substring(6, 8));
		int tYear = Integer.parseInt(sToDate.substring(0, 4));
		int tMonth = Integer.parseInt(sToDate.substring(4, 6));
		int tDay = Integer.parseInt(sToDate.substring(6, 8));
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
		GregorianCalendar gcFrom = new GregorianCalendar(fYear,aMonth[fMonth - 1], fDay);
		GregorianCalendar gcTo = new GregorianCalendar(tYear,aMonth[tMonth - 1], tDay);
		int tyear = gcTo.get(Calendar.YEAR);
		int tmonth = gcTo.get(Calendar.MONTH) + 1;
		int tday = gcTo.get(Calendar.DAY_OF_MONTH);
		String sMonth = String.valueOf(tmonth);
		String sDate = String.valueOf(tday);
		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
		sDate = sDate.length() == 1 ? "0" + sDate : sDate;
		String toDateTime = "" + tyear + sMonth + sDate;
		do {
			Year = gcFrom.get(Calendar.YEAR);
			Month = gcFrom.get(Calendar.MONTH) + 1;
			DAY = gcFrom.get(Calendar.DAY_OF_MONTH);
			sMonth = String.valueOf(Month);
			sDate = String.valueOf(DAY);
			sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
			sDate = sDate.length() == 1 ? "0" + sDate : sDate;
			DateTime = "" + Year + sMonth + sDate;
			vHistroyDate.add(DateTime);
			gcFrom.add(Calendar.DAY_OF_YEAR, 1);
		} while (!DateTime.equals(toDateTime));
		return vHistroyDate;
	}
	
	/**
	 * 将当前时间转换为相应时区的方法
	 * 参数1： 需要转换的时区 列如  GMT+2:00
	 * 参数2 ：当前时间 列如 2015-11-03 11:17:06
	 * return： 转换后的时间
	 * */
	public String  transferTime(String timeSzone,String Date){
		try {
			TimeZone timeZone= TimeZone.getTimeZone(timeSzone);
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			inputFormat.setTimeZone(timeZone);
			SimpleDateFormat outputFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date newDate = outputFormat.parse(Date);
			return inputFormat.format(newDate);
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}
	/**
	 * 计算两个时间之间相差天数的方法 
	 * 参数 1 : 开始时间
	 * 参数 2 ：结束时间
	 *返回值 两个日期之间相差的天数 
	 * */
	public static int getDiscrepantDays(Date dateStart, Date dateEnd) {
	    return (int) ((dateEnd.getTime() - dateStart.getTime()) / 1000 / 60 / 60 / 24);
	}
	
	public static boolean isBeforeToday(String startDate,String endDate){
		 boolean flag =false;
		if(Integer.parseInt(startDate)<Integer.parseInt(endDate))
			flag =true;
		 return flag;
	 }
	//通过配置文件中BLLNO获得存储路径的方法
	public String nullConvert(java.lang.Object object) {
		String s = "";
		if (object != null) {
			s = String.valueOf(object);
		} else {
			s = "&nbsp;";
		}
		return s;
	}
	//获得table标签对中的数据的方法 返回值为 一个 向量
	public Vector<String> getTable(String sHTML) {
		Vector<String> vTable = new Vector<String>();
		try {

			if (sHTML.length() > 0) {
				String sPattern = "<table.*?>.*?</table>";
				Pattern pattern = Pattern.compile(sPattern,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher matcher = pattern.matcher(sHTML);
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
//获得tr中的内容的方法
	public Vector<String> getTR(String sTable) {
		Vector<String> vTable = new Vector<String>();
		try {
			if (sTable.length() > 0) {
				String sPattern = "<tr.*?>(.*?)</tr>";
				Pattern pattern = Pattern.compile(sPattern,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher matcher = pattern.matcher(sTable);
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
	
	//读取PDF文件的方法
	//获得DIV中的内容的方法
	public Vector<String> getDIV(String sDiv) {
		Vector<String> vTable = new Vector<String>();
		try {
			if (sDiv.length() > 0) {
				String sPattern = "<DIV ALIGN=center>(.*?)</DIV>";
				Pattern pattern = Pattern.compile(sPattern,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher matcher = pattern.matcher(sDiv);
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

//获得TD中的内容的方法
	public Vector<String> getTD(String sTR) {
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

	/* 
	 	 * Remove everything between the tagStart and the closing >
	 	 * - if complete tag not found, just return input string
	 	 * - crude, but good enough for now
	 	 */
	//去掉指定的标签的方法 
	public String removeHtmlTag(String input, String tag) {
		if(tag==null)tag="<.*?>";
		Matcher matcher =getMatcherStrGroup(input,"("+tag+")");
		ArrayList<String> alTag=new ArrayList<String>();
		while(matcher.find()){
			alTag.add(matcher.group(1));
		}
		for(int i=0;i<alTag.size();i++){
			String tagTmp=alTag.get(i);
			input=removeSymbol(input, tagTmp);
		}
		return input;
	}

	public String removeTag(String sIn) {
		//String sPattern3 = "<span.*?>(.*?)</span>";
		String sPattern4 = "<a href=.*?>.*?<a href=.*?>(.*?)</a>";
		Pattern pattern4 = Pattern.compile(sPattern4, Pattern.DOTALL| Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern4.matcher(sIn);
		if (matcher.find()) {
			sIn = matcher.group(1);
		}
		sIn = removeSymbol(sIn, "&nbsp;");
		sIn = removeSymbol(sIn, "<br>");
		sIn = removeSymbol(sIn, "<BR>");
		sIn = sIn.trim();
		return sIn;
	}
	
	//将指定的字符串替换为“ ”
	private String removeSymbol(String s, String oldText) {
		String newText = " ";
		StringBuffer sb = new StringBuffer();
		int i = 0, l = oldText.length(), x = 0;
		try {
			while (true) {
				x = s.indexOf(oldText, i);
				sb.append(s.substring(i, x));
				sb.append(newText);
				i = x + l;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		sb.append(s.substring(i));
		return sb.toString();
	}
	//给定一个文件名 判断文件是否存在
	public boolean bIfExistFile(String sFileName) {
		boolean bExits = false;
		File fFile = new File(sFileName);
		if (fFile.exists()) {
			bExits = true;
		}
		return bExits;
	}
//将文件保存到指定目录下
	public void saveFileContentToLocal(String sFileName, String sFileContent) {
		try {
			saveFileContentToLocal(sFileName, sFileContent.getBytes(), true);
		} catch (Exception e) {
			logger.error(e.toString());
			System.out.println("Write Log" + e.toString());
		}
	}
//将文件保存到指定的目录 如果不存在就替换
	public void saveFileContentToLocal(String fileName, byte[] bytes,boolean isReplace) {
		File f = new File(fileName);
		FileOutputStream myos=null;
		try {
			if (!f.exists() || isReplace) {
				myos = new FileOutputStream(f);
				myos.write(bytes, 0, bytes.length);
				myos.close();
			} else
				logger.debug("file " + fileName + " aly exist");
		} catch (java.io.IOException e) {
			logger.error(fileName+":"+e.toString());
		}finally{
			if(myos!=null){
				try {
					myos.close();
				} catch (IOException e) {
					logger.error(fileName+" close file error:"+e.toString());
					e.printStackTrace();
				}
			}
			f=null;
			myos=null;
		}
	}
//删除文件
	public void deleteFile(String sFileName) {
		try {
			String sPathFileName = sFileName;
			File fFile = new File(sPathFileName);
			if (fFile.isFile()) {
				//backupFile(sFileName);
				fFile.delete();
				// fFile.deleteOnExit();
			}
		} catch (Exception e) {
			logger.info("Not Found "+sFileName);
			e.printStackTrace();
		}
	}
	
	//读取本地文件的方法 参数 本地文件目录
	public Vector<String> readLocalFile(String sPathName) {
		Vector<String> vPathFileName = new Vector<String>();
		String sPathFileName = sPathName;
		File fFile = new File(sPathFileName);
		File fPathFileName[] = fFile.listFiles();
		int iFileSize = fPathFileName.length;
		for (int i = 0; i < iFileSize; i++) {
			vPathFileName.add(fPathFileName[i].toString());
//			if(bIsBetweenDate(fPathFileName[i].toString())){
//				  vPathFileName.add(fPathFileName[i]); 
//				  System.out.println("add:fPathFileName["+i+"]="+fPathFileName[i]); 
//			  }
		}
		return vPathFileName;
	}

	public Matcher getMatcherStrGroup(String strContent, String strPattern) {
		Pattern pattern = Pattern.compile(strPattern, Pattern.DOTALL| Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(strContent);
		return matcher;
	}
// 判断是否为数字的方法
	public boolean isNumber(String sIn) {
		boolean bFlag = true;
		for (int i = 0; i < sIn.length(); i++) {
			if (sIn.charAt(i) < '0' || sIn.charAt(i) > '9')
				return false;
		}
		return bFlag;
	}
	//3.6--- 0.036
	public double parNum(double num){
		//小数格式化，引号中的0.000表示保留小数点后三位（第四位四舍五入）
		String newNum = num/100.0+"0";
		DecimalFormat df = new DecimalFormat("0.000");
		double a = Double.parseDouble(newNum);
		return  Double.parseDouble(df.format(a));
	}
	///创建文件目录的方法
	public void createDirectory(String sDirectoryPath) {
		File fFile = new File(sDirectoryPath);
		try {
			if (!fFile.exists()) {
				fFile.mkdirs();
			} else {
				System.out.println("The path " + sDirectoryPath + " is exits!");
			}
		} catch (Exception e) {
			logger.error(e.toString()+" by create path :"+sDirectoryPath);
			e.printStackTrace();
		}
	}
	
	//转换日期格式的方法 Tue Jul 14 17:39:33 CST 2015----- 20150714
	public String getYYYYMMDD(Date dateTime) {
		@SuppressWarnings("deprecation")
		String year = dateTime.getYear() + 1900 + "";
		@SuppressWarnings("deprecation")
		String month = dateTime.getMonth() + 1 + "";
		month = month.length() == 1 ? "0" + month : month;
		@SuppressWarnings("deprecation")
		String date = dateTime.getDate() + "";
		date = date.length() == 1 ? "0" + date : date;
		return year + month + date;
	}
	
	public String getYYYYMMDD() {
		Date dateTime=new Date();
		@SuppressWarnings("deprecation")
		String year = dateTime.getYear() + 1900 + "";
		@SuppressWarnings("deprecation")
		String month = dateTime.getMonth() + 1 + "";
		month = month.length() == 1 ? "0" + month : month;
		@SuppressWarnings("deprecation")
		String date = dateTime.getDate() + "";
		date = date.length() == 1 ? "0" + date : date;
		return year + month + date;
	}
// 获得IBefore 多少年 返回年份
	public String getBeforeYYYY(int iBefore) {
		Date dateTime = new Date();
		@SuppressWarnings("deprecation")
		String year = (dateTime.getYear() - iBefore) + 1900 + "";
		return year;
	}
//获得增加 iDay天后的 日期的
	public String getAddDay(int iDay) {
		Date oDate=new Date();
		@SuppressWarnings("deprecation")
		int year = oDate.getYear() + 1900 ;
		@SuppressWarnings("deprecation")
		int month = oDate.getMonth() + 1;
		@SuppressWarnings("deprecation")
		int date = oDate.getDate() ;
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
		
		oDate=null;
		oGC=null;
		return sYear + sMonth + sDate;
	}
	public String getAddDay(String StartDate,int iDay) {
		try {
		SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd");
		Date oDate = d.parse(StartDate);
		@SuppressWarnings("deprecation")
		int year = oDate.getYear() + 1900 ;
		@SuppressWarnings("deprecation")
		int month = oDate.getMonth() + 1;
		@SuppressWarnings("deprecation")
		int date = oDate.getDate() ;
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
		oDate=null;
		oGC=null;
		return sYear + sMonth + sDate;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
//从一个时间段 增加多少天后 的日期的方法
	public String getAddDay(int iDay, String sYYYYMMDD) {
		String sOut=sYYYYMMDD;
    	GregorianCalendar oGC=new GregorianCalendar(
    			Integer.parseInt(sYYYYMMDD.substring(0,4)),//year
    					Integer.parseInt(sYYYYMMDD.substring(4,6))-1,//month
    							Integer.parseInt(sYYYYMMDD.substring(6,8)));//day
    		
		oGC.add(Calendar.DATE,iDay);
		int year=oGC.get(Calendar.YEAR);
		int month=oGC.get(Calendar.MONTH)+1;
		int date=oGC.get(Calendar.DATE);
		sOut=(year<10?"0"+year:year+"") + (month<10?"0"+month:month+"") + (date<10?"0"+date:date+"");
		oGC=null;
    	return sOut;
	}
	public String getCurrentTime() {
		SimpleDateFormat oFormatter;
		String sTimeFormat = "yyyy-MM-dd HH:mm:ss";
		oFormatter = new SimpleDateFormat(sTimeFormat);
		return oFormatter.format(new Date());
	}
	
	public String getYYYYmmDDHHMMSS() {
		int year = 0;
		int month = 0;
		int date = 0;
		Calendar oGC = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"));
		year=oGC.get(Calendar.YEAR);
		month=oGC.get(Calendar.MONTH)+1;
		date=oGC.get(Calendar.DATE);
		String sYear=year+"";
		String sMonth=month+"";
		String sDate=date+"";
		String sHour=oGC.get(Calendar.HOUR_OF_DAY)+"";
		String sMin=oGC.get(Calendar.MINUTE)+"";
		String sSec=oGC.get(Calendar.SECOND)+"";
		sMonth= sMonth.length()==1?"0"+sMonth:sMonth;
		sDate= sDate.length()==1?"0"+sDate:sDate;
		sHour= sHour.length()==1?"0"+sHour:sHour;
		sMin= sMin.length()==1?"0"+sMin:sMin;
		sSec= sSec.length()==1?"0"+sSec:sSec;
		oGC=null;
		return sYear + "-"+sMonth +"-"+ sDate+" "+ sHour+":"+sMin+":"+sSec;
		
	}
	//获得时分秒
	public String getHHMMSS() {
		Calendar oGC = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"));
		String sHour=oGC.get(Calendar.HOUR_OF_DAY)+"";
		String sMin=oGC.get(Calendar.MINUTE)+"";
		String sSec=oGC.get(Calendar.SECOND)+"";
		
		sHour= sHour.length()==1?"0"+sHour:sHour;
		sMin= sMin.length()==1?"0"+sMin:sMin;
		sSec= sSec.length()==1?"0"+sSec:sSec;
		
		oGC=null;
		return sHour+sMin+sSec;
		
	}
//判断文件夹中保存的文件日期是否在某个日期中
//	public static boolean bIsBetweenDate(String sPathName) {
//		boolean bReturn = false;
//		int iStartDate = Integer.parseInt(Controller.sStartDate);
//		int iEndDate = Integer.parseInt(Controller.sEndDate);
//
//		String sFileDate = "";
//		if (sPathName.indexOf(System.getProperty("file.separator")) > -1) {
//			sFileDate = sPathName.substring(sPathName.lastIndexOf(System.getProperty("file.separator")) + 1);
//			if (sPathName != null && sFileDate.length() > 11) {
//				if (sFileDate.charAt(0) > '0' && sFileDate.charAt(0) < '9') {
//					sFileDate = sFileDate.substring(0, 8);
//				} else {
//					sFileDate = sFileDate.substring(3, 11);
//				}
//			} else {
//				return true;
//			}
//		} else {
//			sFileDate = sPathName;
//		}
//
//		if(!(new CommonMethod().isNumber(sFileDate)))return false;
//		
//		if (Integer.parseInt(sFileDate) >= iStartDate
//				&& Integer.parseInt(sFileDate) <= iEndDate) {
//			bReturn = true;
//		}
//
//		return bReturn;
//	}
//获得本地IP地址的方法
	public static String getLocalHostIPAddress(){
		String ip="";
		try{
			 InetAddress addr = InetAddress.getLocalHost();
			 ip=addr.getHostAddress().toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return ip;
	} 

	
}