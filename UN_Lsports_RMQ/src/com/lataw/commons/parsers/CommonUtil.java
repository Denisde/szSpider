package com.lataw.commons.parsers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;



/**
 * @author wgb
 */
public class CommonUtil {
	private static Logger logger = Logger.getLogger("CommonUtil");

	public String nullConvert(java.lang.Object object) {
		String s = "";

		if (object != null) {
			s = String.valueOf(object);
		} else {
			s = "&nbsp;";
		}
		return s;
	}

	public static String GetLocalIP() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress().toString(); // get local ip
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	

	public static  String readFileToStr(String fileName) {
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
			//return new String(in_b, "gbk");
			return new String(in_b);
			
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

		return sOut;
	}

	public static boolean IsFileExits(String cfg){
		boolean bExitsFile = false;
	
		try {
			File fFile = new File(cfg);
			if (fFile.exists()) {
				bExitsFile = true;
			}
		} catch (Exception e) {
			logger.warn(cfg +" error or is not exists!");
			bExitsFile=false;
		}
		return bExitsFile;
	}

	public static String[] getPropertyFromCfg() {
		boolean bCfg=CommonUtil.IsFileExits("config.xml");
		boolean bSpdr=CommonUtil.IsFileExits("spider.xml");
		
		String xmlContent="";
		if(bCfg){
			xmlContent=CommonUtil.readFileToStr("config.xml");
		}else if (bSpdr){
			xmlContent=CommonUtil.readFileToStr("spider.xml");
		}else{
			logger.warn("Not found \"config.xml\" and \"spider.xml\", spider will not search database info, and will not insert data into db!!");
			return null;
		}
		String dbName=CommonUtil.getValueByPatter(xmlContent,"<databaseName>(.*?)</databaseName>");
		if(dbName==null||dbName.equals("")){
			logger.warn("Not found database name in \"config.xml\" or \"spider.xml\", \nplease add \"<databaseName>database name</databaseName>\" and  \"<dbconfigPath>the path of dbconfig.xml</dbconfigPath>\" in config.xml or spider.xml. \nSpider will not search database info, and will not insert data into db!!");
			return null;
		}
		String cfgPath=CommonUtil.getValueByPatter(xmlContent,"<dbconfigPath>(.*?)</dbconfigPath>");
		
		return (new String[]{dbName,cfgPath});
	}
	

}