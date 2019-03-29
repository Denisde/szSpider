package com.datalabchina.bll1;

import java.io.File;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.common.CommonMethod;
import com.common.FileDispose;
import com.datalabchina.controler.Controller;

public class saveMessage {
	private static Logger logger = Logger.getLogger(saveMessage.class);
	public static String saveLine(String conent){
		String file_v2 =null;
		try {
			String filePathName = CommonMethod.getValueByPatter(conent, "<type_document>(.*?)</type_document>");
			String fileName = getFileName(conent);
			file_v2 =  Controller.sSaveFilePath +File.separator+ fileName.substring(0, 4) + File.separator+fileName.substring(0,8)+ File.separator+filePathName+File.separator+fileName;
			FileDispose.saveFile(conent, file_v2);
		} catch (Exception e) {
			logger.error("",e);
		}
		return file_v2;
	}
		
		private static String getFileName(String conent) {
			String fileName = "";
			try {
				Matcher m = CommonMethod.getMatcherStrGroup(conent, "<date_heure_generation>((\\d{1,})-(\\d{1,})-(\\d{1,})\\s*(\\d{1,}:\\d{1,}:\\d{1,}))</date_heure_generation>");
				if(m.find()) {
					String day= m.group(2);
					String month= m.group(3);
					String year= m.group(4);
					String time = m.group(5).replaceAll(":", "_");
					fileName = year+month+day+time+System.currentTimeMillis()+".xml";
				}else {
					fileName =File.separator+System.currentTimeMillis()+".xml";
				}
			} catch (Exception e) {
				logger.error("",e);
			}
			return fileName;
		}
}
