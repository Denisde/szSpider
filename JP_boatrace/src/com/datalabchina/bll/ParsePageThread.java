package com.datalabchina.bll;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;

import com.datalabchina.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;
import com.datalabchina.common.PageHelper;
import com.datalabchina.common.RacePool;

public class ParsePageThread implements Runnable{
//	private static final String ScheduledStartTime = null;
	private static Logger logger = Logger.getLogger(ParsePageThread.class.getName());
	private CommonDB oCommonDB =new CommonDB();
	private PageHelper pageHelper = PageHelper.getPageHelper();
	private CommonMethod oCommonMethod = new CommonMethod();
	private Hashtable<String,String> ht =new Hashtable<String,String>();
	
	static Hashtable<String,String> Dataht =new Hashtable<String,String>();
	
	DateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private DateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	private	String _threadName = null;
	private Hashtable<String,String> htBetType = null;
	RacePool _rp = null;
	@Override
	public void run(){
		try {
			String id = _rp.GetID();
			while (id==null || !id.equals("exit")) {
				if(id!=null){
					logger.info(_threadName + " >>>>>>>>>>>>>>>>>>> start parse " + id);
				}
				if(id==null){
					logger.info(_threadName + " sleep 10s");
					Thread.sleep(10*1000);
				} 
				else if(id.startsWith("Pre")){
					this.parsePre(id);
				}
				else if (id.startsWith("Post")){
					this.parsePost(id);
				}
				else if (id.startsWith("LiveOdds"))
				{
					this.parseLiveOdds(id);
				}
				else if (id.startsWith("LivePost")){
					this.parseLivePost(id);
				}
				else if (id.startsWith("LivePre")){
					this.parseLivePre(id);
				}
				else if (id.startsWith("FinalOdds_")){
					this.parseLiveFinalOdds(id);
				}
				id = _rp.GetID();
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void parseLivePre(String id) {
		//String id = "LivePre_"+url+"_"+startTime;
		try {
			String startTime= id.split("_")[2].replaceAll("\\s", "");
			String raceDate = oCommonMethod.getValueByPatter(id, "(\\d{8})").replaceAll("\\s", "");
			String raceNo = oCommonMethod.getValueByPatter(id, "rno=(\\d{1,2})&").replaceAll("\\s", "");
			String trackId = oCommonMethod.getValueByPatter(id, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
			 while(raceNo.length()<2)raceNo= "0"+raceNo; 
			 while(trackId.length()<3)trackId= "0"+trackId; 
			String RaceID =raceDate+trackId+raceNo;
			String URaceID = raceDate+"12"+trackId+raceNo;
			parseLivePlayer(RaceID,raceNo,URaceID,startTime,trackId,raceDate);
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void parseLivePlayer(String raceId,String raceNo, String uracdId,String ScheduledStartTime,String trackId, String raceDate) {
		String  BoatNo =null, PlayerId=null, PlayerName=null, PlayerWeight=null, Adjustment=null, Tilt=null, Course=null, Start=null, Exhibition=null, 
		MadeBy=null, ST = null,RawST=null, ST_Type=null, Description=null, Scratch=null, Extracttime=null, CreateTime=null;
		
		while(raceNo.startsWith("0"))raceNo = raceNo.replaceFirst("0", "");
		if(trackId.startsWith("0"))trackId = trackId.replaceFirst("0", "");
		String url ="http://www.boatrace.jp/owpc/pc/race/beforeinfo?rno="+raceNo+"&jcd="+trackId+"&hd="+raceDate;
		
		try {
			String body = pageHelper.doGet(url);
			if(ScheduledStartTime==null||"0000".equals(ScheduledStartTime)){
				Hashtable<Integer, String> timeht= getStartTime(body);
				ScheduledStartTime = timeht.get(Integer.parseInt(raceNo));
			}
			Hashtable<String,String> tab2ht =new Hashtable<String,String>();
			String atTable= oCommonMethod.getValueByPatter(body, "<table class=\"is-w238\">(.*?)</table>");
			Matcher rawstMatcher = oCommonMethod.getMatcherStrGroup(atTable, "<tbody class=\"is-p10-0\">(.*?)</tbody>");
			while(rawstMatcher.find()){
				String st = rawstMatcher.group(1);
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
			
	//		Hashtable<String,String> tab1ht =new Hashtable<String,String>();
			String playerTable= oCommonMethod.getValueByPatter(body, "<table class=\"is-w748\">(.*?)</table>");
			Matcher playerMatcher = oCommonMethod.getMatcherStrGroup(playerTable, "<tbody(.*?)>(.*?)</tbody>");
			while(playerMatcher.find()){
				String isScratch = playerMatcher.group(1);
				if(isScratch.indexOf("is-miss")>-1)
					Scratch ="1";
				else
					Scratch ="0";
				String player= playerMatcher.group(2);
				 Parser trParser = Parser.createParser(player, null);
				 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
				 for(int i=0;i<trNodes.length;i++){
					 String trValue = trNodes[i].toHtml();
					 Parser tdParser = Parser.createParser(trValue, null);
					 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
					 if(tdNodes.length==10){
	//					<td class="is-boatColor1 is-fs14" rowspan="4">1</td>
						 BoatNo =tdNodes[0].toPlainTextString().replaceAll("&nbsp;", ""); 
	//				 	<td class="is-fs18 is-fBold" rowspan="4"><a href="/owpc/pc/data/racersearch/profile?toban=3647">伊藤　　雄二</a></td>
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
							 Start = oCommonMethod.getValueByPatter(RawST,"(\\.\\d{1,})");
							 ST_Type =RawST.replaceAll("\\d", "").replace(".", "");
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
	
	private void parseLiveFinalOdds(String id) {
		try {
			
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	public   ParsePageThread(){
		ht.put("１", "1");
		ht.put("２", "2");
		ht.put("３", "3");
		ht.put("４", "4");
		ht.put("５", "5");
		ht.put("６", "6");
	
		htBetType = new Hashtable<String,String>();
		htBetType.put("Win", "1");
		htBetType.put("Place", "2");
		htBetType.put("Exacta", "3");
		htBetType.put("Quinella", "4");
		htBetType.put("Tierce", "5");
		htBetType.put("Trio", "6");
		htBetType.put("QuinellaPlace", "7");
	}
	
	public ParsePageThread(String threadName ,RacePool rp){
		ht.put("１", "1");
		ht.put("２", "2");
		ht.put("３", "3");
		ht.put("４", "4");
		ht.put("５", "5");
		ht.put("６", "6");
		this._rp = rp;
		_threadName = threadName;
		
		htBetType = new Hashtable<String,String>();
		htBetType.put("Win", "1");
		htBetType.put("Place", "2");
		htBetType.put("Exacta", "3");
		htBetType.put("Quinella", "4");
		htBetType.put("Tierce", "5");
		htBetType.put("Trio", "6");
		htBetType.put("QuinellaPlace", "7");
	}
	
	
	private void parseLivePost(String id) {
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/raceresult";
			try {
//				LivePost_?rno=1&jcd=04&hd=20170727
//				http://www.boatrace.jp/owpc/pc/race/raceresult?rno=5&jcd=01&hd=20170721
				String mainUrl =basicUrl+id.split("_")[1];
//				String startTime= vUrl.get(i).split("_")[1].replace(":", "").replaceAll("\\s", "");
				String raceDate = oCommonMethod.getValueByPatter(mainUrl, "(\\d{8})").replaceAll("\\s", "");
				String raceNo = oCommonMethod.getValueByPatter(mainUrl, "rno=(\\d{1,2})&").replaceAll("\\s", "");
				String trackId = oCommonMethod.getValueByPatter(mainUrl, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
				String body= pageHelper.doGet(mainUrl);
				parseLivePostFile(body,raceDate,raceNo,trackId);
				parseLiveWeather(body,raceDate,raceNo,trackId);
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void parseLiveWeather(String body,String RaceDate,String RaceNo,String TrackID) {
//		fileName = "D:\\Denis\\Jpboat\\test\\20170720\\trackId_11\\raceNo_3.html";
//		String body = FileDispose.readFile(fileName);
		String RaceID =null, TrackName =null, Weather =null, Wave =null, 
		WindDirection =null, WindSpeed =null, Temperature =null, WaterTemp =null, FlowSpeed =null, 
		WaterLine =null, TideUP =null, TideDown =null, ExtractTime,WindDesc=null,Compass_Direction=null;
		if(body.length()<100) {logger.error("raceBody  is empty please check !!!!!!!!!!!!!!!!!!");return;}	
		try {
//			RaceDate = oCommonMethod.getValueByPatter(fileName, "(\\d{8})");
//			RaceNo = oCommonMethod.getValueByPatter(fileName, "raceNo_(\\d{1,2})");
//			TrackID = oCommonMethod.getValueByPatter(fileName, "trackId_(\\d{1,2})");
			 while(RaceNo.length()<2)RaceNo= "0"+RaceNo; 
			 while(TrackID.length()<3)TrackID= "0"+TrackID; 
			 RaceID =RaceDate+TrackID+RaceNo;

			TrackName = oCommonMethod.getValueByPatter(body, "width=\"129\" height=\"45\" alt=\"(.*?)\">");
			 //<p class="weather1_bodyUnitImage is-weather1"></p><div class="weather1_bodyUnitLabel"><span class="weather1_bodyUnitLabelTitle">晴</span>
			 Weather = oCommonMethod.getValueByPatter(body, "<p class=\"weather\\d{1,2}_bodyUnitImage is-weather\\d{1,2}\">\\s*</p>\\s*<div class=\"weather\\d{1,2}_bodyUnitLabel\">" +
			 																											"\\s*<span class=\"weather1_bodyUnitLabelTitle\">(.*?)</span>").replace("&nbsp;", "");
//			 <span class="weather1_bodyUnitLabelTitle">波高</span><span class="weather1_bodyUnitLabelData">5cm</span>
			 Wave = oCommonMethod.getValueByPatter(body, "波高</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("cm","").replace("&nbsp;", "");
//			 <p class="weather1_bodyUnitImage is-wind5"></p> 

//			 WindDirection = oCommonMethod.getValueByPatter(body, "width=\"129\" height=\"45\" alt=\"(.*?)\">");
			 WindDesc = oCommonMethod.getValueByPatter(body, "<div class=\"weather1_bodyUnit is-windDirection\">\\s*<p class=\"weather1_bodyUnitImage is-(.*?)\">\\s*</p>");
			 //<div class="weather1_bodyUnit is-direction"><p class="weather1_bodyUnitImage is-direction11"></p>
			 Compass_Direction = oCommonMethod.getValueByPatter(body, "<div class=\"weather1_bodyUnit is-direction\">\\s*<p class=\"weather1_bodyUnitImage (.*?)\">\\s*</p>");
			 
			 //<span class="weather1_bodyUnitLabelTitle">風速</span><span class="weather1_bodyUnitLabelData"> 6m</span>
			 WindSpeed	= oCommonMethod.getValueByPatter(body, "風速</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("m", "").replace("&nbsp;", "");
//			 <span class="weather1_bodyUnitLabelTitle">気温</span><span class="weather1_bodyUnitLabelData">30.0℃</span>
			 Temperature	= oCommonMethod.getValueByPatter(body, "気温</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("℃", "").replace("&nbsp;", "");
//			 <span class="weather1_bodyUnitLabelTitle">水温</span><span class="weather1_bodyUnitLabelData">29.0℃</span>
			 WaterTemp	= oCommonMethod.getValueByPatter(body, "水温</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("℃", "").replace("&nbsp;", "");
			 ExtractTime = oCommonMethod.getCurrentTime();
			 //<th>決まり手</th></tr></thead><tbody><tr><td class="is-fs16">逃げ</td>
//			 Remark = 
			 
			 saveBoatRaceWeatherToDB(RaceID, TrackID, TrackName, RaceDate, RaceNo, Weather, 
					 Wave, WindDirection, WindSpeed, Temperature, WaterTemp, FlowSpeed, WaterLine, TideUP, TideDown,
					 ExtractTime,WindDesc,Compass_Direction);
			 
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	public void saveBoatRaceWeatherToDB(String RaceID, String TrackID, String TrackName, String RaceDate,String  RaceNo, String Weather, String Wave, 
			String WindDirection,String  WindSpeed, String Temperature,String  WaterTemp,
			String FlowSpeed, String WaterLine,String  TideUP, String TideDown, String ExtractTime,String WindDesc,String Compass_Direction){
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
			
			logger.info("pr_BoatRace_PostRace_LiveResult_Weather_InsertData " + sSql);
			oCommonDB.execStoredProcedures("pr_BoatRace_PostRace_LiveResult_Weather_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
	}	
	
	private void parseLivePostFile(String body,String RaceDate,String RaceNo,String TrackID) {
//		fileName = "D:\\Denis\\Jpboat\\test\\20170720\\trackId_11\\raceNo_3.html";
		String RaceID=null,URaceID=null, ExtractTime=null;
//		String body = FileDispose.readFile(fileName);
		if(body.length()<100) {logger.error("raceBody  is empty please check !!!!!!!!!!!!!!!!!!");return;}	
		try {
//			RaceDate = oCommonMethod.getValueByPatter(fileName, "(\\d{8})");
//			RaceNo = oCommonMethod.getValueByPatter(fileName, "raceNo_(\\d{1,2})");
//			TrackID = oCommonMethod.getValueByPatter(fileName, "trackId_(\\d{1,2})");
			 while(RaceNo.length()<2)RaceNo= "0"+RaceNo; 
			 while(TrackID.length()<3)TrackID= "0"+TrackID; 
			 RaceID =RaceDate+TrackID+RaceNo;
			 URaceID = RaceDate+"12"+TrackID+RaceNo;

			 ExtractTime = oCommonMethod.getCurrentTime();
			 //==========================> Start parse player 
			 String playertable = oCommonMethod.getValueByPatter(body, "<table class=\"is-w495\">.*?<thead>\\s*<tr class=\"is-fs14\">\\s*<th>着</th>(.*?)</table>");
			 
			 Hashtable<String ,String> Actual_STht =  getActual_ST(body);
			 Matcher m = oCommonMethod.getMatcherStrGroup(playertable, "<tbody>(.*?)</tbody>");
			 while(m.find()){
				 String  PlayerID=null, PlayerName=null, BoatNo=null, FinishPosition=null, 
				 RawFinishPosition=null, FinishTime=null, Actual_ST=null, StartPosition=null, Scratch=null;
				 String onePlayer =  m.group(1);
				 Parser tdParser = Parser.createParser(onePlayer, null);
		         Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
//		         <td class="is-p20-0"><span class="is-fs12">4283</span><span class="is-fs18 is-fBold">石井　　裕美</span></td>
		         PlayerID = oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"<span class=\"is-fs12\">(.*?)</span>");
		         PlayerName = oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"<span class=\"is-fs18 is-fBold\">(.*?)</span>");
		         BoatNo =tdNodes[1].toPlainTextString();
		         RawFinishPosition = tdNodes[0].toPlainTextString().replaceAll("\\s*","");
		         FinishPosition = ht.get(RawFinishPosition);
		         if(FinishPosition==null)Scratch ="1";
		         //if(FinishPosition==null)continue;
		         //<td>1&#39;55"8</td>
		         String rawFinishTime =  tdNodes[3].toPlainTextString().replaceAll("\\s*","");
		         FinishTime =  getFinishTime(rawFinishTime);
		         if(Actual_STht.get(BoatNo)!=null){
		        	 Actual_ST = Actual_STht.get(BoatNo).split("_")[0];
		        	 StartPosition = Actual_STht.get(BoatNo).split("_")[1];
		         }
		         savePostLiveToDB(RaceID, RaceNo, PlayerID, PlayerName, BoatNo, FinishPosition, RawFinishPosition, FinishTime, Actual_ST,
		        		 ExtractTime, URaceID, StartPosition, Scratch);
			 }	
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	public void savePostLiveToDB(String RaceID, String RaceNo, String PlayerID,String  PlayerName, String BoatNo, String FinishPosition,String  RawFinishPosition,
			String FinishTime,String  Actual_ST,String  ExtractTime,String  URaceID, String StartPosition,String Scratch){
			try {
				String sSql ="";
				sSql+=RaceID+",";
				sSql+=RaceNo==null?"NULL,":"N'"+RaceNo+"',";
				sSql+=PlayerID==null?"NULL,":"N'"+PlayerID+"',";
				sSql+=PlayerName==null?"NULL,":"N'"+PlayerName+"',";
				sSql+=BoatNo==null?"NULL,":"N'"+BoatNo+"',";
				sSql+=FinishPosition==null?"NULL,":"N'"+FinishPosition+"',";
				sSql+=RawFinishPosition==null?"NULL,":"N'"+RawFinishPosition+"',";
				sSql+=FinishTime==null?"NULL,":"N'"+FinishTime+"',";
				sSql+=Actual_ST==null?"NULL,":"N'"+Actual_ST+"',";
				sSql+=ExtractTime==null?"NULL,":"N'"+ExtractTime+"',";
				sSql+=URaceID==null?"NULL,":"N'"+URaceID+"',";
				sSql+=StartPosition==null?"NULL,":"N'"+StartPosition+"',";
				sSql+=Scratch==null?"0":"N'"+Scratch+"'";
				
				logger.info("pr_BoatRace_PostRace_LiveResult_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_PostRace_LiveResult_InsertData ", sSql);
			} catch (Exception e) {
				logger.error("",e);
			}
	}	

	
	
	private void parseLiveOdds(String id) {
		String basicUrl = "http://www.boatrace.jp/owpc/pc/race";
		try {
			//?rno=1&jcd=02&hd=20170517
			String oddsTypeArr [] ={"odds2tf","oddsk","oddstf","odds3t","odds3f"};
			for(int j=0;j<oddsTypeArr.length;j++){
				String oddsType = oddsTypeArr[j];
//					if(vUrl.get(i).split("_")[1].indexOf("Final")>-1)
//						isFinal =true;
				String mainUrl =basicUrl+"/"+oddsType+id.split("_")[1];
				String raceStartTime =id.split("_")[2];
//				String mainUrl =basicUrl+"/"+oddsType+"?rno=8&jcd=07&hd=20170725";
				this.parseLiveOddsFile( mainUrl,oddsType,raceStartTime);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void parseLiveOddsFile(String mainUrl, String oddsType,String StartTime) {
		try {
			String raceDate = oCommonMethod.getValueByPatter(mainUrl, "(\\d{8})").replaceAll("\\s", "");
			String raceNo = oCommonMethod.getValueByPatter(mainUrl, "rno=(\\d{1,2})&").replaceAll("\\s", "");
			String trackId = oCommonMethod.getValueByPatter(mainUrl, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
			String body= pageHelper.doGet(mainUrl);
			String fileName=Controller.sSaveFilePath+File.separator+raceDate.substring(0, 4)+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+"_"+oddsType+".html";
			FileDispose.saveFile(body, fileName);
			
			 while(raceNo.length()<2)raceNo= "0"+raceNo; 
			 while(trackId.length()<3)trackId= "0"+trackId; 
			 String RaceID =raceDate+trackId+raceNo;
			 //<p class="tab4_refreshText">オッズ更新時間 16:00</p>
			 //<p class="tab4_refreshText">オッズ更新時間 13:36</p>
			 String rawTimeStemp = oCommonMethod.getValueByPatter(body, "<p class=\"tab4_refreshText\">.*?(\\d{1,2}:\\d{1,2}).*?</p>");
			 boolean isFinal=false;
			 String extractTime =oCommonMethod.getCurrentTime();
			 Date localTime = CommonMethod.DateSubHour(yyyyMMddHHmm.parse(extractTime),1);
			 
//			 Date curTime = new Date();
//			 DateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHH:mm");
//			 Date dStartTime = CommonMethod.DateSubHour(yyyyMMddHHmm.parse(raceDate+StartTime),-1);
//			 if(curTime.after(CommonMethod.DateSubMinute(dStartTime,-40))&&curTime.before(CommonMethod.DateSubMinute(dStartTime,15))){

//			 }
			 String timeStamp =null;
			 
			 if(rawTimeStemp.length()>1){
				 timeStamp = raceDate+" "+ rawTimeStemp;
			 }else{
				 //締切時オッズ
				 timeStamp = yyyyMMddHHmm.format(localTime);
					 isFinal =true;
			 }
			if("odds2tf".equals(oddsType)){
				//dbo.Kyotei_finalQ dbo.Kyotei_finalE  --2連単オッズ --2連複オッズ
				logger.info("===============Start Parse LiveQE ======================");
				this.parseQE(fileName,RaceID,timeStamp,extractTime,isFinal);
			}else if("oddsk".equals(oddsType)){
				//dbo.Kyotei_finalQW  --拡連複
				logger.info("===============Start Parse LiveQW ======================");
				this.parseQW(fileName,RaceID,timeStamp,extractTime,isFinal);
			}else if("oddstf".equals(oddsType)){
				//dbo.Kyotei_finalP  dbo.Kyotei_finalW  --単勝オッズ --複勝オッズ
				logger.info("===============Start Parse LivePW ======================");
				this.parsePW(fileName,RaceID,timeStamp,extractTime,isFinal);
			}else if("odds3t".equals(oddsType)){
				//dbo.Kyotei_finalTi  --3連単
				logger.info("===============Start Parse LiveTi ======================");
				this.parseTi(fileName,RaceID,timeStamp,extractTime,isFinal);
			}else if("odds3f".equals(oddsType)){
				//dbo.Kyotei_finalT   --3連複 
				logger.info("===============Start Parse LiveT ======================");
				this.parseT(fileName,RaceID,timeStamp,extractTime,isFinal);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void parseT(String fileName ,String raceID,String timeStamp,String extractTime,boolean isFinal) {
		try {
//			fileName = "D:\\Denis\\Jpboat\\test\\20170724\\trackId_18\\raceNo_11_odds3f.html";
			String body = FileDispose.readFile(fileName);
//			raceID = "2017072401811";
//			timeStamp =oCommonMethod.getCurrentTime();
//			extractTime =oCommonMethod.getCurrentTime();
			String IsFinal ="0";
			if(isFinal)
				IsFinal ="1";
			else 
				IsFinal ="0";
			
			String CorruptedOdds ="0";
			//3連複オッズ
			String Tbody = oCommonMethod.getValueByPatter(body, "3連複オッズ</span>.*?<div class=\"table1\">(.*?)</div>");
//			System.out.println(Ebody);
			String TTable = oCommonMethod.getValueByPatter(Tbody, "<tbody class=\"is-p3-0\">(.*?)</tbody>");
			Parser trParser = Parser.createParser(TTable, null);
			 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
			 String  H1_2_3, H1_2_4, H1_2_5, H1_2_6, H1_3_4, H1_3_5, H1_3_6, H1_4_5, H1_4_6, H1_5_6, H2_3_4, H2_3_5, H2_3_6, 
			 H2_4_5, H2_4_6, H2_5_6, H3_4_5, H3_4_6, H3_5_6, H4_5_6;
			 H1_2_3 = Parser.createParser(trNodes[0].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[2].toPlainTextString();
			 H1_2_4 = Parser.createParser(trNodes[1].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[1].toPlainTextString();
			 H1_2_5 = Parser.createParser(trNodes[2].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[1].toPlainTextString();
			 H1_2_6 = Parser.createParser(trNodes[3].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[1].toPlainTextString();
			 
			 H1_3_4 = Parser.createParser(trNodes[4].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[2].toPlainTextString();
			 H1_3_5 = Parser.createParser(trNodes[5].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[1].toPlainTextString();
			 H1_3_6 = Parser.createParser(trNodes[6].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[1].toPlainTextString();

			 H1_4_5 = Parser.createParser(trNodes[7].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[2].toPlainTextString();
			 H1_4_6 = Parser.createParser(trNodes[8].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[1].toPlainTextString();
			 
			 H1_5_6 = Parser.createParser(trNodes[9].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[2].toPlainTextString();
			 
			 H2_3_4 = Parser.createParser(trNodes[4].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[5].toPlainTextString();
			 H2_3_5 = Parser.createParser(trNodes[5].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[3].toPlainTextString();
			 H2_3_6 = Parser.createParser(trNodes[6].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[3].toPlainTextString();
			 
			 H2_4_5 = Parser.createParser(trNodes[7].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[5].toPlainTextString();
			 H2_4_6 = Parser.createParser(trNodes[8].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[3].toPlainTextString();

			 H2_5_6 = Parser.createParser(trNodes[9].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[5].toPlainTextString();
			 
			 H3_4_5 = Parser.createParser(trNodes[7].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[8].toPlainTextString();
			 H3_4_6 = Parser.createParser(trNodes[8].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[5].toPlainTextString();
			 
			 H3_5_6 = Parser.createParser(trNodes[9].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[8].toPlainTextString();

			 H4_5_6 = Parser.createParser(trNodes[9].toHtml(), null).extractAllNodesThatAre(TableColumn.class)[11].toPlainTextString();
			 
			 saveTtoDB(raceID, timeStamp, IsFinal, H1_2_3, H1_2_4, H1_2_5, H1_2_6, H1_3_4, H1_3_5, 
					 H1_3_6, H1_4_5, H1_4_6, H1_5_6, H2_3_4, H2_3_5, H2_3_6, H2_4_5, H2_4_6, H2_5_6, 
					 H3_4_5, H3_4_6, H3_5_6, H4_5_6, extractTime,CorruptedOdds);
			 
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void saveTtoDB(String raceID, String timeStamp, String isFinal,
			String h1_2_3, String h1_2_4, String h1_2_5, String h1_2_6,
			String h1_3_4, String h1_3_5, String h1_3_6, String h1_4_5,
			String h1_4_6, String h1_5_6, String h2_3_4, String h2_3_5,
			String h2_3_6, String h2_4_5, String h2_4_6, String h2_5_6,
			String h3_4_5, String h3_4_6, String h3_5_6, String h4_5_6,
			String extractTime, String CorruptedOdds) {
		try {
			String sSql ="";
			sSql+=raceID+",";
			sSql+=timeStamp==null?"NULL,":"N'"+timeStamp+"',";
			sSql+=isFinal==null?"NULL,":"N'"+isFinal+"',";
			sSql+=convertString(h1_2_3)==null?"NULL,":"N'"+h1_2_3+"',";
			sSql+=convertString(h1_2_4)==null?"NULL,":"N'"+h1_2_4+"',";
			sSql+=convertString(h1_2_5)==null?"NULL,":"N'"+h1_2_5+"',";
			sSql+=convertString(h1_2_6)==null?"NULL,":"N'"+h1_2_6+"',";
			sSql+=convertString(h1_3_4)==null?"NULL,":"N'"+h1_3_4+"',";
			sSql+=convertString(h1_3_5)==null?"NULL,":"N'"+h1_3_5+"',";
			sSql+=convertString(h1_3_6)==null?"NULL,":"N'"+h1_3_6+"',";
			sSql+=convertString(h1_4_5)==null?"NULL,":"N'"+h1_4_5+"',";
			sSql+=convertString(h1_4_6)==null?"NULL,":"N'"+h1_4_6+"',";
			sSql+=convertString(h1_5_6)==null?"NULL,":"N'"+h1_5_6+"',";
			
			sSql+=convertString(h2_3_4)==null?"NULL,":"N'"+h2_3_4+"',";
			sSql+=convertString(h2_3_5)==null?"NULL,":"N'"+h2_3_5+"',";
			sSql+=convertString(h2_3_6)==null?"NULL,":"N'"+h2_3_6+"',";
			sSql+=convertString(h2_4_5)==null?"NULL,":"N'"+h2_4_5+"',";
			sSql+=convertString(h2_4_6)==null?"NULL,":"N'"+h2_4_6+"',";
			sSql+=convertString(h2_5_6)==null?"NULL,":"N'"+h2_5_6+"',";
			
			sSql+=convertString(h3_4_5)==null?"NULL,":"N'"+h3_4_5+"',";
			sSql+=convertString(h3_4_6)==null?"NULL,":"N'"+h3_4_6+"',";
			
			sSql+=convertString(h3_5_6)==null?"NULL,":"N'"+h3_5_6+"',";

			sSql+=convertString(h4_5_6)==null?"NULL,":"N'"+h4_5_6+"',";
			
//			sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
//			sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
			String key  ="LiveT"+raceID+isFinal; 
			String value = sSql.replace(timeStamp, "");
			if(Dataht.get(key)==null||!Dataht.get(key).equals(value)){
				Dataht.put(key, value);
				sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
				sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
				if(sSql.indexOf("NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL")<0){
					logger.info("pr_BoatRace_LiveT_InsertData " + sSql);
					oCommonDB.execStoredProcedures("pr_BoatRace_LiveT_InsertData ", sSql);	
				}
			}else{
				logger.info("======T odds not change !!!!!!!!!!!!!!!!==============");
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	public static String  convertString(String str){
		if(str==null||str.replaceAll("\\s", "").replaceAll("&nbsp;", "").length()<1)return null;
		if(str.indexOf("欠場")>-1)return null;
		return str.trim().replaceAll("&nbsp;", "");
	}

	private void parseTi(String fileName ,String raceID,String timeStamp,String extractTime,boolean isFinal) {
		try {
//			fileName = "D:\\Denis\\Jpboat\\test\\20170724\\trackId_02\\raceNo_1_odds3t.html";
			String body = FileDispose.readFile(fileName);
//			raceID = "2017072400201";
//			timeStamp =oCommonMethod.getCurrentTime();
//			extractTime =oCommonMethod.getCurrentTime();
			String IsFinal ="0";
			if(isFinal)
				IsFinal ="1";
			else 
				IsFinal ="0";
			String CorruptedOdds ="0";
			//3連複オッズ
			String Tibody = oCommonMethod.getValueByPatter(body, "3連単オッズ</span>.*?<div class=\"table1\">(.*?)</div>");
//			System.out.println(Ebody);
			String Tiable = oCommonMethod.getValueByPatter(Tibody, "<tbody class=\"is-p3-0\">(.*?)</tbody>");
			Hashtable<String,String> Tiht = new Hashtable<String,String>();
			Parser trParser = Parser.createParser(Tiable, null);
			Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
			for(int i=0;i<trNodes.length;i++){
				 String trValue = trNodes[i].toHtml();
				 Parser tdParser = Parser.createParser(trValue, null);
				 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
//				 System.out.println("i = "+i);
				 
				 if(tdNodes.length==18){
					 int clothNo =1;
					 Matcher m = oCommonMethod.getMatcherStrGroup(trValue, "<td class=\"is-fs14 is-boatColor\\d*.*?>(\\d{1})</td>\\s*<td class=\"is-boatColor\\d*\">(\\d{1})</td>\\s*<td class=\"oddsPoint.*?\">(.*?)</td>");
					 while(m.find()){
						 String key = clothNo +"_H_"+m.group(1)+"_"+m.group(2);
						 String value = m.group(3);
//						 System.out.println(key+"---->"+value);
						 if(value.indexOf("欠場")<=-1)
							 Tiht.put(key, value);
						 clothNo++;
					 }
				 }else{
					 int clothNo =1;
					 Matcher m = oCommonMethod.getMatcherStrGroup(trValue, "<td class=\"is-boatColor(\\d{1})\">(\\d{1})</td>\\s*<td class=\"oddsPoint.*?\">(.*?)</td>");
					 while(m.find()){
						 String key = clothNo +"_H_"+m.group(1)+"_"+m.group(2);
						 String value = m.group(3);
//						 System.out.println(key+"---->"+value);
						 if(value.indexOf("欠場")<=-1)
							 Tiht.put(key, value);
						 clothNo++;
					 }
				 }
			}
			//去除重复操作 
//			String key  ="Ti"+raceID+isFinal; 
//			if(Dataht.get(key)==null||!Dataht.get(key).equals(timeStamp)){
//				Dataht.put(key, timeStamp);
			
			boolean isNull = isAllNull(Tiht);
			if(isNull)
				saveTiToDB(raceID, timeStamp, IsFinal,Tiht,extractTime,CorruptedOdds);
			else{
				logger.info(Tiht);
				logger.info("The tiodds is all  null ");
			}
//			saveTiToDB(raceID, timeStamp, IsFinal,Tiht,extractTime,CorruptedOdds);
//			}else{
//				logger.info("=============Ti Odds not change =============");
//			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void saveTiToDB(String raceId, String timeStamp, String isFinal, Hashtable<String, String> Hashtable,String extractTime,String corruptedOdds) {
		try {
			String str ="";
			for(int i=1;i<=6;i++){
				String clothNo = i+"";
				String sSql ="";
				sSql+="N'"+raceId+"',";
				sSql+=timeStamp==null?"NULL,":"N'"+timeStamp+"',";
				sSql+=isFinal==null?"NULL,":"N'"+isFinal+"',";
				sSql+=clothNo==null?"NULL,":"N'"+clothNo+"',";
				sSql+=Hashtable.get(clothNo+"_H_1_1")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_1_1")+"',";
				sSql+=Hashtable.get(clothNo+"_H_1_2")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_1_2")+"',";
				sSql+=Hashtable.get(clothNo+"_H_1_3")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_1_3")+"',";
				sSql+=Hashtable.get(clothNo+"_H_1_4")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_1_4")+"',";
				sSql+=Hashtable.get(clothNo+"_H_1_5")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_1_5")+"',";
				sSql+=Hashtable.get(clothNo+"_H_1_6")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_1_6")+"',";
				
				sSql+=Hashtable.get(clothNo+"_H_2_1")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_2_1")+"',";
				sSql+=Hashtable.get(clothNo+"_H_2_2")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_2_2")+"',";
				sSql+=Hashtable.get(clothNo+"_H_2_3")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_2_3")+"',";
				sSql+=Hashtable.get(clothNo+"_H_2_4")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_2_4")+"',";
				sSql+=Hashtable.get(clothNo+"_H_2_5")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_2_5")+"',";
				sSql+=Hashtable.get(clothNo+"_H_2_6")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_2_6")+"',";
				
				sSql+=Hashtable.get(clothNo+"_H_3_1")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_3_1")+"',";
				sSql+=Hashtable.get(clothNo+"_H_3_2")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_3_2")+"',";
				sSql+=Hashtable.get(clothNo+"_H_3_3")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_3_3")+"',";
				sSql+=Hashtable.get(clothNo+"_H_3_4")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_3_4")+"',";
				sSql+=Hashtable.get(clothNo+"_H_3_5")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_3_5")+"',";
				sSql+=Hashtable.get(clothNo+"_H_3_6")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_3_6")+"',";
				
				sSql+=Hashtable.get(clothNo+"_H_4_1")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_4_1")+"',";
				sSql+=Hashtable.get(clothNo+"_H_4_2")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_4_2")+"',";
				sSql+=Hashtable.get(clothNo+"_H_4_3")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_4_3")+"',";
				sSql+=Hashtable.get(clothNo+"_H_4_4")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_4_4")+"',";
				sSql+=Hashtable.get(clothNo+"_H_4_5")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_4_5")+"',";
				sSql+=Hashtable.get(clothNo+"_H_4_6")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_4_6")+"',";
				
				sSql+=Hashtable.get(clothNo+"_H_5_1")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_5_1")+"',";
				sSql+=Hashtable.get(clothNo+"_H_5_2")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_5_2")+"',";
				sSql+=Hashtable.get(clothNo+"_H_5_3")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_5_3")+"',";
				sSql+=Hashtable.get(clothNo+"_H_5_4")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_5_4")+"',";
				sSql+=Hashtable.get(clothNo+"_H_5_5")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_5_5")+"',";
				sSql+=Hashtable.get(clothNo+"_H_5_6")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_5_6")+"',";
				
				sSql+=Hashtable.get(clothNo+"_H_6_1")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_6_1")+"',";
				sSql+=Hashtable.get(clothNo+"_H_6_2")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_6_2")+"',";
				sSql+=Hashtable.get(clothNo+"_H_6_3")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_6_3")+"',";
				sSql+=Hashtable.get(clothNo+"_H_6_4")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_6_4")+"',";
				sSql+=Hashtable.get(clothNo+"_H_6_5")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_6_5")+"',";
				sSql+=Hashtable.get(clothNo+"_H_6_6")==null?"NULL,":"N'"+Hashtable.get(clothNo+"_H_6_6")+"',";
				
				sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',"+"";
				sSql+=corruptedOdds==null?"NULL":"N'"+corruptedOdds+"'";
//				sSql =sSql.replaceAll(",N'0.0',", ",NULL,");
//				logger.info("pr_Keirin_LiveTi_InsertData sql :" + sSql);
				str = str+"("+sSql+"),"+"\r\n";
			}
			String key  ="LiveTI"+raceId+isFinal; 
			String value = str.replace(timeStamp, "").replace(extractTime, "");
			if(Dataht.get(key)==null||!Dataht.get(key).equals(value)){
				Dataht.put(key, value);
				str = "  insert into BoatRace_LiveTi VALUES "+str.substring(0,str.length()-3);
				logger.info(str);
				oCommonDB.insertToDB(str);
				//pr_Consolidate_Log_TiWprob_Live
				oCommonDB.execStoredProcedures("pr_Consolidate_Log_TiWprob_Live",raceId+",'"+timeStamp+"'");
				logger.info("success exec sp:  pr_Consolidate_Log_TiWprob_Live "+raceId+",'"+timeStamp+"'");
			}else{
				logger.info("======Ti odds not change !!!!!!!!!!!!!!!!==============");
			}
			
		}catch (Exception e) {
			logger.error("",e);
		}
	}
	
	
	private void parsePW(String fileName ,String raceID,String timeStamp,String extractTime,boolean isFinal) {
		try {
//			fileName = "D:\\Denis\\Jpboat\\test\\20170724\\trackId_02\\raceNo_1_oddstf.html";
			String body = FileDispose.readFile(fileName);
			String IsFinal ="0";
			if(isFinal)
				IsFinal ="1";
			else 
				IsFinal ="0";
			String CorruptedOdds ="0";
			
			//単勝オッズ
			String Wbody = oCommonMethod.getValueByPatter(body, "単勝オッズ</span>.*?<div class=\"table1\">(.*?)</div>");
//			System.out.println(Ebody);
			Hashtable<String,String> wht = new Hashtable<String,String>();
			 Parser trParser = Parser.createParser(Wbody, null);
			 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
			 for(int i=1;i<trNodes.length;i++){
				 String trValue = trNodes[i].toHtml();
				 Parser tdParser = Parser.createParser(trValue, null);
				 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
				 String key = "w_"+tdNodes[0].toPlainTextString();
				 String value = tdNodes[2].toPlainTextString();
				 if(value.indexOf("欠場")<=-1)
					 wht.put(key, value);
			 }
			 
			 if(isAllNull(wht))
				 saveWtoDB(raceID,timeStamp,IsFinal,wht,extractTime,CorruptedOdds);
			else{
				logger.info(wht);
				logger.info("The wht is all  null ");
		   }
//			 saveWtoDB(raceID,timeStamp,IsFinal,wht,extractTime,CorruptedOdds);
			 
			 String Pbody = oCommonMethod.getValueByPatter(body, "複勝オッズ</span>.*?<div class=\"table1\">(.*?)</div>");
				Hashtable<String,String> pht = new Hashtable<String,String>();
				 Parser trParser1 = Parser.createParser(Pbody, null);
				 Node[] trNodes1 = trParser1.extractAllNodesThatAre(TableRow.class);
				 for(int i=1;i<trNodes1.length;i++){
					 String trValue = trNodes1[i].toHtml();
					 Parser tdParser = Parser.createParser(trValue, null);
					 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
					 String key = "p_"+tdNodes[0].toPlainTextString();
					 String value = tdNodes[2].toPlainTextString();
					 if(value.indexOf("欠場")<=-1)
						 pht.put(key, value);
				 }
				 if(isAllNull(pht))
					 	savePtoDB(raceID,timeStamp,IsFinal,pht,extractTime,CorruptedOdds);
					else{
						logger.info(pht);
						logger.info("The pht is all  null ");
				   }
//				savePtoDB(raceID,timeStamp,IsFinal,pht,extractTime,CorruptedOdds);
			 
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void savePtoDB(String raceID, String timeStamp,String isFinal,
			Hashtable<String, String> pht, String extractTime,String CorruptedOdds) {
		String sSql ="";
		sSql+=raceID+",";
		sSql+=timeStamp==null?"NULL,":"N'"+timeStamp+"',";
		sSql+=isFinal==null?"NULL,":"N'"+isFinal+"',";
		sSql+=pht.get("p_1")==null?"NULL,":"N'"+pht.get("p_1").split("-")[0]+"',";
		sSql+=pht.get("p_1")==null?"NULL,":"N'"+pht.get("p_1").split("-")[1]+"',";
		sSql+=pht.get("p_2")==null?"NULL,":"N'"+pht.get("p_2").split("-")[0]+"',";
		sSql+=pht.get("p_2")==null?"NULL,":"N'"+pht.get("p_2").split("-")[1]+"',";
		sSql+=pht.get("p_3")==null?"NULL,":"N'"+pht.get("p_3").split("-")[0]+"',";
		sSql+=pht.get("p_3")==null?"NULL,":"N'"+pht.get("p_3").split("-")[1]+"',";
		sSql+=pht.get("p_4")==null?"NULL,":"N'"+pht.get("p_4").split("-")[0]+"',";
		sSql+=pht.get("p_4")==null?"NULL,":"N'"+pht.get("p_4").split("-")[1]+"',";
		sSql+=pht.get("p_5")==null?"NULL,":"N'"+pht.get("p_5").split("-")[0]+"',";
		sSql+=pht.get("p_5")==null?"NULL,":"N'"+pht.get("p_5").split("-")[1]+"',";
		sSql+=pht.get("p_6")==null?"NULL,":"N'"+pht.get("p_6").split("-")[0]+"',";
		sSql+=pht.get("p_6")==null?"NULL,":"N'"+pht.get("p_6").split("-")[1]+"',";
		
		String key  ="LiveP"+raceID+isFinal; 
		String value = sSql.replace(timeStamp, "");
		if(Dataht.get(key)==null||!Dataht.get(key).equals(value)){
			Dataht.put(key, value);
			sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
			sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
		
			logger.info("pr_BoatRace_LiveP_InsertData " + sSql);
			oCommonDB.execStoredProcedures("pr_BoatRace_LiveP_InsertData ", sSql);	
		}else{
			logger.info("======P odds not change !!!!!!!!!!!!!!!!==============");
		}
	}

	private void saveWtoDB(String raceID, String timeStamp,String isFinal,Hashtable<String, String> wht, String extractTime,String CorruptedOdds ) {
		try {
			String sSql ="";
			sSql+=raceID+",";
			sSql+=timeStamp==null?"NULL,":"N'"+timeStamp+"',";
			sSql+=isFinal==null?"NULL,":"N'"+isFinal+"',";
			sSql+=wht.get("w_1")==null?"NULL,":"N'"+wht.get("w_1")+"',";
			sSql+=wht.get("w_2")==null?"NULL,":"N'"+wht.get("w_2")+"',";
			sSql+=wht.get("w_3")==null?"NULL,":"N'"+wht.get("w_3")+"',";
			sSql+=wht.get("w_4")==null?"NULL,":"N'"+wht.get("w_4")+"',";
			sSql+=wht.get("w_5")==null?"NULL,":"N'"+wht.get("w_5")+"',";
			sSql+=wht.get("w_6")==null?"NULL,":"N'"+wht.get("w_6")+"',";
			
	//		sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
	//		sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
			String key  ="LiveW"+raceID+isFinal; 
			String value = sSql.replace(timeStamp, "");
			if(Dataht.get(key)==null||!Dataht.get(key).equals(value)){
				Dataht.put(key, value);
				sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
				sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
				logger.info("pr_BoatRace_LiveW_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_LiveW_InsertData ", sSql);	
			}else{
				logger.info("======W odds not change !!!!!!!!!!!!!!!!==============");
			}
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}
		
		

	private void parseQW(String fileName ,String raceID,String timeStamp,String extractTime,boolean isFinal) {
		try {
//				fileName = "D:\\Denis\\Jpboat\\test\\20170724\\trackId_18\\raceNo_11_oddsk.html";
				String body = FileDispose.readFile(fileName);
//				raceID = "2017072401811";
//				timeStamp =oCommonMethod.getCurrentTime();
//				extractTime =oCommonMethod.getCurrentTime();
				String IsFinal ="0";
				if(isFinal)
					IsFinal ="1";
				else 
					IsFinal ="0";
				String CorruptedOdds ="0";
				
				String QWbody = oCommonMethod.getValueByPatter(body, "拡連複オッズ</span>.*?<div class=\"table1\">(.*?)</div>");
//				System.out.println(Ebody);
				String QWTable = oCommonMethod.getValueByPatter(QWbody, "<tbody class=\"is-p3-0\">(.*?)</tbody>");
				Hashtable<String,String> qwht = new Hashtable<String,String>();
				 Parser trParser = Parser.createParser(QWTable, null);
				 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
				 for(int i=0;i<trNodes.length;i++){
					 String trValue = trNodes[i].toHtml();
					 Parser tdParser = Parser.createParser(trValue, null);
					 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
					 int index = 0;
					 for(int j=0;j<tdNodes.length;j++){
						 if(tdNodes[j].toHtml().indexOf("oddsPoint")<1){
							 continue;
						 }else{
							 String key = "e_"+(j-index)+"_"+(i+2);
							 String value =tdNodes[j].toPlainTextString(); 
//							 System.out.println(key +"===="+value);
							 if(value.indexOf("欠場")<=-1)
								 qwht.put(key, value);
							 index++;
						 }
					 }
				 }
				 if(isAllNull(qwht))
					 saveQWtoDB(raceID,timeStamp,IsFinal,qwht,extractTime,CorruptedOdds);
				else{
					logger.info(qwht);
					logger.info("The qwht is all  null ");
			   }
//				 saveQWtoDB(raceID,timeStamp,IsFinal,qwht,extractTime,CorruptedOdds);
				 
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void saveQWtoDB(String raceID, String timeStamp,String IsFinal,Hashtable<String, String> qwht, String extractTime,String CorruptedOdds) {

		try {
			String sSql ="";
			sSql+=raceID+",";
			sSql+=timeStamp==null?"NULL,":"N'"+timeStamp+"',";
			sSql+=IsFinal==null?"NULL,":"N'"+IsFinal+"',";
			sSql+=qwht.get("e_1_2")==null?"NULL,":"N'"+qwht.get("e_1_2").split("-")[0]+"',";
			sSql+=qwht.get("e_1_2")==null?"NULL,":"N'"+qwht.get("e_1_2").split("-")[1]+"',";
			sSql+=qwht.get("e_1_3")==null?"NULL,":"N'"+qwht.get("e_1_3").split("-")[0]+"',";
			sSql+=qwht.get("e_1_3")==null?"NULL,":"N'"+qwht.get("e_1_3").split("-")[1]+"',";
			sSql+=qwht.get("e_1_4")==null?"NULL,":"N'"+qwht.get("e_1_4").split("-")[0]+"',";
			sSql+=qwht.get("e_1_4")==null?"NULL,":"N'"+qwht.get("e_1_4").split("-")[1]+"',";
			sSql+=qwht.get("e_1_5")==null?"NULL,":"N'"+qwht.get("e_1_5").split("-")[0]+"',";
			sSql+=qwht.get("e_1_5")==null?"NULL,":"N'"+qwht.get("e_1_5").split("-")[1]+"',";
			sSql+=qwht.get("e_1_6")==null?"NULL,":"N'"+qwht.get("e_1_6").split("-")[0]+"',";
			sSql+=qwht.get("e_1_6")==null?"NULL,":"N'"+qwht.get("e_1_6").split("-")[1]+"',";
			
			sSql+=qwht.get("e_2_3")==null?"NULL,":"N'"+qwht.get("e_2_3").split("-")[0]+"',";
			sSql+=qwht.get("e_2_3")==null?"NULL,":"N'"+qwht.get("e_2_3").split("-")[1]+"',";
			sSql+=qwht.get("e_2_4")==null?"NULL,":"N'"+qwht.get("e_2_4").split("-")[0]+"',";
			sSql+=qwht.get("e_2_4")==null?"NULL,":"N'"+qwht.get("e_2_4").split("-")[1]+"',";
			sSql+=qwht.get("e_2_5")==null?"NULL,":"N'"+qwht.get("e_2_5").split("-")[0]+"',";
			sSql+=qwht.get("e_2_5")==null?"NULL,":"N'"+qwht.get("e_2_5").split("-")[1]+"',";
			sSql+=qwht.get("e_2_6")==null?"NULL,":"N'"+qwht.get("e_2_6").split("-")[0]+"',";
			sSql+=qwht.get("e_2_6")==null?"NULL,":"N'"+qwht.get("e_2_6").split("-")[1]+"',";
			
			sSql+=qwht.get("e_3_4")==null?"NULL,":"N'"+qwht.get("e_3_4").split("-")[0]+"',";
			sSql+=qwht.get("e_3_4")==null?"NULL,":"N'"+qwht.get("e_3_4").split("-")[1]+"',";
			sSql+=qwht.get("e_3_5")==null?"NULL,":"N'"+qwht.get("e_3_5").split("-")[0]+"',";
			sSql+=qwht.get("e_3_5")==null?"NULL,":"N'"+qwht.get("e_3_5").split("-")[1]+"',";
			sSql+=qwht.get("e_3_6")==null?"NULL,":"N'"+qwht.get("e_3_6").split("-")[0]+"',";
			sSql+=qwht.get("e_3_6")==null?"NULL,":"N'"+qwht.get("e_3_6").split("-")[1]+"',";
			
			sSql+=qwht.get("e_4_5")==null?"NULL,":"N'"+qwht.get("e_4_5").split("-")[0]+"',";
			sSql+=qwht.get("e_4_5")==null?"NULL,":"N'"+qwht.get("e_4_5").split("-")[1]+"',";
			sSql+=qwht.get("e_4_6")==null?"NULL,":"N'"+qwht.get("e_4_6").split("-")[0]+"',";
			sSql+=qwht.get("e_4_6")==null?"NULL,":"N'"+qwht.get("e_4_6").split("-")[1]+"',";
			
			sSql+=qwht.get("e_5_6")==null?"NULL,":"N'"+qwht.get("e_5_6").split("-")[0]+"',";
			sSql+=qwht.get("e_5_6")==null?"NULL,":"N'"+qwht.get("e_5_6").split("-")[1]+"',";
			
//			sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
//			sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
			
			String key  ="LiveQW"+raceID+IsFinal; 
			String value = sSql.replace(timeStamp, "");
			if(Dataht.get(key)==null||!Dataht.get(key).equals(value)){
				Dataht.put(key, value);
				sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
				sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
				logger.info("pr_BoatRace_LiveQW_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_LiveQW_InsertData ", sSql);	
			}else{
				logger.info("======QW odds not change !!!!!!!!!!!!!!!!==============");
			}
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void parseQE(String fileName ,String raceID,String timeStamp,String extractTime,boolean isFinal) {
		try {
//			fileName = "D:\\Denis\\Jpboat\\test\\20170724\\trackId_02\\raceNo_1_odds2tf.html";
			String body = FileDispose.readFile(fileName);
//			raceID = "2017072400201";
//			timeStamp =oCommonMethod.getCurrentTime();
//			extractTime =oCommonMethod.getCurrentTime();
			String IsFinal ="0";
			if(isFinal)
				IsFinal ="1";
			else 
				IsFinal ="0";
			
			String CorruptedOdds ="0";
			
//				<div class="table1">
			String Ebody = oCommonMethod.getValueByPatter(body, "2連単オッズ</span>.*?<div class=\"table1\">(.*?)</div>");
//			System.out.println(Ebody);
			String eTable = oCommonMethod.getValueByPatter(Ebody, "<tbody class=\"is-p3-0\">(.*?)</tbody>");
			Hashtable<String,String> eht = new Hashtable<String,String>();
			 Parser trParser = Parser.createParser(eTable, null);
			 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
			 for(int i=0;i<trNodes.length;i++){
				 String trValue = trNodes[i].toHtml();
				 Parser tdParser = Parser.createParser(trValue, null);
				 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
				 int index = 0;
				 for(int j=0;j<tdNodes.length;j++){
					 if(tdNodes[j].toHtml().indexOf("oddsPoint")<1){
						 continue;
					 }else{
						 String key = "e_"+(j-index)+"_"+(i+1);
						 String value =tdNodes[j].toPlainTextString(); 
						 if((j-index)<=(i+1)){
							 key = "e_"+(j-index)+"_"+(i+2);
						 }else{
							 key = "e_"+(j-index)+"_"+(i+1);
						 }
						 if(value.indexOf("欠場")<=-1)
							 eht.put(key, value);
						 index++;
					 }
				 }
			 }
			 if(isAllNull(eht))
					saveEtoDB(raceID, timeStamp, IsFinal,eht,extractTime,CorruptedOdds);
				else{
					logger.info(eht);
					logger.info("The eht is all  null ");
				}
//			saveEtoDB(raceID,timeStamp,IsFinal,eht,extractTime,CorruptedOdds);
			
			String Qbody = oCommonMethod.getValueByPatter(body, "2連複オッズ</span>.*?<div class=\"table1\">(.*?)</div>");
			String qTable = oCommonMethod.getValueByPatter(Qbody, "<tbody class=\"is-p3-0\">(.*?)</tbody>");
			Hashtable<String,String> qht = new Hashtable<String,String>();
			
			 Parser trParser1 = Parser.createParser(qTable, null);
			 Node[] trNodes1 = trParser1.extractAllNodesThatAre(TableRow.class);
			 for(int i=0;i<trNodes1.length;i++){
				 String trValue = trNodes1[i].toHtml();
				 Parser tdParser = Parser.createParser(trValue, null);
				 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
				 int index = 0;
				 for(int j=0;j<tdNodes.length;j++){
					 if(tdNodes[j].toHtml().indexOf("oddsPoint")<1){
						 continue;
					 }else{
						 String key = "e_"+(j-index)+"_"+(i+2);
						 String value =tdNodes[j].toPlainTextString(); 
//						 System.out.println(key +"===="+value);
						 if(value.indexOf("欠場")<=-1)
							 qht.put(key, value);
						 index++;
					 }
				 }
			}
			 if(isAllNull(qht))
				 saveQtoDB(raceID, timeStamp, IsFinal,qht,extractTime,CorruptedOdds);
				else{
					logger.info(qht);
					logger.info("The qht is all  null ");
				}
//			 saveQtoDB(raceID,timeStamp,IsFinal,qht,extractTime,CorruptedOdds);
			 
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private boolean isAllNull(Hashtable<String, String> tiht) {
		boolean flag = false;
		try {
			for(Iterator<String> iterator=tiht.keySet().iterator();iterator.hasNext();){
				String key=iterator.next();
				if(tiht.get(key)!=null||!"null".equals(tiht.get(key))){
					flag = true;
				}
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return flag;
	}
	
	private void saveQtoDB(String raceID, String timeStamp,String IsFinal,Hashtable<String, String> qht, String extractTime,String CorruptedOdds) {
		try {
			String sSql ="";
			sSql+=raceID+",";
			sSql+=timeStamp==null?"NULL,":"N'"+timeStamp+"',";
			sSql+=IsFinal==null?"NULL,":"N'"+IsFinal+"',";
			sSql+=qht.get("e_1_2")==null?"NULL,":"N'"+qht.get("e_1_2")+"',";
			sSql+=qht.get("e_1_3")==null?"NULL,":"N'"+qht.get("e_1_3")+"',";
			sSql+=qht.get("e_1_4")==null?"NULL,":"N'"+qht.get("e_1_4")+"',";
			sSql+=qht.get("e_1_5")==null?"NULL,":"N'"+qht.get("e_1_5")+"',";
			sSql+=qht.get("e_1_6")==null?"NULL,":"N'"+qht.get("e_1_6")+"',";
			
			sSql+=qht.get("e_2_3")==null?"NULL,":"N'"+qht.get("e_2_3")+"',";
			sSql+=qht.get("e_2_4")==null?"NULL,":"N'"+qht.get("e_2_4")+"',";
			sSql+=qht.get("e_2_5")==null?"NULL,":"N'"+qht.get("e_2_5")+"',";
			sSql+=qht.get("e_2_6")==null?"NULL,":"N'"+qht.get("e_2_6")+"',";
			
			sSql+=qht.get("e_3_4")==null?"NULL,":"N'"+qht.get("e_3_4")+"',";
			sSql+=qht.get("e_3_5")==null?"NULL,":"N'"+qht.get("e_3_5")+"',";
			sSql+=qht.get("e_3_6")==null?"NULL,":"N'"+qht.get("e_3_6")+"',";
			
			sSql+=qht.get("e_4_5")==null?"NULL,":"N'"+qht.get("e_4_5")+"',";
			sSql+=qht.get("e_4_6")==null?"NULL,":"N'"+qht.get("e_4_6")+"',";
			
			sSql+=qht.get("e_5_6")==null?"NULL,":"N'"+qht.get("e_5_6")+"',";
			
//			sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
//			sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
			
			String key  ="LiveQ"+raceID+IsFinal; 
			String value = sSql.replace(timeStamp, "");
			if(Dataht.get(key)==null||!Dataht.get(key).equals(value)){
				Dataht.put(key, value);
				sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
				sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
				logger.info("pr_BoatRace_LiveQ_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_LiveQ_InsertData ", sSql);	
			}else{
				logger.info("======Q odds not change !!!!!!!!!!!!!!!!==============");
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void saveEtoDB(String raceID,String timeStamp,String IsFinal,Hashtable<String, String> eht, String extractTime,String CorruptedOdds) {
		try {
			String sSql ="";
			sSql+=raceID+",";
			sSql+=timeStamp==null?"NULL,":"N'"+timeStamp+"',";
			sSql+=IsFinal==null?"NULL,":"N'"+IsFinal+"',";
			
			sSql+=eht.get("e_1_2")==null?"NULL,":"N'"+eht.get("e_1_2")+"',";
			sSql+=eht.get("e_1_3")==null?"NULL,":"N'"+eht.get("e_1_3")+"',";
			sSql+=eht.get("e_1_4")==null?"NULL,":"N'"+eht.get("e_1_4")+"',";
			sSql+=eht.get("e_1_5")==null?"NULL,":"N'"+eht.get("e_1_5")+"',";
			sSql+=eht.get("e_1_6")==null?"NULL,":"N'"+eht.get("e_1_6")+"',";
			
			sSql+=eht.get("e_2_1")==null?"NULL,":"N'"+eht.get("e_2_1")+"',";
			sSql+=eht.get("e_2_3")==null?"NULL,":"N'"+eht.get("e_2_3")+"',";
			sSql+=eht.get("e_2_4")==null?"NULL,":"N'"+eht.get("e_2_4")+"',";
			sSql+=eht.get("e_2_5")==null?"NULL,":"N'"+eht.get("e_2_5")+"',";
			sSql+=eht.get("e_2_6")==null?"NULL,":"N'"+eht.get("e_2_6")+"',";
			
			sSql+=eht.get("e_3_1")==null?"NULL,":"N'"+eht.get("e_3_1")+"',";
			sSql+=eht.get("e_3_2")==null?"NULL,":"N'"+eht.get("e_3_2")+"',";
			sSql+=eht.get("e_3_4")==null?"NULL,":"N'"+eht.get("e_3_4")+"',";
			sSql+=eht.get("e_3_5")==null?"NULL,":"N'"+eht.get("e_3_5")+"',";
			sSql+=eht.get("e_3_6")==null?"NULL,":"N'"+eht.get("e_3_6")+"',";
			
			sSql+=eht.get("e_4_1")==null?"NULL,":"N'"+eht.get("e_4_1")+"',";
			sSql+=eht.get("e_4_2")==null?"NULL,":"N'"+eht.get("e_4_2")+"',";
			sSql+=eht.get("e_4_3")==null?"NULL,":"N'"+eht.get("e_4_3")+"',";
			sSql+=eht.get("e_4_5")==null?"NULL,":"N'"+eht.get("e_4_5")+"',";
			sSql+=eht.get("e_4_6")==null?"NULL,":"N'"+eht.get("e_4_6")+"',";
			
			sSql+=eht.get("e_5_1")==null?"NULL,":"N'"+eht.get("e_5_1")+"',";
			sSql+=eht.get("e_5_2")==null?"NULL,":"N'"+eht.get("e_5_2")+"',";
			sSql+=eht.get("e_5_3")==null?"NULL,":"N'"+eht.get("e_5_3")+"',";
			sSql+=eht.get("e_5_4")==null?"NULL,":"N'"+eht.get("e_5_4")+"',";
			sSql+=eht.get("e_5_6")==null?"NULL,":"N'"+eht.get("e_5_6")+"',";
			
			sSql+=eht.get("e_6_1")==null?"NULL,":"N'"+eht.get("e_6_1")+"',";
			sSql+=eht.get("e_6_2")==null?"NULL,":"N'"+eht.get("e_6_2")+"',";
			sSql+=eht.get("e_6_3")==null?"NULL,":"N'"+eht.get("e_6_3")+"',";
			sSql+=eht.get("e_6_4")==null?"NULL,":"N'"+eht.get("e_6_4")+"',";
			sSql+=eht.get("e_6_5")==null?"NULL,":"N'"+eht.get("e_6_5")+"',";
			
			
			String key  ="LiveE"+raceID+IsFinal; 
			String value = sSql.replace(timeStamp, "");
			if(Dataht.get(key)==null||!Dataht.get(key).equals(value)){
				Dataht.put(key, value);
				sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
				sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
				logger.info("pr_BoatRace_LiveE_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_LiveE_InsertData ", sSql);	
			}else{
				logger.info("======E odds not change !!!!!!!!!!!!!!!!==============");
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void parsePre(String vUrl){
		String basicUrl = "http://www.boatrace.jp/owpc/pc/race/racelist";
		try {
			//?rno=1&jcd=02&hd=20170517_11:26
//				String mainUrl ="http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=01&hd=20170720";
				String mainUrl =basicUrl+vUrl.split("_")[1];
				String startTime= vUrl.split("_")[2].replace(":", "").replaceAll("\\s", "");
				String raceDate = oCommonMethod.getValueByPatter(mainUrl, "(\\d{8})").replaceAll("\\s", "");
				String raceNo = oCommonMethod.getValueByPatter(mainUrl, "rno=(\\d{1,2})&").replaceAll("\\s", "");
				String trackId = oCommonMethod.getValueByPatter(mainUrl, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
//				String raceId =raceDate+"_"+trackId+"_"+raceNo; 
				String body= pageHelper.doGet(mainUrl);
				String fileName=Controller.sSaveFilePath+File.separator+raceDate.substring(0,4)+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+"_"+startTime+".html";
				FileDispose.saveFile(body, fileName);
				parsePreFile(fileName);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	
	private void parsePost(String id) {
		String basicUrl = "http://www.boatrace.jp/owpc/pc/race/raceresult";
		try {
			String mainUrl =basicUrl+id.split("_")[1];
//			String startTime= vUrl.get(i).split("_")[1].replace(":", "").replaceAll("\\s", "");
			String raceDate = oCommonMethod.getValueByPatter(mainUrl, "(\\d{8})").replaceAll("\\s", "");
			String raceNo = oCommonMethod.getValueByPatter(mainUrl, "rno=(\\d{1,2})&").replaceAll("\\s", "");
			String trackId = oCommonMethod.getValueByPatter(mainUrl, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
//			String raceId =raceDate+"_"+trackId+"_"+raceNo; 
			String body= pageHelper.doGet(mainUrl);
			String fileName=Controller.sSaveFilePath+File.separator+raceDate.substring(0, 4)+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+".html";
			FileDispose.saveFile(body, fileName);
			parsePostFile(fileName);
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void parsePostFile(String fileName) {
//		fileName = "D:\\Denis\\Jpboat\\test\\20170720\\trackId_11\\raceNo_3.html";
		String RaceID=null, TrackID=null, TrackName=null, RaceTitle=null, RaceDate=null, RaceNo=null, Weather=null, Wave=null, WindDirection=null, WindSpeed=null, 
		Temperature=null,WaterTemp=null, FlowSpeed=null, WaterLine=null, TideUP=null, TideDown=null, WindDesc=null,
		ExtractTime=null, WinTactics=null, Remark=null, URaceID=null, CancelledDesc=null, Distance=null,Compass_Direction=null;
		String body = FileDispose.readFile(fileName);
		if(body.length()<100) {logger.error("raceBody  is empty please check !!!!!!!!!!!!!!!!!!");return;}	
		try {
			RaceDate = oCommonMethod.getValueByPatter(fileName, "(\\d{8})");
			RaceNo = oCommonMethod.getValueByPatter(fileName, "raceNo_(\\d{1,2})");
			TrackID = oCommonMethod.getValueByPatter(fileName, "trackId_(\\d{1,2})");
			 while(RaceNo.length()<2)RaceNo= "0"+RaceNo; 
			 while(TrackID.length()<3)TrackID= "0"+TrackID; 
			 RaceID =RaceDate+TrackID+RaceNo;
			 URaceID = RaceDate+"12"+TrackID+RaceNo;

			TrackName = oCommonMethod.getValueByPatter(body, "width=\"129\" height=\"45\" alt=\"(.*?)\">");
			 RaceTitle = oCommonMethod.getValueByPatter(body, "<h2 class=\"heading2_titleName\">(.*?)</h2>");
			 //<p class="weather1_bodyUnitImage is-weather2"></p><div class="weather1_bodyUnitLabel"><span class="weather1_bodyUnitLabelTitle">曇り</span>
			 //<p class="weather1_bodyUnitImage is-weather1"></p><div class="weather1_bodyUnitLabel"><span class="weather1_bodyUnitLabelTitle">晴</span>
			 Weather = oCommonMethod.getValueByPatter(body, "<p class=\"weather\\d{1,2}_bodyUnitImage is-weather\\d{1,2}\">\\s*</p>\\s*<div class=\"weather\\d{1,2}_bodyUnitLabel\">" +
			 																											"\\s*<span class=\"weather1_bodyUnitLabelTitle\">(.*?)</span>");
//			 <span class="weather1_bodyUnitLabelTitle">波高</span><span class="weather1_bodyUnitLabelData">5cm</span>
			 Wave = oCommonMethod.getValueByPatter(body, "波高</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("cm","");
//			 <p class="weather1_bodyUnitImage is-wind5"></p> 
//			 <p class="weather1_bodyUnitImage is-wind7"></p> 
//			 <p class="weather1_bodyUnitImage is-wind9"></p> 
			 
//			 WindDirection = oCommonMethod.getValueByPatter(body, "width=\"129\" height=\"45\" alt=\"(.*?)\">");
			 WindDesc = oCommonMethod.getValueByPatter(body, "<div class=\"weather1_bodyUnit is-windDirection\">\\s*<p class=\"weather1_bodyUnitImage is-(.*?)\">\\s*</p>");
			 
			 Compass_Direction = oCommonMethod.getValueByPatter(body, "<div class=\"weather1_bodyUnit is-direction\">\\s*<p class=\"weather1_bodyUnitImage (.*?)\">\\s*</p>");
			 
			 //<span class="weather1_bodyUnitLabelTitle">風速</span><span class="weather1_bodyUnitLabelData"> 6m</span>
			 WindSpeed	= oCommonMethod.getValueByPatter(body, "風速</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("m", "");
//			 <span class="weather1_bodyUnitLabelTitle">気温</span><span class="weather1_bodyUnitLabelData">30.0℃</span>
			 Temperature	= oCommonMethod.getValueByPatter(body, "気温</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("℃", "");
//			 <span class="weather1_bodyUnitLabelTitle">水温</span><span class="weather1_bodyUnitLabelData">29.0℃</span>
			 WaterTemp	= oCommonMethod.getValueByPatter(body, "水温</span>\\s*<span class=\"weather1_bodyUnitLabelData\">(.*?)</span>").replace("℃", "");
			 ExtractTime = oCommonMethod.getCurrentTime();
			 //<th>決まり手</th></tr></thead><tbody><tr><td class="is-fs16">逃げ</td>
			 WinTactics = oCommonMethod.getValueByPatter(body, "<th>決まり手</th>\\s*</tr>\\s*</thead>\\s*<tbody>\\s*<tr>\\s*<td class=\"is-fs16\">(.*?)</td>");
//			 Remark = 
//			 CancelledDesc=
			 Distance = oCommonMethod.getValueByPatter(body, "span class=\"heading2_titleDetail is-type1\">.*?(\\d{3,4}).*?</span>");
			 
			 if(!(Weather.indexOf("&nbsp;")>-1&&Wave.indexOf("&nbsp;")>-1&&WindSpeed.indexOf("&nbsp;")>-1&&Temperature.indexOf("&nbsp;")>-1)){
				 saveBoatRaceToDB(RaceID, TrackID, TrackName, RaceTitle, RaceDate, RaceNo, Weather, Wave, WindDirection, 
						 WindSpeed, Temperature, WaterTemp, FlowSpeed, WaterLine, TideUP, TideDown, WindDesc, ExtractTime,
						 WinTactics, Remark, URaceID, CancelledDesc, Distance,Compass_Direction);
			 }
			
			 //==========================> Start parse Dividend
			 parseDividend(body,RaceDate,RaceNo,TrackID);
			 
			 //==========================> Start parse player 
			 String playertable = oCommonMethod.getValueByPatter(body, "<table class=\"is-w495\">.*?<thead>\\s*<tr class=\"is-fs14\">\\s*<th>着</th>(.*?)</table>");
			 
			 Hashtable<String ,String> Actual_STht =  getActual_ST(body);
			 Matcher m = oCommonMethod.getMatcherStrGroup(playertable, "<tbody>(.*?)</tbody>");
			 while(m.find()){
				 String  PlayerID=null, PlayerName=null, BoatNo=null, FinishPosition=null, RawFinishPosition=null, FinishTime=null, Actual_ST=null, StartPosition=null, PlayerWeight=null;
				 String onePlayer =  m.group(1);
				 Parser tdParser = Parser.createParser(onePlayer, null);
		         Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
//		         <td class="is-p20-0"><span class="is-fs12">4283</span><span class="is-fs18 is-fBold">石井　　裕美</span></td>
		         PlayerID = oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"<span class=\"is-fs12\">(.*?)</span>");
		         PlayerName = oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"<span class=\"is-fs18 is-fBold\">(.*?)</span>");
		         BoatNo =tdNodes[1].toPlainTextString();
		         RawFinishPosition = tdNodes[0].toPlainTextString().replaceAll("\\s*","");
		         FinishPosition = ht.get(RawFinishPosition);
//		         if(FinishPosition==null)continue;
		         //<td>1&#39;55"8</td>
		         String rawFinishTime =  tdNodes[3].toPlainTextString().replaceAll("\\s*","");
		         FinishTime =  getFinishTime(rawFinishTime);
		         if(Actual_STht.get(BoatNo)!=null){
		        	 Actual_ST = Actual_STht.get(BoatNo).split("_")[0];
		        	 StartPosition = Actual_STht.get(BoatNo).split("_")[1];
		         }
		         saveBoatPlayerToDB(RaceID, RaceNo, PlayerID, PlayerName, BoatNo, FinishPosition, RawFinishPosition, FinishTime, 
		        		 Actual_ST, ExtractTime, URaceID, StartPosition, PlayerWeight);
			 }	
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void parseDividend(String body,String RaceDate,String RaceNo,String TrackID) {
//		fileName = "D:\\Denis\\Jpboat\\test\\20170720\\trackId_11\\raceNo_3.html";
//		String body = FileDispose.readFile(fileName);
		if(body==null||body.length()<100) {logger.error("raceBody  is empty please check !!!!!!!!!!!!!!!!!!");return;}	
		String RaceID=null, Uraceid=null,   ExtractTime=null;
		try {
			 while(RaceNo.length()<2)RaceNo= "0"+RaceNo; 
			 while(TrackID.length()<3)TrackID= "0"+TrackID; 
			 RaceID =RaceDate+TrackID+RaceNo;
			 Uraceid = RaceDate+"12"+TrackID+RaceNo;
			 ExtractTime = oCommonMethod.getCurrentTime();
			 
			String dividendTable = oCommonMethod.getValueByPatter(body, "<table class=\"is-w495\">.*?<th>勝式</th>(.*?)</table>");
			 Matcher m = oCommonMethod.getMatcherStrGroup(dividendTable, "<tbody>(.*?)</tbody>");
			 while(m.find()){
				 String BetTypeID=null, Combination=null, Dividend=null, Popularity=null,BetTypeName=null;
				 String dividendvalue  = m.group(1);
				 Parser trParser = Parser.createParser(dividendvalue, null);
				 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
				 for(int i=0;i<trNodes.length;i++){
					 String trValue = trNodes[i].toHtml();
					 Parser tdParser = Parser.createParser(trValue, null);
					 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
					 if(tdNodes.length== 4){
						 BetTypeName = tdNodes[0].toPlainTextString();
						 BetTypeID = getBetTypeIDByTypeName(BetTypeName);
						 
						 Combination = tdNodes[1].toPlainTextString().replaceAll("\\s", "").replace("/.numberSet1_row", "").replace("/.numberSet1", "");
						 //td><span class="is-payout1">&yen;530</span></td>
						 Dividend = tdNodes[2].toPlainTextString().replace("&yen;", "").replaceAll("[^\\d]", "");
						 //<td>1</td>
						 Popularity = tdNodes[3].toPlainTextString();
						 if(Popularity.replaceAll("\\s", "").equals("&nbsp;"))Popularity = null;

//						 System.out.println(BetTypeName+"++++++++"+Dividend+Combination+Popularity);
						 saveDividendToDB(RaceID, Uraceid, RaceDate, RaceNo, BetTypeID, Combination, Dividend, Popularity, ExtractTime, BetTypeName);
						 
					 }else if(tdNodes.length==3){
						 Combination = tdNodes[0].toPlainTextString().replaceAll("\\s", "").replace("/.numberSet1_row", "").replace("/.numberSet1", "");
						 if(Combination.replaceAll("\\s", "").equals("&nbsp;"))continue;

						 Parser trParser0 = Parser.createParser(dividendvalue, null);
						 Node[] trNodes0 = trParser0.extractAllNodesThatAre(TableRow.class);
						 String trValue0 = trNodes0[0].toHtml();
						 Parser tdParser0 = Parser.createParser(trValue0, null);
						 Node[] tdNodes0 = tdParser0.extractAllNodesThatAre(TableColumn.class);
						 BetTypeName = tdNodes0[0].toPlainTextString();
						 BetTypeID = getBetTypeIDByTypeName(BetTypeName);
						 
						 //td><span class="is-payout1">&yen;530</span></td>
						 Dividend = tdNodes[1].toPlainTextString().replace("&yen;", "").replaceAll("[^\\d]", "");
						 //<td>1</td>
						 Popularity = tdNodes[2].toPlainTextString();
						 if(Popularity.replaceAll("\\s", "").equals("&nbsp;"))Popularity = null;
//						 System.out.println(BetTypeName+"++++++++"+Dividend+Combination+Popularity);
						 saveDividendToDB(RaceID, Uraceid, RaceDate, RaceNo, BetTypeID, Combination, Dividend, Popularity, ExtractTime, BetTypeName);
					 }
				 }
			 }
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void saveDividendToDB(String raceID, String uraceid,
			String raceDate, String raceNo, String betTypeID,
			String combination, String dividend, String popularity,
			String extractTime, String betTypeName) {
		try {
			String sSql ="";
			sSql+=raceID+",";
			sSql+=uraceid==null?"NULL,":"N'"+uraceid+"',";
			sSql+=raceDate==null?"NULL,":"N'"+raceDate+"',";
			sSql+=raceNo==null?"NULL,":"N'"+raceNo+"',";
			sSql+=betTypeID==null?"NULL,":"N'"+betTypeID+"',";
			sSql+=convertString(combination)==null?"NULL,":"N'"+combination+"',";
			sSql+=convertString(dividend)==null?"NULL,":"N'"+dividend+"',";
			sSql+=convertString(popularity)==null?"NULL,":"N'"+popularity+"',";
			sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
			sSql+=betTypeName==null?"NULL":"N'"+betTypeName+"'";
			
			logger.info("pr_BoatRace_Dividend_InsertData " + sSql);
			oCommonDB.execStoredProcedures("pr_BoatRace_Dividend_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
//	public static String convertString(String str){
//		if(str==null||str.replaceAll("\\s","").trim().length()<1)return null;
//		else return str;
//	}
	
	private String getBetTypeIDByTypeName(String sBetTypeName) {
		/*
		 * 3連単++++++++5301-2-51
			3連複++++++++1801=2=51
			2連単++++++++2701-21
			2連複++++++++1101=21
			拡連複++++++++1101=21
			拡連複++++++++1501=53	
			拡連複++++++++1202=52
			単勝++++++++1301&nbsp;
			複勝++++++++1001&nbsp;
			複勝++++++++1002&nbsp;
		 * */
		//	  logger.info(sBetTypeName);
	  if(sBetTypeName.equals("単勝"))sBetTypeName="Win";
	  else if(sBetTypeName.equals("複勝"))sBetTypeName="Place";
	  else if(sBetTypeName.equals("2連単"))sBetTypeName="Exacta";
	  else if(sBetTypeName.equals("2連複"))sBetTypeName="Quinella";
	  else if(sBetTypeName.equals("3連単"))sBetTypeName="Tierce";
	  else if(sBetTypeName.equals("3連複"))sBetTypeName="Trio";
	  else if(sBetTypeName.equals("拡連複"))sBetTypeName="QuinellaPlace";
	  if(htBetType.containsKey(sBetTypeName))
		  return htBetType.get(sBetTypeName).toString();
	  else
		  return "0";		  
	}
	
	public  Hashtable<String ,String>  getActual_ST(String body){
		Hashtable<String ,String> ht = new Hashtable<String ,String>();
		try {
			String table = oCommonMethod.getValueByPatter(body, "<tbody class=\"is-p10-0\">(.*?)</tbody>");
			Parser  trParser = Parser.createParser(table, null);
			Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
			 for(int i=0;i<trNodes.length;i++){
				 String value = trNodes[i].toHtml();
				  Parser tdParser = Parser.createParser(value, null);
		          Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
		          String boatNo = oCommonMethod.getValueByPatter(tdNodes[0].toHtml(), "<span class=\"table1_boatImage1Number is-type\\d{1,2}\">(\\d{1,2})</span>");
		          String  Actual_ST = oCommonMethod.getValueByPatter(tdNodes[0].toHtml(), "<span class=\"table1_boatImage1TimeInner \">(\\.\\d{1,2})");
		          if(Actual_ST.length()<1)Actual_ST = oCommonMethod.getValueByPatter(tdNodes[0].toHtml(), "<span class=\"table1_boatImage1TimeInner is-fBold is-fColor1\">.*?(\\.\\d{1,2})");
		          //<span class="table1_boatImage1TimeInner ">.13　
		          ht.put(boatNo, Actual_ST+"_"+(i+1));
			 }	
			 return ht ; 
		} catch (Exception e) {
			logger.error("",e);
		}
		return ht ; 
	}
	
	//<td>1&#39;55"8</td>
	private String getFinishTime(String rawFinishTime) {
		try {
			if(rawFinishTime.replaceAll("\\s", "").length()<1) return null;
			if(rawFinishTime.indexOf("&#39;")>-1){
				String min =  oCommonMethod.getValueByPatter(rawFinishTime, "(\\d{1,2})&#39;");
				String sec =  oCommonMethod.getValueByPatter(rawFinishTime, "&#39;(\\d{1,2})\"");
				String ssec =  oCommonMethod.getValueByPatter(rawFinishTime, "\"(\\d{1,2})");
				return Integer.parseInt(min)*60+Integer.parseInt(sec)+"."+ssec;
			}else{
				String sec =  oCommonMethod.getValueByPatter(rawFinishTime, "(\\d{1,2})\"");
				String ssec =  oCommonMethod.getValueByPatter(rawFinishTime, "\"(\\d{1,2})");
				return Integer.parseInt(sec)+"."+ssec;
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
	public void saveBoatRaceToDB(String RaceID, String TrackID, String TrackName, String RaceTitle, String RaceDate,String  RaceNo, String Weather, String Wave,
			String  WindDirection,String WindSpeed, String Temperature,String  WaterTemp,String  FlowSpeed,String  WaterLine,String  TideUP, String TideDown, String WindDesc, 
			String ExtractTime,String WinTactics,String  Remark, String URaceID, String CancelledDesc,String  Distance,String Compass_Direction){
		try {
			String sSql ="";
			sSql+=RaceID+",";
			sSql+=TrackID==null?"NULL,":"N'"+TrackID+"',";
			sSql+=TrackName==null?"NULL,":"N'"+TrackName+"',";
			sSql+=RaceTitle==null?"NULL,":"N'"+RaceTitle+"',";
			sSql+=RaceDate==null?"NULL,":"N'"+RaceDate+"',";
			sSql+=RaceNo==null?"NULL,":"N'"+RaceNo+"',";
			sSql+=Weather==null?"NULL,":"N'"+Weather+"',";
			sSql+=Wave==null?"NULL,":"N'"+Wave+"',";
			sSql+=WindDirection==null?"NULL,":"N'"+WindDirection+"',";
			sSql+=WindSpeed==null?"NULL,":"N'"+WindSpeed+"',";
			sSql+=Temperature==null?"NULL,":"N'"+Temperature+"',";
			sSql+=WaterTemp==null?"NULL,":"N'"+WaterTemp+"',";
			sSql+=FlowSpeed==null?"NULL,":"N'"+FlowSpeed+"',";
			sSql+=WaterLine==null?"NULL,":"N'"+WaterLine+"',";
			sSql+=TideUP==null?"NULL,":"N'"+TideUP+"',";
			sSql+=TideDown==null?"NULL,":"N'"+TideDown+"',";
			sSql+=WindDesc==null?"NULL,":"N'"+WindDesc+"',";
			sSql+=ExtractTime==null?"NULL,":"N'"+ExtractTime+"',";
			sSql+=WinTactics==null?"NULL,":"N'"+WinTactics+"',";
			sSql+=Remark==null?"NULL,":"N'"+Remark+"',";
			sSql+=URaceID==null?"NULL,":"N'"+URaceID+"',";
			sSql+=CancelledDesc==null?"NULL,":"N'"+CancelledDesc+"',";
			sSql+=Distance==null?"NULL,":"N'"+Distance+"',";
			sSql+=Compass_Direction==null?"NULL":"N'"+Compass_Direction+"'";
			
			logger.info("pr_BoatRace_PostRace_Race_InsertData " + sSql);
			oCommonDB.execStoredProcedures("pr_BoatRace_PostRace_Race_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
	}	
	
	public void saveBoatPlayerToDB(String RaceID, String RaceNo, String PlayerID,String  PlayerName, String BoatNo, String FinishPosition,String  RawFinishPosition,
			String FinishTime,String  Actual_ST,String  ExtractTime,String  URaceID, String StartPosition,String PlayerWeight){
			try {
				String sSql ="";
				sSql+=RaceID+",";
				sSql+=RaceNo==null?"NULL,":"N'"+RaceNo+"',";
				sSql+=PlayerID==null?"NULL,":"N'"+PlayerID+"',";
				sSql+=PlayerName==null?"NULL,":"N'"+PlayerName+"',";
				sSql+=BoatNo==null?"NULL,":"N'"+BoatNo+"',";
				sSql+=FinishPosition==null?"NULL,":"N'"+FinishPosition+"',";
				sSql+=RawFinishPosition==null?"NULL,":"N'"+RawFinishPosition+"',";
				sSql+=FinishTime==null?"NULL,":"N'"+FinishTime+"',";
				sSql+=Actual_ST==null?"NULL,":"N'"+Actual_ST+"',";
				sSql+=ExtractTime==null?"NULL,":"N'"+ExtractTime+"',";
				sSql+=URaceID==null?"NULL,":"N'"+URaceID+"',";
				sSql+=StartPosition==null?"NULL,":"N'"+StartPosition+"',";
				sSql+=PlayerWeight==null?"NULL":"N'"+PlayerWeight+"'";
				
				logger.info("pr_BoatRace_PostRace_player_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_PostRace_player_InsertData ", sSql);
			} catch (Exception e) {
				logger.error("",e);
			}
	}	
	
	private void parsePreFile(String fileName) {
//		fileName ="D:\\Denis\\Jpboat\\test\\20170721\\trackId_03\\raceNo_4_1215.html";
		String RaceID=null, TrackId=null, Grade=null, RaceTitle=null, DaySequence=null, RacePeriod=null, RaceDate=null, 
		RaceNo=null, ExtractTime=null, ScheduledStartTime=null, TrackName=null, RaceClass=null, URaceID=null, Cancelled=null, FixedStartDesc=null,Distance = null;;
		try {
			String body = FileDispose.readFile(fileName);
			if(body.length()<100) 
				{logger.error("raceBody  is empty please check !!!!!!!!!!!!!!!!!!");return;}	
			
			 RaceDate = oCommonMethod.getValueByPatter(fileName, "(\\d{8})");
			 RaceNo = oCommonMethod.getValueByPatter(fileName, "raceNo_(\\d{1,2})");
			 TrackId = oCommonMethod.getValueByPatter(fileName, "trackId_(\\d{1,2})");
			 String startTime = oCommonMethod.getValueByPatter(fileName, "_(\\d{4}).html");
			 
			 if("0000".equals(startTime)||startTime.replaceAll("\\s", "").length()<1){
				 Hashtable<Integer ,String> startTimeht = getStartTime(body);
				 if(startTimeht!=null)
					 startTime = startTimeht.get(Integer.parseInt(RaceNo));
			 }
			 
			 if(startTime!=null&&startTime.length()>1){
				 if(startTime.indexOf(":")>-1)
					 ScheduledStartTime =startTime;
				 else
					 ScheduledStartTime = startTime.substring(0,2)+":"+startTime.substring(2);
			 }
			 
			 while(RaceNo.length()<2)RaceNo= "0"+RaceNo; 
			 while(TrackId.length()<3)TrackId= "0"+TrackId; 
			 RaceID =RaceDate+TrackId+RaceNo;
			 URaceID = RaceDate+"12"+TrackId+RaceNo;
			 //<div class="heading2_title is-ippan">
			 String raceInfo = oCommonMethod.getValueByPatter(body, "<div class=\"heading2_title.*?\">(.*?)</div>");
			 //<h2 class="heading2_titleName">第５３回日刊スポーツ賞</h2>
			 //<h2 class="heading2_titleName">Ｇ３オールレディース・第５１回日刊スポーツ杯</h2>
			 RaceTitle = oCommonMethod.getValueByPatter(raceInfo, "<h2 class=\"heading2_titleName\">(.*?)</h2>");
//			 Grade = oCommonMethod.getValueByPatter(RaceTitle, "(.*?)オ");
//			 <div class="heading2_title is-ippan">
//				<h2 class="heading2_titleName">
//			 Grade = oCommonMethod.getValueByPatter(RaceTitle, "<div class=\"(.*?)\">\\s*<h2 class=\"heading2_titleName\">");
			 //<div class="heading2_title is-ippan"><h2 class="heading2_titleName">
			 Grade = oCommonMethod.getValueByPatter(body, "<div class=\"(heading2_title.*?)\">\\s{1,}<h2 class=\"heading2_titleName\">");
					 
			 Distance = oCommonMethod.getValueByPatter(body, "<span class=\"heading2_titleDetail is-type1\">.*?(\\d{1,}).*?</span>");
			 
			 if(Grade.indexOf("heading2_title is-ippan")>-1){
				 Grade = "一般";
			 }else if(Grade.indexOf("heading2_title is-G3")>-1){
				 Grade = "ＧⅢ";
			 }else if(Grade.indexOf("heading2_title is-SG")>-1){
				 Grade = "ＳＧ";
			 }else if(Grade.indexOf("heading2_title is-G2")>-1){
				 Grade = "ＧⅡ";
			 }else if(Grade.indexOf("heading2_title is-G1")>-1){
				 Grade = "ＧⅠ";
			 }
			 
			 if(Grade.length()<1)Grade=null;
			 //<span class="heading2_titleDetail is-type1">まつりだｏｎｅ1800m</span>
			 //<span class="heading2_titleDetail is-type1">予選1800m</span>
			 RaceClass = oCommonMethod.getValueByPatter(raceInfo, "<span class=\"heading2_titleDetail.*?\">(.*?)</span>").replaceAll("\\d{4}m", "").replaceAll("\\s", "").replaceAll("<.*?>", "").replaceAll("　", "");
			 //<li class="is-active2"><span class="tab2_inner">5月17日<span>初日</span></span></li>
			 DaySequence = oCommonMethod.getValueByPatter(body, "<li class=\"is-active2\">\\s*<span class=\"tab2_inner\">.*?<span>(.*?)</span>\\s*</span></li>").trim();
			 //<span class="tab2_inner">5月17日<span>初日</span></span></li>
			 //>5月14日<span>初日</span></a></li>
//			 String startDate =oCommonMethod.getValueByPatter(body, ">(\\d{1,2}月\\d{1,2}日)<span>初日</span>");
			 String startDate =oCommonMethod.getValueByPatter(body, "tab2_tabs\">.*?>(\\d{1,2}月\\d{1,2}日)<span>.*?</span>");
//			 if(startDate==null||"".equals(startDate)) {
//				 startDate =oCommonMethod.getValueByPatter(body, ">(\\d{1,2}月\\d{1,2}日)<span>順延</span>");
//			 }
			 String year = RaceDate.substring(0,4);
			 //5月17日
			 String month = oCommonMethod.getValueByPatter(startDate, "(\\d{1,2})月");
			 String day = oCommonMethod.getValueByPatter(startDate, "(\\d{1,2})日");
			 while(month.length()<2)month ="0"+month;
			 while(day.length()<2)day ="0"+day;
			 startDate =year+month+day;
			 //
			 int dayNum =oCommonMethod.GetMatcherCount(oCommonMethod.getMatcherStrGroup(body,">(\\d{1,2}月\\d{1,2}日)<span>.*?</span>"));
			 String endDate =df_yyyyMMdd.format(oCommonMethod.DateSub(df_yyyyMMdd.parse(startDate), dayNum-1));
			 RacePeriod = startDate+"-"+endDate;
//			  <td class = " is-activeColor1">11:40</td>
//			 ScheduledStartTime = oCommonMethod.getValueByPatter(body, "<td class = \" is-activeColor1\">(.*?)</td>");
			 //<img src=/static_extra/pc/images/text_place2_05.png width="129" height="45" alt="多摩川">
			 TrackName = oCommonMethod.getValueByPatter(body, "width=\"129\" height=\"45\" alt=\"(.*?)\">");
			 ExtractTime = oCommonMethod.getCurrentTime();
			 
			 saveBoatRaceToDB(RaceID, TrackId, Grade, RaceTitle, DaySequence, RacePeriod, RaceDate,
			RaceNo, ExtractTime, ScheduledStartTime, TrackName, RaceClass, URaceID, Cancelled, FixedStartDesc,Distance);
			 
			 String playerInfo = oCommonMethod.getValueByPatter(body, "(<table>\\s*<colgroup span=\"1\" style=\"width:25px;\">\\s*</colgroup>.*?</table>)");
//			System.out.println(playerInfo); 
			 
			parsePlayer(playerInfo,RaceID,RaceNo,URaceID);
//			
			updatePlayer(RaceNo,TrackId,RaceDate,RaceID);
			
		}catch(Exception e){
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
	private void updatePlayer(String raceNo, String trackId, String raceDate,String raceId) {
		while(raceNo.startsWith("0"))raceNo = raceNo.replaceFirst("0", "");
		if(trackId.startsWith("0"))trackId = trackId.replaceFirst("0", "");
		
		String url ="http://www.boatrace.jp/owpc/pc/race/beforeinfo?rno="+raceNo+"&jcd="+trackId+"&hd="+raceDate;
		try {
			String body = pageHelper.doGet(url);
			
			String fileName=Controller.sSaveFilePath+File.separator+raceDate.substring(0, 4)+File.separator+raceDate+File.separator+"beforeInfo_trackId_"+trackId+File.separator+"raceNo_"+raceNo+"_"+System.currentTimeMillis()+".html";
			FileDispose.saveFile(body, fileName);
			
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
					 //table1_boatImage1Time is-fBold is-fColor1
					 String rawSt = oCommonMethod.getValueByPatter(tdNodes[0].toHtml(),"<span class=\"table1_boatImage1Time.*?>(.*?)</span>"); 
					 if(boatNo.length()>0&&rawSt.length()>0)
						 tab2ht.put(boatNo, rawSt+"_"+(i+1));
				 }
			}
			
//			Hashtable<String,String> tab1ht =new Hashtable<String,String>();
			String playerTable= oCommonMethod.getValueByPatter(body, "<table class=\"is-w748\">(.*?)</table>");
			Matcher m = oCommonMethod.getMatcherStrGroup(playerTable, "<tbody class=\"is-fs12 \">(.*?)</tbody>");
			while(m.find()){
				String player= m.group(1);
				 Parser trParser = Parser.createParser(player, null);
				 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
				 for(int i=0;i<trNodes.length;i++){
					 String trValue = trNodes[i].toHtml();
					 Parser tdParser = Parser.createParser(trValue, null);
					 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
					 if(tdNodes.length==10){
						 //<td class="is-boatColor1 is-fs14" rowspan="4">1</td>
						 String boatNo =tdNodes[0].toPlainTextString(); 
//					 <td class="is-fs18 is-fBold" rowspan="4"><a href="/owpc/pc/data/racersearch/profile?toban=3647">伊藤　　雄二</a></td>
//						 String playerName =tdNodes[2].toPlainTextString(); 
						 String playerId =oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"toban=(\\d{1,})\">"); 
						 //<td rowspan="2">51.2kg</td>
//						 String playerWeight =tdNodes[3].toPlainTextString(); 
						 //<td rowspan="4">6.94</td>
						 String exhibition =convertString(tdNodes[4].toPlainTextString()); 
						 //<td rowspan="4">0.0</td>
						 String tilt =convertString(tdNodes[5].toPlainTextString()); 
						 ///</tr>\\s*<tr>\\s*<td rowspan="2">0.0</td><td>ST</td>
						 String adjustMent  =convertString(oCommonMethod.getValueByPatter(player, "</tr>\\s*<tr>\\s*<td rowspan=\"2\">(.*?)</td>\\s*<td>ST</td>"));
						 String MadeBy =convertString(tdNodes[6].toPlainTextString().replaceAll("&nbsp;", "")); 
						 String Description =convertString(oCommonMethod.getValueByPatter(tdNodes[7].toHtml(),"<span class=\"label4 is-type1\">(.*?)</span>").replaceAll("&nbsp;", "")); 
						 String rawStart =null;
						 String course =null;
						 
						 if(tab2ht.get(boatNo)!=null){
							  rawStart = convertString(tab2ht.get(boatNo).split("_")[0]);
							  course = convertString(tab2ht.get(boatNo).split("_")[1]);
						 }
						 
						 String Start =null;
						 if(rawStart==null){
							 Start = "0";
						 }
						 String param = exhibition+"_"+adjustMent+"_"+tilt+"_"+rawStart+"_"+Start+"_"+course+"_"+MadeBy+"_"+Description;
						 oCommonDB.updPlayer("BoatRace_PreRace_Player",raceId,playerId,param);
					 }
				 }
			}
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void parsePlayer(String playerInfo,String RaceID,String RaceNo,String URaceID) {
		
		String  BoatNo=null, PlayerId=null, PlayerName=null, ExtractTime=null, PlayerOrigin=null, Age=null, 
		PlayerWeight=null, PlayerClass=null, PlayerImagePath=null, Adjustment=null, Tilt=null, Course=null, Start=null, Exhibition=null, 
		SlowDash=null, Motor=null, Boat=null, RawST=null, ST_Type=null, Start_F=null, Start_L=null, Avg_ST=null, WinRate_Country=null,
		 ShowRate_Country=null, WinRate_Local=null, ShowRate_Local=null, ShowRate_Motor=null, ShowRate_Boat=null, Scratch=null, PlayerBranch=null;
		try {
			Parser  trParser = Parser.createParser(playerInfo, null);
			 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
			 for(int i=3;i<trNodes.length;i+=4){
				 String value = trNodes[i].toHtml();
				  Parser tdParser = Parser.createParser(value, null);
		          Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
		          BoatNo = tdNodes[0].toPlainTextString();
		          // <a href="/owpc/pc/data/racersearch/profile?toban=3254"><img src="/racerphoto/3254.jpg" width="67" height="95" alt=""/></a>
		          PlayerImagePath = oCommonMethod.getValueByPatter(tdNodes[1].toHtml(), "(racerphoto/.*?\\.jpg)").replace("racerphoto","PlayerImage"); 
		          String playerInfohtml  = tdNodes[2].toHtml();
		          // <div class="is-fs11">3254  / <span class="">B1</span></div><div class="is-fs18 is-fBold">
//		          <a href="/owpc/pc/data/racersearch/profile?toban=3254">柳澤　　千春</a></div>
//		          <div class="is-fs11">香川/埼玉<br>50歳/47.0kg</div>
		          String PlayerOriginAndPlayerBranch = oCommonMethod.getValueByPatter(playerInfohtml, "</div>\\s*<div class=\"is-fs11\">(.*?)<br>.*?</div>");
		          if(PlayerOriginAndPlayerBranch.indexOf("/")>-1){
		        	  PlayerBranch = PlayerOriginAndPlayerBranch.split("/")[0];
		        	  PlayerOrigin = PlayerOriginAndPlayerBranch.split("/")[1];
		          }else{
		        	  PlayerOrigin = PlayerOriginAndPlayerBranch;
		          }
		          String PlayerAgeAndWeight = oCommonMethod.getValueByPatter(playerInfohtml, "<div class=\"is-fs11\">.*?<br>(.*?)</div>");
		          if(PlayerAgeAndWeight.indexOf("/")>-1){
		        	  Age = PlayerAgeAndWeight.split("/")[0].replace("歳","");
		        	  PlayerWeight = PlayerAgeAndWeight.split("/")[1].replace("kg","");
		        	  if(PlayerWeight.indexOf("-")>-1)PlayerWeight = null;
		          }
		          
		          PlayerId =oCommonMethod.getValueByPatter(playerInfohtml, "<div class=\"is-fs11\">(.*?)/\\s*<span").trim();
		          
		          CodePlayerBll.parsePlayerByplayerId(PlayerId);
		          
		          PlayerClass = oCommonMethod.getValueByPatter(playerInfohtml, "<span class=\".*?\">(.*?)</span>").trim();
		          PlayerName = oCommonMethod.getValueByPatter(playerInfohtml, "<a href=\"/owpc/pc/data/racersearch/.*?\">(.*?)</a>").trim();
		          // <td class="is-lineH2" rowspan="4">F0<br>L0<br>0.21</td>
		          String Start_F_L_Value = oCommonMethod.getValueByPatter(tdNodes[3].toHtml(),"<td.*?>(.*?)</td>");
		          if(Start_F_L_Value.split("<br>").length==3){
		        	  Start_F = oCommonMethod.getValueByPatter(Start_F_L_Value.split("<br>")[0], ("(\\d{1,3})")).trim();
		        	  Start_L = oCommonMethod.getValueByPatter(Start_F_L_Value.split("<br>")[1], ("(\\d{1,3})")).trim();
		        	  Avg_ST = Start_F_L_Value.split("<br>")[2].trim();
		        	  if("-".equals(Avg_ST))Avg_ST = null;
		          }
		          // <td class="is-lineH2" rowspan="4">4.52<br>20.65<br>43.48</td>
		          String WinRaceValue = oCommonMethod.getValueByPatter(tdNodes[4].toHtml(),"<td.*?>(.*?)</td>");
		          if(WinRaceValue.split("<br>").length==3){
		        	  WinRate_Country = WinRaceValue.split("<br>")[0].trim();
		        	  ShowRate_Country = WinRaceValue.split("<br>")[1].trim();
		          }
		          // <td class="is-lineH2" rowspan="4">5.83<br>31.03<br>65.52</td>
		          String WinRace_LocalValue = oCommonMethod.getValueByPatter(tdNodes[5].toHtml(),"<td.*?>(.*?)</td>");
		          if(WinRace_LocalValue.split("<br>").length==3){
		        	  WinRate_Local = WinRace_LocalValue.split("<br>")[0].trim();
		        	  ShowRate_Local = WinRace_LocalValue.split("<br>")[1].trim();
		          }
		          String ShowValue = oCommonMethod.getValueByPatter(tdNodes[6].toHtml(),"<td.*?>(.*?)</td>");
		          if(ShowValue.split("<br>").length==3){
		        	  Motor = ShowValue.split("<br>")[0].trim();
		        	  ShowRate_Motor = ShowValue.split("<br>")[1].trim();
		        	  ShowRate_Motor =Double.parseDouble(ShowRate_Motor)/100.0+"";
		          }
		          
		          String Show_BoatValue = oCommonMethod.getValueByPatter(tdNodes[7].toHtml(),"<td.*?>(.*?)</td>");
		          if(ShowValue.split("<br>").length==3){
		        	  Boat = Show_BoatValue.split("<br>")[0].trim();
		        	  ShowRate_Boat = Show_BoatValue.split("<br>")[1].trim();
		        	  ShowRate_Boat =Double.parseDouble(ShowRate_Boat)/100.0+"";
		          }
		          
		          ExtractTime = oCommonMethod.getCurrentTime();
		          saveBoatPlayerToDB(RaceID, RaceNo, BoatNo, PlayerId, PlayerName, ExtractTime, 
		        		  PlayerOrigin, Age, PlayerWeight, PlayerClass, PlayerImagePath, Adjustment, Tilt, Course, 
		        		  Start, Exhibition, SlowDash, URaceID, Motor, Boat, RawST, ST_Type, Start_F, Start_L, Avg_ST, WinRate_Country,
		        		  ShowRate_Country, WinRate_Local, ShowRate_Local, ShowRate_Motor, ShowRate_Boat, Scratch, PlayerBranch);
			 }
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	public void saveBoatRaceToDB(String RaceID, String TrackId, String Grade, String RaceTitle,String  DaySequence, String RacePeriod,
			String  RaceDate, String RaceNo,String ExtractTime, String ScheduledStartTime,
			String TrackName, String RaceClass, String URaceID, String Cancelled,String  FixedStartDesc,String Distance){
		try {
			//RaceID, TrackId, Grade, RaceTitle, DaySequence,
//			RacePeriod, RaceDate, RaceNo, ExtractTime, ScheduledStartTime, TrackName, RaceClass, URaceID, Cancelled, FixedStartDesc
			String sSql ="";
			sSql+=RaceID+",";
			sSql+=TrackId==null?"NULL,":"N'"+TrackId+"',";
			sSql+=Grade==null?"NULL,":"N'"+Grade+"',";
			sSql+=RaceTitle==null?"NULL,":"N'"+RaceTitle+"',";
			sSql+=DaySequence==null?"NULL,":"N'"+DaySequence+"',";
			sSql+=RacePeriod==null?"NULL,":"N'"+RacePeriod+"',";
			sSql+=RaceDate==null?"NULL,":"N'"+RaceDate+"',";
			sSql+=RaceNo==null?"NULL,":"N'"+RaceNo+"',";
			sSql+=ExtractTime==null?"NULL,":"N'"+ExtractTime+"',";
			sSql+=ScheduledStartTime==null?"NULL,":"N'"+ScheduledStartTime+"',";
			sSql+=TrackName==null?"NULL,":"N'"+TrackName+"',";
			sSql+=RaceClass==null?"NULL,":"N'"+RaceClass+"',";
			sSql+=URaceID==null?"NULL,":"N'"+URaceID+"',";
			sSql+=Cancelled==null?"0,":"N'"+Cancelled+"',";
			sSql+=FixedStartDesc==null?"NULL,":"N'"+FixedStartDesc+"',";
			sSql+=Distance==null?"NULL":"N'"+Distance+"'";
			
			logger.info("pr_BoatRace_PreRace_Race_InsertData " + sSql);
			oCommonDB.execStoredProcedures("pr_BoatRace_PreRace_Race_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	public void saveBoatPlayerToDB(String RaceID, String RaceNo, String BoatNo, String PlayerId, String PlayerName, String ExtractTime,
			String PlayerOrigin, String Age, String PlayerWeight, String PlayerClass,String  PlayerImagePath,String  Adjustment, String Tilt,String  Course, String Start,
			String Exhibition,String  SlowDash, String URaceID,String  Motor, String Boat,String  RawST,String  ST_Type, String Start_F, String Start_L, String Avg_ST, 
			String WinRate_Country,String  ShowRate_Country, String WinRate_Local,String  ShowRate_Local, String ShowRate_Motor,String  ShowRate_Boat, 
			String Scratch,String PlayerBranch){
			try {
//				RaceID, RaceNo, BoatNo, PlayerId, PlayerName, ExtractTime, PlayerOrigin, Age, PlayerWeight, PlayerClass, PlayerImagePath, Adjustment,
//				Tilt, Course, Start, Exhibition, SlowDash, URaceID, Motor, Boat, RawST, ST_Type, Start_F, Start_L, Avg_ST, 
//				WinRate_Country, ShowRate_Country, WinRate_Local, ShowRate_Local, ShowRate_Motor, ShowRate_Boat, Scratch, PlayerBranch
				String sSql ="";
				sSql+=RaceID+",";
				sSql+=RaceNo==null?"NULL,":"N'"+RaceNo+"',";
				sSql+=BoatNo==null?"NULL,":"N'"+ht.get(BoatNo)+"',";
				sSql+=PlayerId==null?"NULL,":"N'"+PlayerId+"',";
				sSql+=PlayerName==null?"NULL,":"N'"+PlayerName+"',";
				sSql+=ExtractTime==null?"NULL,":"N'"+ExtractTime+"',";
				sSql+=PlayerOrigin==null?"NULL,":"N'"+PlayerOrigin+"',";
				sSql+=Age==null?"NULL,":"N'"+Age+"',";
				sSql+=PlayerWeight==null?"NULL,":"N'"+PlayerWeight+"',";
				sSql+=PlayerClass==null?"NULL,":"N'"+PlayerClass+"',";
				sSql+=PlayerImagePath==null?"NULL,":"N'"+PlayerImagePath+"',";
				sSql+=Adjustment==null?"NULL,":"N'"+Adjustment+"',";
				sSql+=Tilt==null?"NULL,":"N'"+Tilt+"',";
				sSql+=Course==null?"NULL,":"N'"+Course+"',";
				sSql+=Start==null?"NULL,":"N'"+Start+"',";
				sSql+=Exhibition==null?"NULL,":"N'"+Exhibition+"',";
				sSql+=SlowDash==null?"NULL,":"N'"+SlowDash+"',";
				sSql+=URaceID==null?"NULL,":"N'"+URaceID+"',";
				sSql+=Motor==null?"NULL,":"N'"+Motor+"',";
				sSql+=Boat==null?"NULL,":"N'"+Boat+"',";
				sSql+=RawST==null?"NULL,":"N'"+RawST+"',";
				sSql+=ST_Type==null?"NULL,":"N'"+ST_Type+"',";
				sSql+=Start_F==null?"NULL,":"N'"+Start_F+"',";
				sSql+=Start_L==null?"NULL,":"N'"+Start_L+"',";
				sSql+=Avg_ST==null?"NULL,":"N'"+Avg_ST+"',";
				sSql+=WinRate_Country==null?"NULL,":"N'"+WinRate_Country+"',";
				sSql+=ShowRate_Country==null?"NULL,":"N'"+ShowRate_Country+"',";
				sSql+=WinRate_Local==null?"NULL,":"N'"+WinRate_Local+"',";
				sSql+=ShowRate_Local==null?"NULL,":"N'"+ShowRate_Local+"',";
				sSql+=ShowRate_Motor==null?"NULL,":"N'"+ShowRate_Motor+"',";
				sSql+=ShowRate_Boat==null?"NULL,":"N'"+ShowRate_Boat+"',";
				sSql+=Scratch==null?"0,":"N'"+Scratch+"',";
				sSql+=PlayerBranch==null?"NULL":"N'"+PlayerBranch+"'";
				
				logger.info("pr_BoatRace_PreRace_Player_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_PreRace_Player_InsertData ", sSql);
			} catch (Exception e) {
				logger.error("",e);
			}
	}

}
