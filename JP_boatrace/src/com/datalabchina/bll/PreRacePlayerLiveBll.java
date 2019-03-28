package com.datalabchina.bll;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
//import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.junit.Test;

import com.datalabchina.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;
import com.datalabchina.common.PageHelper;
//import com.datalabchina.common.RacePool;
import com.datalabchina.common.RacePool;

public class PreRacePlayerLiveBll implements Runnable{
	private static Logger logger = Logger.getLogger(PreRacePlayerLiveBll.class.getName());
	private PageHelper pageHelper = PageHelper.getPageHelper();
	private static CommonDB oCommonDB =new CommonDB();
	private CommonMethod oCommonMethod = new CommonMethod();
	private Hashtable<String,String> ht =new Hashtable<String,String>();
	public DateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
//	public boolean isUseTraead = true;
	public boolean isUseTraead = false;
	//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170516 //某一天所有比赛的链接 但是只包含post  的数据 
	//http://www.boatrace.jp/owpc/pc/race/pay 当天所有比赛信息的链接  
	//http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517 访问每场比赛的链接 
	@Override
	public void run(){
		RacePool rp = new RacePool();
		String startDate = Controller.sStartDate;
		String endDate = Controller.sEndDate;
		Vector<String> vDate = CommonMethod.getBetwenneDate(startDate, endDate);
		try {
			for(int i =0;i<vDate.size();i++) {
				String raceDate =vDate.get(i);
				if(isUseTraead){
					int threadNum = 3;
					for(int j=1;j<=threadNum;j++)
					{
						Thread thread = new Thread( new ParsePageThread("TaskThread-"+j,rp));
						thread.start();
					}
					this.getPreRaceUrl(raceDate,rp);
					for(int k=0;k<threadNum;k++)
					{
						rp.AddID("exit");
					}
				}else{
					this.ParsePre(raceDate);
				}
			}
		}catch (Exception e){
			logger.error("",e);
			for(int i=0;i<3;i++)
			{
				rp.AddID("exit");
			}
		}
	}
	
	public PreRacePlayerLiveBll()
	{
		ht.put("１", "1");
		ht.put("２", "2");
		ht.put("３", "3");
		ht.put("４", "4");
		ht.put("５", "5");
		ht.put("６", "6");
	}
	@Test
	public void test(){
		/*
		 * 2017100402009,2017100901602,2017102101509,
			2017102800708,2017103000304,2017113000304,
			2017120800402,2017120802107,2017121201107,2017121201604
		 * */
		PreRacePlayerLiveBll p = new PreRacePlayerLiveBll();
		p.parsePreByRaceID("2017100402009");
	}
	
	public void parseToday(){
		try {
			//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170516
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay";
			String basicBody = pageHelper.doGet(basicUrl);
			//<a href="/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517">11:01</a>
			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/racelist(.*?)\">(.*?)</a>");
			//?rno=1&jcd=02&hd=20170517
			while(m.find())
			{
				String url = m.group(1);
				String startTime = m.group(2);
				String raceDate = oCommonMethod.getValueByPatter(url, "(\\d{8})");
				String raceNo = oCommonMethod.getValueByPatter(url, "rno=(\\d{1,2})&");
				String trackId = oCommonMethod.getValueByPatter(url, "jcd=(\\d{1,2})&");
				String body= pageHelper.doGet(url);
				String fileName=Controller.sSaveFilePath+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+"_"+startTime+".html";
				FileDispose.saveFile(body, fileName);
				parse(fileName);
			}
		}catch (Exception e){
			logger.error("",e);
		}
	}
	
	public void readLocalFileDir(String sPathName){
		File fFile = new File(sPathName);
		File fPathFileName[] = fFile.listFiles();
		int iFileSize = fPathFileName.length;
		for (int i = 0; i < iFileSize; i++){
			File file = fPathFileName[i];
			if(!file.isDirectory()){
				String fileName =file.getPath();
				parse(fileName);
			}else{
				String filePath = file.getPath();
				readLocalFileDir(filePath);
			}
		}
	}
	
	
//http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517
//	private void getMainPage(String sraceDate,RacePool rp){
	public void ParsePre(String sraceDate){
			Vector<String> vUrl = getPreRaceUrl(sraceDate);
			logger.info("VUrl =================" +vUrl.size());
	//		String basicUrl = "http://www.boatrace.jp/owpc/pc/race/racelist";
			try {
				//?rno=1&jcd=02&hd=20170517_11:26
				for(int i=0;i<vUrl.size();i++){
					String mainUrl =vUrl.get(i);
					parse(mainUrl);
				}
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	//?rno=1&jcd=02&hd=20170517_11:26
	private void parse(String url) {
//		String startTime= url.split("_")[1].replace(":", "").replaceAll("\\s", "");
		String startTime= url.split("_")[1].replaceAll("\\s", "");
		String raceDate = oCommonMethod.getValueByPatter(url, "(\\d{8})").replaceAll("\\s", "");
		String raceNo = oCommonMethod.getValueByPatter(url, "rno=(\\d{1,2})&").replaceAll("\\s", "");
		String trackId = oCommonMethod.getValueByPatter(url, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
		try {
			 while(raceNo.length()<2)raceNo= "0"+raceNo; 
			 while(trackId.length()<3)trackId= "0"+trackId; 
			String RaceID =raceDate+trackId+raceNo;
			String URaceID = raceDate+"12"+trackId+raceNo;
			parseLivePlayer(RaceID,raceNo,URaceID,startTime,trackId,raceDate);
			logger.info("=======> start to exec pr_kyotei_prerace_player_live_UpdateMaster "+URaceID);
			oCommonDB.execStoredProcedures("pr_kyotei_prerace_player_live_UpdateMaster", URaceID);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	public void parsePreByRaceID(String raceId){
		//http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517
		try {
			//2017051700501 2017072202212 2017072602310
			String raceDate = raceId.substring(0,8);
			String  trackId= raceId.substring(8,11).replaceFirst("0", "");
			String raceNo = raceId.substring(11);
			 while(raceNo.length()<2)raceNo= "0"+raceNo; 
			 while(trackId.length()<3)trackId= "0"+trackId; 
			String RaceID =raceDate+trackId+raceNo;
			String URaceID = raceDate+"12"+trackId+raceNo;
//			String url ="http://www.boatrace.jp/owpc/pc/race/racelist?rno="+raceNo+"&jcd="+trackId+"&hd="+raceDate;
//			String body= pageHelper.doGet(url);
//			parseLivePlayer(RaceID,Integer.parseInt(raceNo)+"",URaceID,null,raceId.substring(8,11).replaceFirst("0", ""),raceDate);
			parseLivePlayer(RaceID,Integer.parseInt(raceNo)+"",URaceID,null,trackId,raceDate);
		}catch (Exception e){
			logger.error("",e);
		}
	}
	
	private Hashtable<Integer ,String> getStartTime(String body) {
		Hashtable<Integer ,String> ht = new Hashtable<Integer ,String>();
		try {
			String tbody = oCommonMethod.getValueByPatter(body, "<div class=\"table1\">\\s*<table>.*?<tbody>(.*?)</tbody>");
			Parser  trParser = Parser.createParser(tbody, null);
			 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
			 for(int i=0;i<trNodes.length;i++){
				 String value = trNodes[i].toHtml();
				  Parser tdParser = Parser.createParser(value, null);
		          Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
		          if(tdNodes.length==13){
		        	  for(int j=1;j<tdNodes.length;j++){
		        		  String startTime = tdNodes[j].toPlainTextString();
		        		  ht.put(j,startTime);
		        	  }
		          }
		     }
		} catch (Exception e) {
			logger.error("",e);
		}
		return ht;
	}

	//http://www.boatrace.jp/owpc/pc/race/beforeinfo?rno=4&jcd=24&hd=20170720
//	private void updatePlayer(String raceNo, String trackId, String raceDate,String raceId) {
	//RaceID,RaceNo,URaceID,ScheduledStartTime,TrackId,RaceDate
	private void parseLivePlayer(String raceId,String raceNo, String uracdId,String ScheduledStartTime,String trackId, String raceDate) {
		while(raceNo.startsWith("0"))raceNo = raceNo.replaceFirst("0", "");
		if(trackId.startsWith("0"))trackId = trackId.replaceFirst("0", "");
		
		String url ="http://www.boatrace.jp/owpc/pc/race/beforeinfo?rno="+raceNo+"&jcd="+trackId+"&hd="+raceDate;
		try {
			String body = pageHelper.doGet(url);
			String fileName=Controller.sSaveFilePath+File.separator+raceDate+File.separator+"beforeInfo_trackId_"+trackId+File.separator+"raceNo_"+raceNo+".html";
			
			FileDispose.saveFile(body, fileName);
			if(ScheduledStartTime==null||ScheduledStartTime.equals("0000")){
				Hashtable<Integer, String> timeht= getStartTime(body);
				ScheduledStartTime = timeht.get(Integer.parseInt(raceNo));
			}
			
			Hashtable<String,String> tab2ht =new Hashtable<String,String>();
			String atTable= oCommonMethod.getValueByPatter(body, "<table class=\"is-w238\">(.*?)</table>");
			Matcher m1 = oCommonMethod.getMatcherStrGroup(atTable, "<tbody class=\"is-p10-0\">(.*?)</tbody>");
			while(m1.find()){
				String st = m1.group(1);
				 Parser trParser = Parser.createParser(st, null);
				 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
				 for(int i=0;i<trNodes.length;i++){
					 String trValue = trNodes[i].toHtml();
					 Parser tdParser = Parser.createParser(trValue, null);
					 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
					 //	<span class="table1_boatImage1Number is-type6">6</span>
					 String boatNo = oCommonMethod.getValueByPatter(tdNodes[0].toHtml(),"<span class=\"table1_boatImage1Number is-type\\d{1,2}\">(.*?)</span>"); 
					 String  rawST = oCommonMethod.getValueByPatter(tdNodes[0].toHtml(),"<span class=\"table1_boatImage1Time.*?>(.*?)</span>"); 
					 
					 if(boatNo.length()>0&&rawST.length()>0)
						 tab2ht.put(boatNo, rawST+"_"+(i+1));
				 }
			}
			
//			Hashtable<String,String> tab1ht =new Hashtable<String,String>();
			String playerTable= oCommonMethod.getValueByPatter(body, "<table class=\"is-w748\">(.*?)</table>");
			//<tbody class="is-fs12 is-miss">
			// <tbody class="is-fs12 ">
			Matcher m = oCommonMethod.getMatcherStrGroup(playerTable, "<tbody(.*?)>(.*?)</tbody>");
			while(m.find()){
				String  BoatNo =null, PlayerId=null, PlayerName=null, PlayerWeight=null, Adjustment=null, Tilt=null, Course=null, Start=null, Exhibition=null, 
				MadeBy=null, ST = null,RawST=null, ST_Type=null, Description=null, Scratch=null, Extracttime=null, CreateTime=null;
//				System.out.println(m.group(1));
				String isScratch = m.group(1);
				if(isScratch.indexOf("is-miss")>-1)
					Scratch ="1";
				else
					Scratch ="0";
				
				String player= m.group(2);
				 Parser trParser = Parser.createParser(player, null);
				 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
				 for(int i=0;i<trNodes.length;i++){
					 String trValue = trNodes[i].toHtml();
					 Parser tdParser = Parser.createParser(trValue, null);
					 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
					 if(tdNodes.length==10){
//						<td class="is-boatColor1 is-fs14" rowspan="4">1</td>
						 BoatNo =tdNodes[0].toPlainTextString().replaceAll("&nbsp;", ""); 
//					 	<td class="is-fs18 is-fBold" rowspan="4"><a href="/owpc/pc/data/racersearch/profile?toban=3647">伊藤　　雄二</a></td>
						 PlayerName =convertString(tdNodes[2].toPlainTextString()); 
						 PlayerId =oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"toban=(\\d{1,})\">"); 
						 //<td rowspan="2">51.2kg</td>
						 PlayerWeight =convertString(tdNodes[3].toPlainTextString().replaceAll("kg", "")); 
						 //<td rowspan="4">6.94</td>
						 Exhibition =convertString(tdNodes[4].toPlainTextString().replaceAll("&nbsp;", "")); 
						 //<td rowspan="4">0.0</td>
						 Tilt =convertString(tdNodes[5].toPlainTextString().replaceAll("&nbsp;", "")); 
						 ///</tr>\\s*<tr>\\s*<td rowspan="2">0.0</td><td>ST</td>
						 Adjustment  =convertString(oCommonMethod.getValueByPatter(player, "</tr>\\s*<tr>\\s*<td rowspan=\"2\">(.*?)</td>\\s*<td>ST</td>"));
						 //<ul class="labelGroup1"><li><span class="label4 is-type1">キャブ&nbsp;</span></li>
						 //<span class="label4 is-type1">キャブ&nbsp;</span>
						 MadeBy =convertString(tdNodes[6].toPlainTextString().replaceAll("&nbsp;", "")); 
						 Description =oCommonMethod.getValueByPatter(tdNodes[7].toHtml(),"<span class=\"label4 is-type1\">(.*?)</span>").replaceAll("&nbsp;", ""); 
						 if(Description.length()<1)
							 Description = null;
						 if(tab2ht.get(BoatNo)!=null){
							 RawST = tab2ht.get(BoatNo).split("_")[0];
							 Course = tab2ht.get(BoatNo).split("_")[1];
						 }else{
							 RawST = null;
							 Course = null;
						 }
						 if(RawST!=null){
							 Start = convertString(oCommonMethod.getValueByPatter(RawST,"(\\.\\d{1,})"));
							 ST_Type =convertString(RawST.replaceAll("\\d", "").replace(".", ""));
							 ST =Start; 
						 }
						 Matcher m11 =  oCommonMethod.getMatcherStrGroup(tdNodes[7].toHtml(), "<li>\\s*<span class=\"label\\d* is-type\\d*\">(.*?)</span>\\s*</li>");
						 while(m11.find()){
							 String  Equipment = m11.group(1).replaceAll("&nbsp;", "");
							 String noofChange = "1";
							 if(m11.group(1).split("×").length==2){
								 Equipment =m11.group(1).split("×")[0];
								 noofChange= ht.get(m11.group(1).split("×")[1].replaceAll("\\s", ""));
							 }
							 saveEquipmentToDB(raceId,PlayerId,Equipment,noofChange);
						 }
						 
						  Extracttime = oCommonMethod.getCurrentTime();
				          CreateTime = Extracttime;
						   saveBoatPlayerLiveToDB(uracdId, raceId, raceNo, BoatNo, PlayerId, PlayerName, 
		      					   PlayerWeight, Adjustment, Tilt, Course, Start, Exhibition, MadeBy, 
		      					   ST, RawST, ST_Type, Description, Scratch, Extracttime, ScheduledStartTime, CreateTime);
					 }
				 }
			}
			//解析 pre 的weather 数据 
			parseLiveWeather(raceId,trackId,raceDate,raceNo,body);
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}

		private void parseLiveWeather(String raceId, String trackId, String raceDate, String raceNo,String body) {
			String  TrackName =null, Weather =null, Wave =null, 
			WindDirection =null, WindSpeed =null, Temperature =null, 
			WaterTemp =null, FlowSpeed =null, WaterLine =null, TideUP =null, TideDown =null, ExtractTime,WindDesc,Compass_Direction;
			try {
				TrackName = oCommonMethod.getValueByPatter(body, "width=\"129\" height=\"45\" alt=\"(.*?)\">");
				 //<p class="weather1_bodyUnitImage is-weather1"></p><div class="weather1_bodyUnitLabel"><span class="weather1_bodyUnitLabelTitle">晴</span>
				//<p class="weather1_bodyUnitImage is-weather3"></p><!-- 天候画像 --><div class="weather1_bodyUnitLabel"><span class="weather1_bodyUnitLabelTitle">雨</span>
 
				Weather = oCommonMethod.getValueByPatter(body, "<p class=\"weather\\d{1,2}_bodyUnitImage is-weather\\d{1,2}\">\\s*</p>.*?\\s*<div class=\"weather\\d{1,2}_bodyUnitLabel\">" +
				 																											"\\s*<span class=\"weather1_bodyUnitLabelTitle\">(.*?)</span>").replace("&nbsp;", "");
//				 <span class="weather1_bodyUnitLabelTitle">波高</span><span class="weather1_bodyUnitLabelData">5cm</span>
				 Wave = oCommonMethod.getValueByPatter(body, "波高</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("cm","").replace("&nbsp;", "");
//				 <p class="weather1_bodyUnitImage is-wind5"></p> 

				 //<span class="weather1_bodyUnitLabelTitle">風速</span><span class="weather1_bodyUnitLabelData"> 6m</span>
				 WindSpeed	= oCommonMethod.getValueByPatter(body, "風速</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("m", "").replace("&nbsp;", "");
//				 <span class="weather1_bodyUnitLabelTitle">気温</span><span class="weather1_bodyUnitLabelData">30.0℃</span>
				 Temperature	= oCommonMethod.getValueByPatter(body, "気温</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("℃", "").replace("&nbsp;", "");
//				 <span class="weather1_bodyUnitLabelTitle">水温</span><span class="weather1_bodyUnitLabelData">29.0℃</span>
				 WaterTemp	= oCommonMethod.getValueByPatter(body, "水温</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("℃", "").replace("&nbsp;", "");
				 ExtractTime = oCommonMethod.getCurrentTime();
				 //<th>決まり手</th></tr></thead><tbody><tr><td class="is-fs16">逃げ</td>
//				 Remark = 
				 WindDesc = oCommonMethod.getValueByPatter(body, "<div class=\"weather1_bodyUnit is-windDirection\">\\s*<p class=\"weather1_bodyUnitImage is-(.*?)\">\\s*</p>");
				 //<div class="weather1_bodyUnit is-direction"><p class="weather1_bodyUnitImage is-direction11"></p>
				 Compass_Direction = oCommonMethod.getValueByPatter(body, "<div class=\"weather1_bodyUnit is-direction\">\\s*<p class=\"weather1_bodyUnitImage (.*?)\">\\s*</p>");
				 
				 saveBoatRaceWeatherToDB(raceId, trackId, TrackName, raceDate, raceNo, 
						 Weather, Wave, WindDirection, WindSpeed, Temperature, WaterTemp, FlowSpeed, WaterLine, TideUP, TideDown, ExtractTime,WindDesc,Compass_Direction);
				
			} catch (Exception e) {
				logger.error("",e);
			}
		}

		public void saveBoatRaceWeatherToDB(String RaceID, String TrackID, String TrackName, String RaceDate,String  RaceNo, String Weather, String Wave, 
				String WindDirection,String  WindSpeed, String Temperature,String  WaterTemp, String FlowSpeed, String WaterLine,String  TideUP, 
				String TideDown, String ExtractTime,String WindDesc,String Compass_Direction){
			try {
				String sSql ="";
				sSql+=RaceID+",";
				sSql+=TrackID==null?"NULL,":"N'"+TrackID+"',";
				sSql+=TrackName==null?"NULL,":"N'"+TrackName+"',";
				sSql+=RaceDate==null?"NULL,":"N'"+RaceDate+"',";
				sSql+=RaceNo==null?"NULL,":"N'"+RaceNo+"',";
				sSql+=convertString(Weather)==null?"NULL,":"N'"+Weather+"',";
				sSql+=convertString(Wave)==null?"NULL,":"N'"+Wave+"',";
				sSql+=convertString(WindDirection)==null?"NULL,":"N'"+WindDirection+"',";
				sSql+=convertString(WindSpeed)==null?"NULL,":"N'"+WindSpeed+"',";
				sSql+=convertString(Temperature)==null?"NULL,":"N'"+Temperature+"',";
				sSql+=convertString(WaterTemp)==null?"NULL,":"N'"+WaterTemp+"',";
				sSql+=convertString(FlowSpeed)==null?"NULL,":"N'"+FlowSpeed+"',";
				sSql+=convertString(WaterLine)==null?"NULL,":"N'"+WaterLine+"',";
				sSql+=convertString(TideUP)==null?"NULL,":"N'"+TideUP+"',";
				sSql+=convertString(TideDown)==null?"NULL,":"N'"+TideDown+"',";
				sSql+=ExtractTime==null?"NULL,":"N'"+ExtractTime+"',";
				sSql+=WindDesc==null?"NULL,":"N'"+WindDesc+"',";
				sSql+=Compass_Direction==null?"NULL":"N'"+Compass_Direction+"'";
				
				logger.info("pr_BoatRace_PreRace_Live_Weather_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_PreRace_Live_Weather_InsertData ", sSql);
			} catch (Exception e) {
				logger.error("",e);
			}
		}	

		
		private void saveEquipmentToDB(String raceID, String playerId, String equipment,String noofChange) {
			try {
				String sSql ="";
				sSql+=raceID+",";
				sSql+=playerId==null?"NULL,":"N'"+playerId+"',";
				sSql+=equipment==null?"NULL,":"N'"+equipment+"',";
				sSql+=noofChange==null?"NULL,":"N'"+noofChange+"',";
				sSql+=oCommonMethod.getCurrentTime()==null?"NULL":"N'"+oCommonMethod.getCurrentTime()+"'";
				logger.info("pr_BoatRace_Player_EquipmentReplacement_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_Player_EquipmentReplacement_InsertData ", sSql);
			} catch (Exception e) {
				logger.error("",e);
			}
		}
		
	private void getPreRaceUrl(String raceDate,RacePool rp) {
//		Vector<String>  v= new Vector<String>();
		try {
			//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170516
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay?hd="+raceDate;
			String basicBody = pageHelper.doGet(basicUrl);
			
			Date curTime = new Date();
			DateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHH:mm");
			
			//<a href="/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517">11:01</a>
			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/racelist(.*?)\">(.*?)</a>");
			//?rno=1&jcd=02&hd=20170517
			while(m.find())
			{
				String url = m.group(1);
				String startTime = m.group(2).replaceAll("\\s", "");
				// 对开赛时间进行一个过滤 
				Date raceStartTime = CommonMethod.DateSubHour(yyyyMMddHHmm.parse(raceDate+startTime),-1);
				if(curTime.after(CommonMethod.DateSubMinute(raceStartTime,-40))&&curTime.before(CommonMethod.DateSubMinute(raceStartTime,15)))
				{
					String id = "LivePre_"+url+"_"+startTime;
					rp.AddID(id);
				}
			}
		} catch (Exception e){
			logger.error("",e);
		}
	}
	
	private Vector<String> getPreRaceUrl(String raceDate) {
		Vector<String>  v= new Vector<String>();
		try {
			//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170516
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay?hd="+raceDate;
			String basicBody = pageHelper.doGet(basicUrl);
			
//			Date curTime = new Date();
//			DateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHH:mm");
			
			//<a href="/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517">11:01</a>
			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/racelist(.*?)\">(.*?)</a>");
			//?rno=1&jcd=02&hd=20170517
			while(m.find())
			{
				String url = m.group(1);
				String startTime = m.group(2).replaceAll("\\s", "");
				// 对开赛时间进行一个过滤 
//				Date raceStartTime = CommonMethod.DateSubHour(yyyyMMddHHmm.parse(raceDate+startTime),-1);
//				if(curTime.after(CommonMethod.DateSubMinute(raceStartTime,-40))&&curTime.before(CommonMethod.DateSubMinute(raceStartTime,15)))
//				{
					v.add(url+"_"+startTime);
//				}
			}
			
			Matcher m1 = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<span class=\"is-payout2");
			//?rno=1&jcd=02&hd=20170517
			while(m1.find()){
				String url = m1.group(1);
				String id = url+"_"+"0000";
				v.add(id);
			}
			
		} catch (Exception e){
			logger.error("",e);
			return v;
		}
		return v;
	}
	
	public static String convertString(String str){
		if(str==null||str.replaceAll("\\s", "").replaceAll("&nbsp;", "").length()<1)return null;
		else return str;
	}
	
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		PreRacePlayerLiveBll p = new PreRacePlayerLiveBll();
//		p.run();
//		List<String>  list = oCommonDB.getfixRaceID();
//		 for(int i=0;i<list.size();i++){
//			 String raceid = list.get(i); 
//			 p.parsePreByRaceID(raceid);
//		 }
		p.parsePreByRaceID("2017072701607");
//		p.parse(null);
	}
	
	
	public void saveBoatPlayerLiveToDB(String URaceID,String  RaceID,String  RaceNo, String BoatNo, String PlayerId,String  PlayerName, String PlayerWeight, String Adjustment,String  Tilt,
			String Course,String Start, String Exhibition,String  MadeBy, String ST, String RawST, String ST_Type, String Description, String Scratch, String Extracttime, String ScheduledStartTime,
			String CreateTime){
			try {
				String sSql ="";
				sSql+=URaceID==null?"NULL,":"N'"+URaceID+"',";
				sSql+=RaceID+",";
				sSql+=RaceNo==null?"NULL,":"N'"+RaceNo+"',";
				sSql+=BoatNo==null?"NULL,":"N'"+BoatNo+"',";
				sSql+=PlayerId==null?"NULL,":"N'"+PlayerId+"',";
				sSql+=PlayerName==null?"NULL,":"N'"+PlayerName+"',";
				sSql+=PlayerWeight==null?"NULL,":"N'"+PlayerWeight+"',";
				sSql+=Adjustment==null?"NULL,":"N'"+Adjustment+"',";
				sSql+=Tilt==null?"NULL,":"N'"+Tilt+"',";
				sSql+=Course==null?"NULL,":"N'"+Course+"',";
				sSql+=Start==null?"NULL,":"N'"+Start+"',";
				sSql+=Exhibition==null?"NULL,":"N'"+Exhibition+"',";
				sSql+=MadeBy==null?"NULL,":"N'"+MadeBy+"',";
				sSql+=ST==null?"NULL,":"N'"+ST+"',";
				sSql+=RawST==null?"NULL,":"N'"+RawST+"',";
				sSql+=ST_Type==null?"NULL,":"N'"+ST_Type+"',";
				sSql+=Description==null?"NULL,":"N'"+Description+"',";
				sSql+=Scratch==null?"0,":"N'"+Scratch+"',";
				sSql+=Extracttime==null?"NULL,":"N'"+Extracttime+"',";
				sSql+=ScheduledStartTime==null?"NULL,":"N'"+ScheduledStartTime+"',";
				sSql+=CreateTime==null?"NULL":"N'"+CreateTime+"'";
				
				logger.info("pr_BoatRace_PreRace_Player_Live_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_PreRace_Player_Live_InsertData ", sSql);
			} catch (Exception e) {
				logger.error("",e);
			}
	}
}	
