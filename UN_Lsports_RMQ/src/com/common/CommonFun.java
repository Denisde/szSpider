/*
 * Created on 2004-11-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.common;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CommonFun {
	
	public static Logger logger = Logger.getLogger(CommonFun.class.getName());
	public static String SYS_SEPARATOR = java.io.File.separator;
	
	public static void main(String[] args)
	{
		PropertyConfigurator.configure("config/log4j.properties");	
//		String PdfSavePath = CommonFun.GetCurrPath() + CommonFun.SYS_SEPARATOR
//		+ "PdfSave";
//		CommonFun.createDirectory(PdfSavePath);
//		String host = "www.datalabchina.com";
//		String username = "ukpdf@datalabchina.com";
//		String password = "20050216";
//		CommonFun.SaveEmailAttachToLocal(host, username, password, PdfSavePath,"pdf");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = DateSubHour(new Date(),-3);
		logger.info(df.format(date));
	}
	
	public static String GetStrFromPatter(String content,String patter,int position)
	{
		try
		{
			Matcher matcher = CommonFun.GetMatcherStrGroup(content, patter);
			if(matcher.find())
			{
				if(matcher.group(position)!=null)
					return matcher.group(position).trim();
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	public static String GetStrFromPatter(int pattern,String content,String patter,int position)
	{
		try
		{
			Matcher matcher = CommonFun.GetMatcherStrGroup(content, patter,pattern);
			if(matcher.find())
			{
				if(matcher.group(position)!=null)
					return matcher.group(position).trim();
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	public static String GetStrFromPatter(String content,String patter,int position,int number)
	{
		try
		{
			Matcher matcher = CommonFun.GetMatcherStrGroup(content, patter);
			int i=0;
			while(matcher.find())
			{
				i++;
				if(i==number)
					if(matcher.group(position)!=null)
						return matcher.group(position).trim();
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	public static String GetStrFromPatter(String content,String patter,int position,int number,int pattern)
	{
		try
		{
			Matcher matcher = CommonFun.GetMatcherStrGroup(content, patter);
			int i=0;
			while(matcher.find())
			{
				i++;
				if(i==number)
					if(matcher.group(position)!=null)
						return matcher.group(position).trim();
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	public static String GetStrFromPatter(String content,String patter,int position,boolean case_insensitive)
	{
		try
		{
			Matcher matcher = CommonFun.GetMatcherStrGroup(content, patter, true);
			if(matcher.find())
			{
				if(matcher.group(position)!=null)
					return matcher.group(position).trim();
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	public static String GetStrFromPatter(String content,String patter,int position,int number,boolean case_insensitive)
	{
		try
		{
			Matcher matcher = CommonFun.GetMatcherStrGroup(content, patter, true);
			int i=0;
			while(matcher.find())
			{
				i++;
				if(i==number)
					if(matcher.group(position)!=null)
						return matcher.group(position).trim();
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	public static String GetLocalIP()
	{
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress().toString(); //get local ip
		}
		catch(Exception e)
		{
			logger.error(e);
		} 
		return null;
	} 
	
	public static boolean FileRename(String oldfilename,String newfilename)
	{
		try{
			return new File(oldfilename).renameTo(new File(newfilename));
		}
		catch(Exception e)
		{
			logger.error(e.toString());
		}
		return false;
	}
	
	public static FilenameFilter fileNameFilter = new FilenameFilter() {
		public boolean accept(File arg0, String arg1) {
			String tmp = arg1.toLowerCase();
			if (tmp.endsWith(".html") || tmp.endsWith(".jpg")) {
				return true;
			}
			return false;
		}
	};
	

	
	public static void sleep(int mins,int maxs)
	{
		try
		{
			int s = RandomUtils.nextInt(maxs);
			if(s<mins)
				s = s+mins;
			Thread.sleep(s);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public static Matcher GetMatcherStrGroup(String strContent,	String strPattern, int intPattern) {
		Pattern pattern = Pattern.compile(strPattern, intPattern);
		Matcher matcher = pattern.matcher(strContent);
		return matcher;
	}
	
	public static Matcher GetMatcherStrGroup(String strContent,
			String strPattern) {
		Pattern pattern = Pattern.compile(strPattern, Pattern.DOTALL
				| Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(strContent);
		return matcher;
	}
	
	public static Matcher GetMatcherStrGroup(String strContent,
			String strPattern, boolean case_insensitive) {
		Pattern pattern = Pattern.compile(strPattern, Pattern.DOTALL
				| Pattern.CASE_INSENSITIVE);
		if(case_insensitive)
			pattern = Pattern.compile(strPattern, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(strContent);
		return matcher;
	}
	
	public static boolean GetMatcherStrGroupByIndex(Matcher matcher, int index) {
		for (int i = 0; i < index; i++)
			if(!matcher.find())return false;
		return true;
	}
	
	public static int GetMatcherCount(Matcher matcher) {
		int i=0;
		while(matcher.find())i++;
		matcher.reset();
		return i;
	}
	
	public static void OutToFile(String fileName, String content,
			boolean isReplace) {
		File f = new File(fileName);
		java.io.PrintWriter myprint;
		try {
			if (!f.exists() || isReplace) {
				myprint = new java.io.PrintWriter(new java.io.FileWriter(f,
						false), true);
				myprint.print(content);
				myprint.close();
				logger.info("file " + fileName + " save succ");
			} else
				logger.info("file " + fileName + " already exist");
		} catch (java.io.IOException e) {
			logger.error(e.toString());
		}
	}
	
	public static void OutToFileAppend(String fileName, String content,boolean isAppend) {
		File f = new File(fileName);
		java.io.PrintWriter myprint;
		try {
			myprint = new java.io.PrintWriter(new java.io.FileWriter(f,
					isAppend), true);
			myprint.print(content);
			myprint.close();
		} catch (java.io.IOException e) {
			logger.error(e.toString());
		}
	}
	
	public static void WriteFile(String fileName, String content) {
		try {
			File tmpFile = new File(fileName);
			
			if (tmpFile.exists()) {
				tmpFile.delete();
			}
			FileWriter fw = new FileWriter(fileName, true);
			fw.write(content);
			fw.close();
		}catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	public static void OutToFileByte(String fileName, byte[] bytes,
			boolean isReplace) {
		File f = new File(fileName);
		FileOutputStream myos;
		try {
			if (!f.exists() || isReplace) {
				myos = new FileOutputStream(f);
				myos.write(bytes, 0, bytes.length);
				myos.close();
				logger.info("file " + fileName + " save succ");
			} else
				logger.info("file " + fileName + " already exist");
		} catch (java.io.IOException e) {
			logger.error(e.toString());
		}
	}
	
	public static String ReadFile(String fileName) {
		java.lang.String strFileName;
		java.io.File objFile;
		java.io.FileReader objFileReader;
		char[] chrBuffer = new char[1000];		
		strFileName = fileName;
		objFile = new java.io.File(strFileName);
		
		try {
			if (objFile.exists()) {
				objFileReader = new java.io.FileReader(objFile);
				StringBuffer fileContract = new StringBuffer();				
				while ((objFileReader.read(chrBuffer)) != -1)
					fileContract.append(chrBuffer);
				objFileReader.close();
				return fileContract.toString();
			} else {
				logger.info("the file no exist:" + strFileName);
				return null;
			}
		} catch (Exception e) {
			logger.error("read file " + strFileName + " find wrong\r\n"
					+ e.toString());
		}
		return null;
		
	}
	
	public static String ReadFileUTF8(String fileName) {
		ByteArrayOutputStream swapStream = null;
		java.io.FileInputStream objFileReader = null;
		java.io.File objFile = null;
		try {
			
			objFile = new java.io.File(fileName);
			swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1000];
			int rc = 0;
			objFileReader = new FileInputStream(objFile);
			while ((rc = objFileReader.read(buff, 0, 1000)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] in_b = swapStream.toByteArray();
			return new String(in_b, "utf-8");
		} catch (Exception e) {
			logger.error("read file " + fileName + " find wrong\r\n"
					+ e.toString());			
		}
		finally {
			try
			{
				swapStream.close();
				objFileReader.close();
				
			}
			catch(Exception e)
			{
				logger.error(e);
			}
		}
		return null;
	}
	
	public static short ParseDistance(String distanceStr) {
		short distance = 0;
		short f = 0;
		char c;
		String field = "";
		char[] disArray = distanceStr.toCharArray();
		for (int i = 0; i < disArray.length; i++) {
			c = disArray[i];
			if (c >= '0' && c <= '9')
				field += c;
			else {
				f = Short.parseShort(field);
				field = "";
				switch (c) {
				case 'm':
					distance += f * 1760;
					break;
				case 'f':
					distance += f * 220;
					break;
				case 'y':
					distance += f;
					break;
				}
			}
		}
		return distance;
	}
	
	//This method judges whether it is a Integer
	public static boolean isNumber(String sIn) {
		boolean isNum = true;
		try {
			if ((sIn == null) || sIn.equals("") || sIn.length() == 0) {
				return isNum = false;
			} else
				for (int i = 0; i < sIn.length(); i++) {
					char c = sIn.charAt(i);
					if (c < '0' || c > '9') {
						isNum = false;
						break;
					}
				}
		} catch (Exception e) {
			logger.error(e);
		}
		return isNum;
	}
	
	public static boolean DownloadImg(String imgUrl, String relationFilePath,
			String imgType) {
		String imgtype = "jpg";
		if (imgType != null)
			imgtype = imgType;
		try {
			String imgSaveName = CommonFun.GetCurrPath() + relationFilePath;
			File outputFile = new File(imgSaveName);
			if (outputFile.exists())
				return true;
			if (imgSaveName.lastIndexOf("/") != -1)
				CommonFun.createDirectory(imgSaveName.substring(0, imgSaveName
						.lastIndexOf("/")));
			else
				CommonFun.createDirectory(imgSaveName.substring(0, imgSaveName
						.lastIndexOf(CommonFun.SYS_SEPARATOR)));
			URL url = new URL(imgUrl);
			Image image = ImageIO.read(url);
			ImageWriter imgWriter = null;
			Iterator iter = ImageIO.getImageWritersByFormatName(imgtype);
			if (iter.hasNext())
				imgWriter = (ImageWriter) iter.next();
			if (imgWriter == null)
				throw new RuntimeException("Can not get ImageWriter for "
						+ imgtype + "!");
			
			imgWriter.setOutput(new FileImageOutputStream(outputFile));
			imgWriter.write((BufferedImage) image);
			return true;
		} catch (Exception e) {
			logger.error(e.toString());
			return false;
		}
	}
	
	public static boolean DownloadImg1(String imgUrl, String relationFilePath,
			String imgType) {
		String imgtype = "jpg";
		if (imgType != null)
			imgtype = imgType;
		try {
			String imgSaveName = CommonFun.GetCurrPath() + relationFilePath;
			File outputFile = new File(imgSaveName);
			if (outputFile.exists())
				return true;
			if (imgSaveName.lastIndexOf("/") != -1)
				CommonFun.createDirectory(imgSaveName.substring(0, imgSaveName
						.lastIndexOf("/")));
			else
				CommonFun.createDirectory(imgSaveName.substring(0, imgSaveName
						.lastIndexOf(CommonFun.SYS_SEPARATOR)));
			URL url = new URL(imgUrl);
			logger.info(url.toString());
			Image image = ImageIO.read(url);
			ImageIO.write((BufferedImage) image, imgtype, outputFile);
			return true;
		} 
		catch(IllegalArgumentException e)
		{
			logger.info("can't get image from "+imgUrl);
			return false;
		}
		catch (Exception e) {
			logger.error(e.toString());
			return false;
		}
	}
	
	public static String GetCurrPath() {
		return System.getProperty("user.dir");
	}
	
	public static String getCurrentDateTime() {
		TimeZone oTZ;
		Calendar oCAL;
		SimpleDateFormat oFormatter;
		String sTimeFormat = "yyyy-MM-dd HH:mm:ss";
		
		oTZ = TimeZone.getTimeZone("GMT");
		TimeZone.setDefault(oTZ);
		oCAL = Calendar.getInstance(oTZ);
		oCAL.add(11, +8);
		oCAL.add(12, 0);
		oFormatter = new SimpleDateFormat(sTimeFormat);
		return oFormatter.format(oCAL.getTime());
	}
	
	public static Date GetFormatedDate(String formatStr, String dateStr) {
		try {
			DateFormat mydf = new SimpleDateFormat(formatStr);
			return mydf.parse(dateStr);
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}
	
	public static Date DateSub(Date date,int days)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE,days);
		return calendar.getTime();
	}
	
	public static Date DateSubHour(Date date,int hour)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR,hour);
		return calendar.getTime();
	}
	
	public static Date DateSubMinute(Date date,int minute)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE,minute);
		return calendar.getTime();
	}
	
	public static Date DateSubMillisecond(Date date,int millisecond)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MILLISECOND,millisecond);
		return calendar.getTime();
	}
	
	public static Date GetFormatedDate(String dateStr) {
		try {
			DateFormat mydf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return mydf.parse(dateStr);
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}
	}
	
	public static String DeleteFlag(String sStr) {
		String s = "";
		int sLen = 0;
		if (sStr != null) {
			sLen = sStr.length();
			for (int i = 0; i < sLen; i++) {
				char a = sStr.charAt(i);
				if (a != '"')
					s = s + String.valueOf(a);
				if (a == '\'')
					s = s + "\'";
			}
		} else
			s = "";
		return s;
	}
	
	public static void printVector(Vector v) {
		for (Enumeration enu = v.elements(); enu.hasMoreElements();)
			logger.info(enu.nextElement().toString());
	}
		
	public static void createDirectory(String sDirectoryPath) {
		logger.info(" Set Save local Path:" + sDirectoryPath);
		File fFile = new File(sDirectoryPath);
		try {
			if (!fFile.isFile() && !fFile.exists()) {
				fFile.mkdirs();
			} else {
				logger.info("The path " + sDirectoryPath + " is exits!");
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
	}
	
	public static String removeHTML(String htmlcontent) {
		try {
			String patter = "<.*?>";
			Matcher matcher = CommonFun.GetMatcherStrGroup(htmlcontent,patter);
			return matcher.replaceAll("");			
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return htmlcontent;
	}
}