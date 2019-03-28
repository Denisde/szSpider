package com.datalabchina.bll;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
//import java.util.Vector;
import java.util.regex.Matcher;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.datalabchina.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;
import com.datalabchina.common.PageHelper;

public class PreRaceBll implements Runnable{
	private static Logger logger = Logger.getLogger(PreRaceBll.class.getName());
	static PageHelper pageHelper = PageHelper.getPageHelper();
	static CommonDB oCommonDB =new CommonDB();
	Map<String,List<String>> map = new HashMap<String,List<String>>();
	 static CommonMethod oCommonMethod = new CommonMethod();
	static Hashtable<String,String> dateHt = new Hashtable<String,String>();
	@Override
	public void run() 
	{
		if(!login()){
			logger.error("Login faile !!!!!");
			return;
		}
		parse();
	}

	public void parse() {
//		String startDate =Controller.sStartDate;
//		String endDate =Controller.sEndDate;
//		Vector<String> vDate = oCommonMethod.getHistoryDate(startDate,endDate);
//		for(int i=0;i<vDate.size();i++) 
//			{
//				String rawRaceDate = vDate.get(i);
//				String raceDate =  convertRaceDate(rawRaceDate);
//				List<String> raceUrlList = getHistoryPage(raceDate);
//				getRacePageAndParse(raceUrlList,rawRaceDate);
//			}
		List<String> raceUrlList = getHistoryPage();
		String rawRaceDate = oCommonMethod.getYYYYMMDD();
		getRacePageAndParse(raceUrlList,rawRaceDate);
	}
	
//	public void parse() {
//		String startDate =Controller.sStartDate;
//		String endDate =Controller.sEndDate;
//		Vector<String> vDate = oCommonMethod.getHistoryDate(startDate,endDate);
//		for(int i=0;i<vDate.size();i++) 
//		{
//			String rawRaceDate = vDate.get(i);
//			String raceDate =  convertRaceDate(rawRaceDate);
//			List<String> raceUrlList = getHistoryPage(raceDate);
//			getRacePageAndParse(raceUrlList,rawRaceDate);
//		}
//	}
	
//		String fileName = "D:\\Denis\\elturf\\Test\\20170329\\174918.html";
//		String sPathName = "D:\\Denis\\elturf\\Test";
	public void readDataFromDir(String sPathName)
	{
		List<String>  fileList = FileDispose.readLocalFileDir(sPathName);
		for(int i=0 ;i<fileList.size();i++){
			String fileName =fileList.get(i); 
			parsePage(fileName);
		}
	}
	
	public void fixRaceByIdFromSql(){
		List<String> idlist = oCommonDB.getRaceId();
		for(int i=0;i<idlist.size();i++){
			String id = idlist.get(i);
			test(id);
		}
//		System.out.println(datelist);
	}

	//http://www.elturf.com/elturfcom/carreras-resultado-ver?id_carrera=173658
	public void test(String raceId){
		String url ="http://www.elturf.com/elturfcom/carreras-resultado-ver?id_carrera="+raceId;
		String body = pageHelper.doGet(url);
		String fileName =Controller.sSaveFilePath+File.separator+"test"+File.separator+oCommonMethod.getValueByPatter(url, "(\\d{1,6})")+".html";
		FileDispose.saveFile(body, fileName);
		parsePage(fileName);
	}
	
	private String convertRaceDate(String rawRaceDate) {
		try {
			if(rawRaceDate==null||rawRaceDate.length()!=8){
				logger.error("The date format is incorrect ,please check !!!");
				return "2017-1-1";	
			}
			return rawRaceDate.substring(0,4)+"-"+rawRaceDate.substring(4,6)+"-"+rawRaceDate.substring(6);
		} catch (Exception e) {
			logger.error("",e);
		}
		return "2017-1-1";
	}
	
//Martes 22 de Agosto del 2017
	public PreRaceBll()
	{
		dateHt.put("Enero", "01");
		dateHt.put("Febrero", "02");
		dateHt.put("Marzo", "03");
		dateHt.put("Abril", "04");
		dateHt.put("Mayo", "05");
		dateHt.put("Junio", "06");
		dateHt.put("Julio", "07");
		dateHt.put("Agosto", "08");
		dateHt.put("Septiembre", "09");
		dateHt.put("Octubre", "10");
		dateHt.put("Noviembre", "11");
		dateHt.put("Diciembre", "12");
	}
	
	//http://www.elturf.com/elturfcom/carreras-resultado-ver?id_carrera=158415
	private void getRacePageAndParse(List<String> raceUrlList,String raceDate) {
		if(raceUrlList==null){
			logger.error("================urlList is null!  please check!!!=================");
			return;
		}
		try {
			for(int i=0;i<raceUrlList.size();i++){
				//http://elturf.com/elturfcom/carreras-programa-ver?id_carrera=190886
//				String url = "http://www.elturf.com/elturfcom/"+raceUrlList.get(i);
				String url = "http://elturf.com/elturfcom/"+raceUrlList.get(i);
				//10890,10811,10758,10711
				String body = pageHelper.doGet(url);
				String  rawRaceDate =  oCommonMethod.getValueByPatter(body, "<td class=\"text-right\" style=\"width:45%;\">(.*?)</td>");
				if(rawRaceDate==null||"".equals(rawRaceDate))
					rawRaceDate =   oCommonMethod.getValueByPatter(body, "<div class=\"col-xs-6 col-sm-6 col-md-6 col-lg-6 text-right\">(.*?)</div>");
				raceDate = getRaceDate(rawRaceDate);
				if(raceDate!=null) {
					String fileName =Controller.sSaveFilePath+File.separator+raceDate.substring(0,4)+File.separator+raceDate+File.separator+oCommonMethod.getValueByPatter(url, "id_carrera=(\\d{1,6})")+".html";
					FileDispose.saveFile(body, fileName);
					parsePage(fileName);
				}
			}
		} catch (Exception e){
			logger.error("",e);
		}
	}
	
	
	public void readFromLocalBySql(){
		try {
			 List<String> list = oCommonDB.getfixRaceDate("select  CONVERT(varchar(100),raceDate, 112),raceid ,count(*) from   uruguaydb..elturf_postrace_horse "+
					 						" where  1=1  "+
					 						" group by raceid,raceDate "+
					 						" having count(*)>1 "+
					 						" order by 1");
			 for(int i=0;i<list.size();i++){
				 //20030528_12909 /home/szspider/websitefile/uruguay/elturf/postfile
				 String raceDateAndRaceid = list.get(i);
				 String localFile = Controller.sSaveFilePath+File.separator+raceDateAndRaceid.split("_")[0]+File.separator+raceDateAndRaceid.split("_")[1]+".html";
				 logger.info("*******************Start parse localFile *********************"+localFile);
				 parsePage(localFile);
			 }
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	
	public void fixRaceByraceId(String raceId){
		try {
			//http://elturf.com/elturfcom/carreras-programa-ver?id_carrera=190886
			String url = "http://elturf.com/elturfcom/carreras-programa-ver?id_carrera="+raceId;
			//10890,10811,10758,10711
			String body = pageHelper.doGet(url);
			String fileName =Controller.sSaveFilePath+File.separator+"fix"+File.separator+oCommonMethod.getValueByPatter(url, "(\\d{1,6})")+".html";
			FileDispose.saveFile(body, fileName);
			parsePage(fileName);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void parsePage(String fileName) {
		String body = FileDispose.readFile(fileName);
		if(body==null){
			logger.info("******************Start parse from Web****************************");
			login();
			String raceID = oCommonMethod.getValueByPatter(fileName, "(\\d{1,6}).html");
			fixRaceByraceId(raceID);
			return ;
		}
		if(body.length()<1||body.trim().equals("404")) {
			logger.error("==============body is empty !!!==================");
			return;
		}
		try {
			//<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-left">Reunión  5 Hipódromo Chile</div>
			String RawTrackName = oCommonMethod.getValueByPatter(body, "<td class=\"col-xs-6 col-sm-6 col-md-6 col-lg-6 text-left\">(.*?)</div>");
			if(RawTrackName==null||"".equals(RawTrackName))//col-xs-6 col-sm-6 col-md-6 col-lg-6 text-left
				RawTrackName =   oCommonMethod.getValueByPatter(body, "<div class=\"col-xs-6 col-sm-6 col-md-6 col-lg-6 text-left\">(.*?)</div>");
			
//			System.out.println(RawTrackName);
			//Reunión  5 Hipódromo Chile
			String trackName =RawTrackName.replaceAll("\\d","").replace("Reunión", "").trim(); 
			//<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-right">Martes 22 de Agosto del 2017</div>
			String  rawRaceDate =  oCommonMethod.getValueByPatter(body, "<td class=\"text-right\" style=\"width:45%;\">(.*?)</td>");
			if(rawRaceDate==null||"".equals(rawRaceDate))
				rawRaceDate =   oCommonMethod.getValueByPatter(body, "<div class=\"col-xs-6 col-sm-6 col-md-6 col-lg-6 text-right\">(.*?)</div>");
			
//			System.out.println(rawRaceDate);
			String raceDate = getRaceDate(rawRaceDate);
			String raceNo = oCommonMethod.getValueByPatter(body, "<h3><strong>(\\d{1,2}).*?</strong>");
			String officeGoing =null;
			String raceInfo = oCommonMethod.getValueByPatter(body,"<td class=\"text-left\" style=\"width:47.5%;\">(.*?)</td>");
			String actualStartTime = oCommonMethod.getValueByPatter(raceInfo, "(\\d{2}:\\d{2})");
			String meetingNo =  oCommonMethod.getValueByPatter(raceInfo, "<small>\\((.*?)\\)</small>\\s*</h3>");
			
			String distance =oCommonMethod.getValueByPatter(raceInfo, "(\\d{3,4})m");
			String arr[] = raceInfo.split("<br>");
			String raceName = null;
			String raceTypeAge = null;
			String surfaceName = null;
			String rawCategory =null;
			
			rawCategory = arr[0].substring(arr[0].indexOf("</h3>")).replace("</h3>", "").replaceAll("<.*?>", "").trim();
			if(arr.length==7){
				raceName  = (arr[1].replaceAll("<.*?>", "")+arr[2]+arr[3]).replaceAll("'", "");
				raceTypeAge = arr[4];
				surfaceName =arr[5].replace(distance+"m","").trim();
			}else if(arr.length==6){
				raceName  = (arr[1].replaceAll("<.*?>", "")+arr[2]).replaceAll("'", "");
				raceTypeAge = arr[3];
				surfaceName =arr[4].replace(distance+"m","").trim();
			}else if(arr.length==5){
				raceName  = (arr[1].replaceAll("<.*?>", "")+arr[2]).replaceAll("'","''");
				surfaceName =arr[3].replace(distance+"m","").trim();
			}else if(arr.length==4){
				raceName  = (arr[1].replaceAll("<.*?>", "")).replaceAll("'","''");
				surfaceName =arr[2].replace(distance+"m","").trim();
			}
			//<strong>Tiempo:</strong> 0.58.76 1000m Arena (Normal)</div>
			String rawleaderFinishTime = oCommonMethod.getValueByPatter(body, "<strong>Tiempo:</strong>(.*?)</div>");
			String prizeInfo = oCommonMethod.getValueByPatter(body, "<td class=\"text-right\" style=\"width:47.5%;\">(.*?)</td>");
			
//			String prize =oCommonMethod.getValueByPatter(body, "<br>\\s*Bolsa de Premios\\s*<br>\\s*<strong>\\$(.*?)</strong>").replaceAll("\\.", "");
//			Matcher m = oCommonMethod.getMatcherStrGroup(prizeInfo, "<strong>\\$(.*?)</strong>\\s*al\\s*(\\d{1,2}).*?<br>");
//			Hashtable<String,String> ht = new Hashtable<String,String>();
//			while(m.find()){
//				ht.put("prize"+m.group(2),m.group(1).replaceAll("\\.", ""));
//			}
			String prize =oCommonMethod.getValueByPatter(body, "<br>Bolsa de Premios<br>\\s*<strong>.*?\\$(\\d{1,}\\.?\\d{1,}\\.?\\d{1,})\\s*</strong>").replaceAll("\\.", "");
			//S/.10.915
			if(prize.length()<1) {
				prize =oCommonMethod.getValueByPatter(body, "<br>\\s*Bolsa de Premios\\s*<br>\\s*<strong>.*?S/?\\.?(\\d{1,}\\.?\\d{1,}\\.?\\d{1,})</strong>").replaceAll("\\.", "");
			}
//			System.out.println(prizeInfo);
			Hashtable<String,String> ht = new Hashtable<String,String>();
			Matcher m = oCommonMethod.getMatcherStrGroup(prizeInfo, "<strong>.*?\\$(\\d{1,}\\.?\\d{1,}\\.?\\d{1,})</strong>\\s*al\\s*(\\d{1,2}).*?<br>");
			while(m.find()){
//				System.out.println(m.group(1)+"-------------------"+m.group(2));
				ht.put("prize"+m.group(2),m.group(1).replaceAll("\\.", ""));
			}
			if(ht.size()<1) {
				Matcher m1 = oCommonMethod.getMatcherStrGroup(prizeInfo, "<strong>.*?S/?\\.(\\d{1,}\\.?\\d{1,}\\.?\\d{1,})</strong>\\s*al\\s*(\\d{1,2}).*?<br>");
				while(m1.find()){
//				System.out.println(m.group(1)+"-------------------"+m.group(2));
					ht.put("prize"+m1.group(2),m1.group(1).replaceAll("\\.", ""));
				}
			}
			
			String raceTitle =null;

			String raceId = oCommonMethod.getValueByPatter(fileName, "(\\d{1,6}).html");
			savePreRaceToDB(raceDate,trackName,raceNo,officeGoing,actualStartTime,
					raceName,surfaceName,prize,ht,raceTitle,raceTypeAge,distance,
					meetingNo,raceId,rawleaderFinishTime,rawCategory);
			
			parseHorseInfo(body,raceDate,trackName,raceNo,raceId);
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void parseHorseInfo(String body,String raceDate,String trackName,String raceNo,String raceId) {
		try {	
			String  RaceDate =raceDate, TrackName = trackName, RaceNo = raceNo, Raceid = raceId, ExtractTime =oCommonMethod.getCurrentTime();
			//<table style="border:0px; cellpadding:0px; cellspacing:0px; width:100%;">
			Matcher m = oCommonMethod.getMatcherStrGroup(body, "<table style=\"border:0px; cellpadding:0px; cellspacing:0px; width:100%;\">(.*?<table style=\"border:0px; cellpadding:0px; cellspacing:0px; width:100%;\">\\s*<tr style=\"vertical-align:top;\">.*?)</table>");
			while(m.find()){
				String horseInfo = m.group(1);
				String ClothNo=null, RawClothNo=null, BreederName=null, JockeyName=null, TrainerName=null, Sire=null, Dam=null, SireOfDam=null, HorseAgeDesc=null,
				WinOdds=null, HandicapWeight=null, HorseWeight=null, WebHorseID=null, HorseName=null, HorseDesc=null, HorseOrigin =null, Scratch ="0", Sex = null,
				BarrierDraw =null,HorseIndex=null,OwnerName = null;
//				String Sex=null;
				String horseData = oCommonMethod.getValueByPatter(horseInfo, "<small>\\s*<span class=\"esconder_resultados_tablet1 esconder_resultados_tablet2 esconder_resultados_pc1\">(.*?)</tr>");
//				<br></span> (415k) (I:1)</small></h2>CC 8a (Ago) <strong>Stud Marquez</strong>
//				<br>Pyrus   y  Maletita  por  Multiengine
//				<br>Haras Casablanca
//				<br>Enrique Lagunas R. <b>2</b>vsc
//				<br><strong>55k</strong> - Kevin Espina L. <b>2</b>hch <b>6</b>chs <b>1</b>vsc</td>
//				<td style="text-align:right;"><h4>P.1</h4></td>
				HorseName = oCommonMethod.getValueByPatter(horseInfo, "return true; \"><strong>(.*?)</strong>");
				WebHorseID = oCommonMethod.getValueByPatter(horseInfo, "caja_menu_ejemplar_(\\d{1,})_");
				
				HorseBll hb = new HorseBll();
				hb.parsePage(WebHorseID);
				
				Matcher m0 = oCommonMethod.getMatcherStrGroup(horseData, "</strong>\\s*<br>(.*?)   y  (.*?)  por  (.*?)<br>");
				if(m0.find()){
					Sire = m0.group(1);
					Dam = m0.group(2);
					SireOfDam = m0.group(3);
				}
				HorseWeight = oCommonMethod.getValueByPatter(horseData, "</span>\\s*\\((\\d{1,})k\\)");
				//</span> (437k) (I:46)</small></h2>
				HorseIndex = oCommonMethod.getValueByPatter(horseData, "\\(.*?(\\d{1,})\\)</small></h2>");
				
				//</h2>CC 8a (Ago) <strong>
				String rawSex = oCommonMethod.getValueByPatter(horseData, "</h2>(.*?)<strong>");
				String arr [] = rawSex.split(" ");
				if(arr.length==3){
					Sex =arr[0]; 
					HorseAgeDesc =arr[1]; 
				}
				OwnerName = oCommonMethod.getValueByPatter(horseData, "</h2>.*?<strong>(.*?)</strong>\\s*<br>");
				//<strong>56.5k</strong> - Héctor I. Berríos Ch. <b>14</b>hch <b>28</b>chs <b>21</b>vsc</td>
				Matcher m1 = oCommonMethod.getMatcherStrGroup(horseData, "</strong>\\s*<br>.*?<br>(.*?)<br>(.*?)<br>\\s*<strong>\\s*(\\d{1,}\\.?\\d{1,})k</strong>(.*?)</td>");
				if(m1.find()){
					BreederName =convertName(m1.group(1).replaceAll("<.*?>", "--"));
					//Carlos O. Conejeros A. <b>13</b>hch
					TrainerName =convertName(m1.group(2).replaceAll("<.*?>", "--"));
					HandicapWeight =m1.group(3).replaceAll("<.*?>", "--");
					//  Felipe Henríquez 3hch 4chs 1vsc
					JockeyName = convertName(m1.group(4).replaceAll("<.*?>", "--")).replace("-", "").trim();
				}else{
					m1 = oCommonMethod.getMatcherStrGroup(horseData, "</strong>\\s*<br>.*?<br>(.*?)\\s*<strong>\\s*(\\d{1,}\\.?\\d{1,})k</strong>(.*?)</td>");
					if(m1.find()){
						TrainerName =convertName(m1.group(1).replaceAll("<.*?>", "--"));
						HandicapWeight =m1.group(2).replaceAll("<.*?>", "--");
						//  Felipe Henríquez 3hch 4chs 1vsc
						JockeyName = convertName(m1.group(3).replaceAll("<.*?>", "--")).replace("-", "").trim();
					}
				}
				
				//<td style="text-align:right;"><h4>P.1</h4></td>
				String rawBarrierDraw = oCommonMethod.getValueByPatter(horseData, "<td style=\"text-align:right;\">\\s*<h4>(.*?)</h4>\\s*</td>");
				//P.13
				BarrierDraw = rawBarrierDraw.replace("P.", "");
				//<td style="text-align:middle;"><h1>1a</h1></td>
				RawClothNo = oCommonMethod.getValueByPatter(horseInfo, "<td style=\"text-align:middle;\">\\s*<h1>(.*?)</h1>\\s*</td>");
				
				ClothNo = oCommonMethod.getValueByPatter(RawClothNo, "(\\d{1,})");
				//<div class="col-sm-12 text-center elt_retirado">
				if(RawClothNo.indexOf("<strike>")>-1){
					Scratch = "1";
				}
				
				savePostHorseToDB(RaceDate, TrackName, RaceNo, WebHorseID, 
						HorseName, HorseDesc, HorseOrigin, ClothNo, RawClothNo, BreederName, JockeyName, TrainerName, Sire, Dam, 
						SireOfDam, HorseAgeDesc, WinOdds, HandicapWeight, HorseWeight, Raceid, Scratch, ExtractTime,Sex,BarrierDraw,HorseIndex,OwnerName);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	public String convertName(String Name){
		//  Felipe Henríquez 3hch 4chs 1vsc
		try {
			if(Name==null)return null;
			if(Name.indexOf("--")>-1){
//				if(Name.matches("\\d")){
//					Name = oCommonMethod.getValueByPatter(Name, "(.*?)\\d{1}").trim();
//					return Name.replaceAll("'", "''");
//				}else if(Name.indexOf(".")>-1){
//					return Name.substring(0,Name.indexOf("."));
//				}
				return Name.substring(0,Name.indexOf("--"));
			}else{
				return Name;
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return Name;
	}
	
	   private void savePostHorseToDB(String raceDate, String trackName,
			String raceNo, String webHorseID, String horseName,
			String horseDesc, String horseOrigin, String clothNo,
			String rawClothNo, String breederName, String jockeyName,
			String trainerName, String sire, String dam, String sireOfDam,
			String horseAgeDesc, String winOdds, String handicapWeight,
			String horseWeight, String raceid, String scratch,
			String extractTime,String Sex,String BarrierDraw,String HorseIndex,String OwnerName) {
		   try {
			   String sSql ="";
				sSql+=raceDate==null?"NULL,":"N'"+raceDate+"',";
				sSql+=covertString(trackName)==null?"NULL,":"N'"+trackName+"',";
				sSql+=raceNo==null?"NULL,":"N'"+raceNo+"',";
				sSql+=webHorseID==null?"NULL,":"N'"+webHorseID+"',";
				sSql+=covertString(horseName)==null?"NULL,":"N'"+horseName+"',";
				sSql+=horseDesc==null?"NULL,":"N'"+horseDesc+"',";
//				sSql+=horseOrigin==null?"NULL,":"N'"+horseOrigin+"',";
				sSql+=clothNo==null?"NULL,":"N'"+clothNo+"',";
				sSql+=rawClothNo==null?"NULL,":"N'"+rawClothNo.replaceAll("<.*?>", "")+"',";
				sSql+=covertString(breederName)==null?"NULL,":"N'"+breederName+"',";
				sSql+=covertString(jockeyName)==null?"NULL,":"N'"+jockeyName+"',";
				sSql+=covertString(trainerName)==null?"NULL,":"N'"+trainerName+"',";
				sSql+=covertString(sire)==null?"NULL,":"N'"+sire+"',";
				sSql+=covertString(dam)==null?"NULL,":"N'"+dam+"',";
				sSql+=covertString(sireOfDam)==null?"NULL,":"N'"+sireOfDam+"',";
				sSql+=horseAgeDesc==null?"NULL,":"N'"+horseAgeDesc+"',";
//				sSql+=winOdds==null?"NULL,":"N'"+winOdds+"',";
				sSql+=handicapWeight==null?"NULL,":"N'"+handicapWeight+"',";
				sSql+=covertString(horseWeight)==null?"NULL,":"N'"+horseWeight+"',";
				sSql+=raceid==null?"NULL,":"N'"+raceid+"',";
				sSql+=scratch==null?"NULL,":"N'"+scratch+"',";
				sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
				sSql+=Sex==null?"NULL,":"N'"+Sex+"',";
				sSql+=BarrierDraw==null?"NULL,":"N'"+BarrierDraw+"',";
				sSql+=covertString(HorseIndex)==null?"NULL,":"N'"+HorseIndex+"',";
				sSql+=OwnerName==null?"NULL":"N'"+OwnerName+"'";
				
				logger.info("pr_Elturf_PreRace_Horse_InsertData sql :" + sSql);
				oCommonDB.execStoredProcedures("pr_Elturf_PreRace_Horse_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
		   
	}

	private static String  covertString(String str){
	    	if(str==null||str.trim().length()<1)
	    		return null;
	    	else
	    		return str.trim().replaceAll("'", "''");
	    }
	
	
	private void savePreRaceToDB(String raceDate, String trackName,
			String raceNo, String officeGoing, String actualStartTime,
			String raceName, String surfaceName,String prize,
			Hashtable<String, String> ht, String raceTitle, String raceTypeAge,
			String distance, String meetingNo, String raceId,
			String rawleaderFinishTime, String rawCategory) {
		try {
			String sSql ="";
			sSql+=raceDate==null?"NULL,":"N'"+raceDate+"',";
			sSql+=trackName==null?"NULL,":"N'"+trackName+"',";
			sSql+=raceNo==null?"NULL,":"N'"+raceNo+"',";
			sSql+=officeGoing==null?"NULL,":"N'"+officeGoing+"',";
			sSql+=actualStartTime==null?"NULL,":"N'"+actualStartTime+"',";
			sSql+=raceName==null?"NULL,":"N'"+raceName+"',";
			sSql+=surfaceName==null?"NULL,":"N'"+surfaceName+"',";
			sSql+=prize==null?"NULL,":"N'"+prize+"',";
			sSql+=ht.get("prize1")==null?"NULL,":"N'"+ht.get("prize1")+"',";
			sSql+=ht.get("prize2")==null?"NULL,":"N'"+ht.get("prize2")+"',";
			sSql+=ht.get("prize3")==null?"NULL,":"N'"+ht.get("prize3")+"',";
			sSql+=ht.get("prize4")==null?"NULL,":"N'"+ht.get("prize4")+"',";
			sSql+=ht.get("prize5")==null?"NULL,":"N'"+ht.get("prize5")+"',";
			sSql+=ht.get("prize6")==null?"NULL,":"N'"+ht.get("prize6")+"',";
			sSql+=ht.get("prize7")==null?"NULL,":"N'"+ht.get("prize7")+"',";
			sSql+=ht.get("prize8")==null?"NULL,":"N'"+ht.get("prize8")+"',";
			sSql+=ht.get("prize9")==null?"NULL,":"N'"+ht.get("prize9")+"',";
			sSql+=ht.get("prize10")==null?"NULL,":"N'"+ht.get("prize10")+"',";
			sSql+=raceTitle==null?"NULL,":"N'"+raceTitle+"',";
			sSql+=raceTypeAge==null?"NULL,":"N'"+raceTypeAge+"',";
			sSql+=distance==null?"NULL,":"N'"+distance+"',";
			sSql+=meetingNo==null?"NULL,":"N'"+meetingNo+"',";
			sSql+="N'"+oCommonMethod.getCurrentTime()+"',";
			sSql+=raceId==null?"NULL,":"N'"+raceId+"',";
			sSql+=rawCategory==null?"NULL":"N'"+rawCategory+"'";
			
			logger.info("pr_Elturf_PreRace_Race_InsertData sql :" + sSql);
			oCommonDB.execStoredProcedures("pr_Elturf_PreRace_Race_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	//Martes 22 de Agosto del 2017
	private String  getRaceDate(String rawRaceDate) {
		String racePatter = "[^<]+? (\\d{1,2}) de ([^<]+?) del (\\d{4})";
		Matcher matcher = oCommonMethod.getMatcherStrGroup(rawRaceDate, racePatter);
		if(matcher.find())
		{
			String yyyy = matcher.group(3);
			String MM = dateHt.get(matcher.group(2)).toString();
			String dd = matcher.group(1);
			if(dd.length()<2)
				dd = "0"+dd;
			return yyyy+MM+dd;
		}else{
			return null;
		}
	}

	//http://www.elturf.com/elturfcom/carreras-ultimos-resultados
	//http://www.elturf.com/elturfcom/carreras-buscar-programas?fecha_reunion=2017-3-28#
	private List<String>  getHistoryPage(String raceDate) {
//		String raceDate ="2017-3-28";
									 //http://             elturf.com/elturfcom/carreras-buscar-programas?id_pais_programa=&fecha_reunion=2018-1-19
									 //http://elturf.com/elturfcom/carreras-proximos-programas?id_pais_programa=&fecha_reunion=2019-3-1#
//		String  raceUrl ="http://www.elturf.com/elturfcom/carreras-buscar-programas?id_pais_programa=&fecha_reunion="+raceDate+"#";
		String  raceUrl ="http://elturf.com/elturfcom/carreras-proximos-programas?id_pais_programa=&fecha_reunion="+raceDate+"#";
		String body = pageHelper.doGet(raceUrl);
		List<String> raceUrlList = new ArrayList<String>();
		try {
			//carreras-programa-ver?id_carrera=190886
			//<a href="carreras-programa-ver?id_carrera=190886">
			Matcher m = oCommonMethod.getMatcherStrGroup(body, "<a href=\"(carreras-programa-ver\\?id_carrera=.*?)\">");
			while(m.find()){
				String oneRaceUrl = m.group(1);
				if(oneRaceUrl.indexOf("class")>-1) continue;
				raceUrlList.add(oneRaceUrl);
			}
		} catch (Exception e) {
			logger.info("",e);
		}
		return raceUrlList;
	}
	//http://elturf.com/elturfcom/carreras-proximos-programas?id_pais_programa=1
	private List<String>  getHistoryPage() {
//		String raceDate ="2017-3-28";
		//http://elturf.com/elturfcom/carreras-buscar-programas?id_pais_programa=&fecha_reunion=2018-1-19
											//http://elturf.com/elturfcom/carreras-proximos-programas?id_pais_programa=1
		List<String> raceUrlList = new ArrayList<String>();
		try {
		String  uruRaceUrl ="http://elturf.com/elturfcom/carreras-proximos-programas?id_pais_programa=18";
		String uruBody = pageHelper.doGet(uruRaceUrl);
			//carreras-programa-ver?id_carrera=190886
			//<a href="carreras-programa-ver?id_carrera=190886">
			Matcher m = oCommonMethod.getMatcherStrGroup(uruBody, "<a href=\"(carreras-programa-ver\\?id_carrera=.*?)\">");
			while(m.find()){
				String oneRaceUrl = m.group(1);
				if(oneRaceUrl.indexOf("class")>-1) continue;
				raceUrlList.add(oneRaceUrl);
			}
			String  CHIraceUrl ="http://elturf.com/elturfcom/carreras-proximos-programas?id_pais_programa=1";
			String CHIbody = pageHelper.doGet(CHIraceUrl);
			Matcher m1 = oCommonMethod.getMatcherStrGroup(CHIbody, "<a href=\"(carreras-programa-ver\\?id_carrera=.*?)\">");
			while(m1.find()){
				String oneRaceUrl = m1.group(1);
				if(oneRaceUrl.indexOf("class")>-1) continue;
				raceUrlList.add(oneRaceUrl);
			}
		} catch (Exception e) {
			logger.info("",e);
		}
		return raceUrlList;
	}

	
	private boolean login() {
		boolean flag = true;
		//http://elturf.com/elturfcom/dashboard-login-in
		String loginurl="http://elturf.com/elturfcom/dashboard-login-in";
//		pageHelper = testProxy(loginurl);
		
		String refer = "http://elturf.com/elturfcom/dashboard-login?msg_dash=2";
		try {
			List<NameValuePair> nvp =  new ArrayList<NameValuePair>();
			String  userName ="939755738@qq.com";
			String  passWord ="123456";
//			String  userName ="820129856@qq.com";
//			String  passWord ="123456";
			/*
			 * form_contacto_usuario:820129856@qq.com
			 * form_contacto_passwd:123456
			 * */
			nvp.add(new BasicNameValuePair("form_contacto_usuario", userName));
			nvp.add(new BasicNameValuePair("form_contacto_passwd",passWord));
			pageHelper.doPost(loginurl, refer, nvp);
			String homeUrl = "http://www.elturf.com/elturfcom/home";
			String body = pageHelper.doGet(homeUrl);
//			String str = oCommonMethod.getValueByPatter(body, "<strong>("+userName+")</strong>");
			String str = oCommonMethod.getValueByPatter(body, "<span class=\"glyphicon glyphicon-user\">\\s*</span>\\s*(.*?)\\s*</a>");
//			String str = oCommonMethod.getValueByPatter(body, "<strong>(939755738@qq.com)</strong>");
//			String str = oCommonMethod.getValueByPatter(body, "(820129856)");
			if(str.length()>1){
				logger.info("============================login Success !!!!====================");
				return flag;
			} else {
				flag =false ;
				return flag;
			}
		} catch (Exception e) {
			logger.error("",e);
			return false;
		}
	}
	public static void main(String[] args) {
		PreRaceBll p = new PreRaceBll();
//		p.parsePage("F:\\Denis\\elturf\\Test\\20180120\\190887.html");
		Controller.sStartDate="20190301";
		Controller.sEndDate ="20190301";
//		p.run();
//		p.parse();
//		String arr ="1,2,3,4,5,6,7,8,9,4755,7250,15718,20700,23052,24482,50691,58624,66993,67805,97601";
//		for(int i=0;i<arr.split(",").length;i++){
//			p.fixRaceByraceId(arr.split(",")[i]+"");
//		}
		p.login();
		p.fixRaceByraceId("217340");
//		 List<String> list = oCommonDB.getfixRaceDate("select distinct raceid  from UruguayDB..Elturf_Prerace_Race A  where  prize =0 ");	
//			for(int i=0;i<list.size();i++){
//				String raceId =list.get(i);
//				p.fixRaceByraceId(raceId);
//			}
	}
}
