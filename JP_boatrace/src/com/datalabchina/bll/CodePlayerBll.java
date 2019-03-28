package com.datalabchina.bll;

//import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.datalabchina.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;
import com.datalabchina.common.PageHelper;

public class CodePlayerBll {
	private static Logger logger = Logger.getLogger(CodePlayerBll.class.getName());
	static PageHelper pageHelper = PageHelper.getPageHelper();
	static CommonDB oCommonDB =new CommonDB();
	static CommonMethod oCommonMethod = new CommonMethod();
	public static void main(String[] args) {
		List<String> playerCodeList = oCommonDB.getPlayerCode();
		for(int i=0;i<playerCodeList.size();i++){
			String playerId = playerCodeList.get(i);
			parsePlayerByplayerId(playerId);
		}
		
//		http://www.boatrace.jp/owpc/pc/data/racersearch/profile?toban=4194
	}
	public static void parsePlayerByplayerId(String playerId) {
		String PlayerID=null, PlayerName=null, Birthday=null, Weight=null, Height=null, PlayerClass=null, 
		Register=null, BloodType=null, Homeplace=null, ExtractTime=null, PlayerName_JP=null, PlayerImagePath=null, Sex=null,ImageSex = null;
		String basicUrl = "http://www.boatrace.jp/owpc/pc/data/racersearch/profile?toban="+playerId;
		try {
			String body = pageHelper.doGet(basicUrl);
			PlayerID = playerId;
			 PlayerName = oCommonMethod.getValueByPatter(body, "<p class=\"racer1_bodyName\">(.*?)</p>");
			 if(PlayerName ==null ||PlayerName.length()<1){
				 return;
			 }
			String playerInfo = oCommonMethod.getValueByPatter(body, "<dl class=\"list3\">(.*?)</dl>");
			PlayerName_JP = oCommonMethod.getValueByPatter(body, "<p class=\"racer1_bodyKana\">(.*?)</p>");
			PlayerImagePath = oCommonMethod.getValueByPatter(body, "<p class=\"racer1_image\">\\s*<img src=(.*?) width");
			
			ImageSex = getImageSex(playerId);
			
			Birthday = oCommonMethod.getValueByPatter(playerInfo, "<dt>生年月日</dt>\\s*<dd>(.*?)</dd>").replaceAll("/", "");
//			44kg
			Weight = oCommonMethod.getValueByPatter(playerInfo, "<dt>体重</dt>\\s*<dd>(.*?)</dd>").replaceAll("kg", "");
			Height = oCommonMethod.getValueByPatter(playerInfo, "<dt>身長</dt>\\s*<dd>(.*?)</dd>").replaceAll("cm", "");
			PlayerClass = oCommonMethod.getValueByPatter(playerInfo, "<dt>級別</dt>\\s*<dd>(.*?)</dd>");
			Register = oCommonMethod.getValueByPatter(playerInfo, "<dt>登録期</dt>\\s*<dd>(.*?)</dd>").replaceAll("期", "");
			BloodType = oCommonMethod.getValueByPatter(playerInfo, "<dt>血液型</dt>\\s*<dd>(.*?)</dd>").replaceAll("型", "");
			Homeplace = oCommonMethod.getValueByPatter(playerInfo, "<dt>出身地</dt>\\s*<dd>(.*?)</dd>");
			ExtractTime = oCommonMethod.getCurrentTime();
			
			saveCoddePlayerToDB(PlayerID, PlayerName, Birthday, Weight, Height, PlayerClass, Register, BloodType, Homeplace, ExtractTime, PlayerName_JP, PlayerImagePath, Sex,ImageSex);
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	//http://boatrace.jp/racerphoto/4720.jpg
	private static String getImageSex(String playerCode) {
		String imageSex  ="1";
		try{
			String imageUrl = "http://boatrace.jp/racerphoto/"+playerCode+".jpg";
			byte[] imagebyte = pageHelper.doGetByte(imageUrl,null);
			String fileName = Controller.sSaveFilePath+File.separator+"playerImage"+File.separator+playerCode+".jpg";
			FileDispose.saveFileContentToLocal(fileName, imagebyte, true);
			imageSex = getSexByImage(fileName);
//			oCommonDB.updSex(imageSex, playerCode);
		} catch (Exception e) {
			logger.error("",e);
		}
		return imageSex;
	}
	
	  public static String  getSexByImage(String image) {
	        int[] rgb = new int[3];
	        File file = new File(image);
	        BufferedImage bi = null;
	        try {
	            bi = ImageIO.read(file);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        int count =0;
	        int width = bi.getWidth();
	        int height = bi.getHeight();
	        int minX = bi.getMinX();
	        /*
	         * rgb[0] = (pixel & 0xff0000) >> 16;
					rgb[1] = (pixel & 0xff00) >> 8;
					rgb[2] = (pixel & 0xff);
	         * */
	        for(int y = height-15; y < height; y++) {
	            for(int x = minX; x < width; x++) {
	                //获取包含这个像素的颜色信息的值, int型
	                int pixel = bi.getRGB(x, y);
	                rgb[0] = (pixel & 0xff0000) >> 16; //r 值
	            	rgb[1] = (pixel & 0xff00) >> 8;
	            	rgb[2] = (pixel & 0xff);	// b值
//	            	if(Math.abs( rgb[0]-255)>30){ //r 值 -255 绝对值>100 的像素个数大于 1000 sex=1 否则 sex =2
	            	if(Math.abs( rgb[0]-255)<=30&&Math.abs( rgb[1]-115)<=30&&Math.abs( rgb[2]-180)<=30){ 
	            		count++;
	            	}
	            }
	        }
//	        System.err.println(count);
	        if(count>30){
	        	return "2";
	        }else {
	        	return "1";
	        }
	    }
	
	private static void saveCoddePlayerToDB(String playerID, String playerName,
			String birthday, String weight, String height, String playerClass,
			String register, String bloodType, String homeplace,
			String extractTime, String playerName_JP, String playerImagePath,
			String sex,String ImageSex) {
		try {
			String sSql ="";
			sSql+=playerID==null?"NULL,":"N'"+playerID+"',";
			sSql+=playerName==null?"NULL,":"N'"+playerName+"',";
			sSql+=birthday==null?"NULL,":"N'"+birthday+"',";
			sSql+=weight==null?"NULL,":"N'"+weight+"',";
			sSql+=height==null?"NULL,":"N'"+height+"',";
			sSql+=playerClass==null?"NULL,":"N'"+playerClass+"',";
			sSql+=register==null?"NULL,":"N'"+register+"',";
			sSql+=bloodType==null?"NULL,":"N'"+bloodType+"',";
			sSql+=homeplace==null?"NULL,":"N'"+homeplace+"',";
			sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
			sSql+=playerName_JP==null?"NULL,":"N'"+playerName_JP+"',";
			sSql+=playerImagePath==null?"NULL,":"N'"+playerImagePath+"',";
			sSql+=sex==null?"NULL,":"N'"+sex+"',";
			sSql+=ImageSex==null?"NULL":"N'"+ImageSex+"'";
			logger.info("pr_Code_Player_InsertData " + sSql);
			oCommonDB.execStoredProcedures("pr_Code_Player_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}

}
