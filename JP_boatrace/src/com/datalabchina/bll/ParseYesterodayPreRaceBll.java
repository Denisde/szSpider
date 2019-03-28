package com.datalabchina.bll;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
//import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;

import com.datalabchina.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;
import com.datalabchina.common.PageHelper;
public class ParseYesterodayPreRaceBll implements Runnable{
	private static Logger logger = Logger.getLogger(ParseYesterodayPreRaceBll.class.getName());
	private PageHelper pageHelper = PageHelper.getPageHelper();
	private static CommonDB oCommonDB =new CommonDB();
	private CommonMethod oCommonMethod = new CommonMethod();
	private Hashtable<String,String> ht =new Hashtable<String,String>();
	public DateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
//	public boolean isUseTraead = true;
	//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170516 //某一天所有比赛的链接 但是只包含post  的数据 
	//http://www.boatrace.jp/owpc/pc/race/pay 当天所有比赛信息的链接  
	//http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517 访问每场比赛的链接 
	@Override
	public void run(){
//		String startDate = Controller.sStartDate;
//		String endDate = Controller.sEndDate;
		String startDate =oCommonMethod.getAddDay(-1);
		String endDate =oCommonMethod.getAddDay(0);
		Vector<String> vDate = CommonMethod.getBetwenneDate(startDate, endDate);
		try {
			for(int i =0;i<vDate.size();i++) {
				String raceDate =vDate.get(i);
				 List<String>  list = oCommonDB.getfixRaceID(raceDate);
				 for(int j=0;j<list.size();j++){	
					 String raceid = list.get(j); 
					 parsePreByRaceID(raceid);
				 }
			}
		}catch (Exception e){
			logger.error("",e);
		}
	}
	
	public ParseYesterodayPreRaceBll()
	{
		ht.put("１", "1");
		ht.put("２", "2");
		ht.put("３", "3");
		ht.put("４", "4");
		ht.put("５", "5");
		ht.put("６", "6");
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
	
	public void parsePreByRaceID(String raceId){
		//http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517
		try {
			//2017051700501 2017072202212 2017072602310
			String raceDate = raceId.substring(0,8);
			String raceNo = raceId.substring(11);
			String  trackId= raceId.substring(8,11).replaceFirst("0", "");
			String url ="http://www.boatrace.jp/owpc/pc/race/racelist?rno="+raceNo+"&jcd="+trackId+"&hd="+raceDate;
			String body= pageHelper.doGet(url);
			String fileName=Controller.sSaveFilePath+File.separator+raceDate.substring(0,4)+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+".html";
			FileDispose.saveFile(body, fileName);
			parse(fileName);
		}catch (Exception e){
			logger.error("",e);
		}
	}

//http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517
		public void ParsePre(String sraceDate){
		Vector<String> vUrl = getPreRaceUrl(sraceDate);
		logger.info("VUrl =================" +vUrl.size());
		String basicUrl = "http://www.boatrace.jp/owpc/pc/race/racelist";
		try {
			//?rno=1&jcd=02&hd=20170517_11:26
			for(int i=0;i<vUrl.size();i++){
				String mainUrl =basicUrl+vUrl.get(i).split("_")[0];
//				String mainUrl ="http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=01&hd=20170720";
				String startTime =null;
				if(vUrl.get(i).split("_").length==1){
					startTime= "0000";
				}else{
					startTime= vUrl.get(i).split("_")[1].replace(":", "").replaceAll("\\s", "");
				}
				String raceDate = oCommonMethod.getValueByPatter(mainUrl, "(\\d{8})").replaceAll("\\s", "");
				String raceNo = oCommonMethod.getValueByPatter(mainUrl, "rno=(\\d{1,2})&").replaceAll("\\s", "");
				String trackId = oCommonMethod.getValueByPatter(mainUrl, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
//				String raceId =raceDate+"_"+trackId+"_"+raceNo; 
				String body= pageHelper.doGet(mainUrl);
				String fileName=Controller.sSaveFilePath+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+"_"+startTime+".html";
				FileDispose.saveFile(body, fileName);
				parse(fileName);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void parse(String fileName) {
//		fileName ="D:\\Denis\\Jpboat\\test\\20170721\\trackId_03\\raceNo_4_1215.html";
		String RaceID=null, TrackId=null, Grade=null, RaceTitle=null, DaySequence=null, RacePeriod=null, RaceDate=null, 
		RaceNo=null, ExtractTime=null, ScheduledStartTime=null, TrackName=null, RaceClass=null, URaceID=null, Cancelled=null, FixedStartDesc=null,Distance = null;
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
//			 Grade = oCommonMethod.getValueByPatter(RaceTitle, "<div class=\"(.*?)\">\\s*<h2 class=\"heading2_titleName\">");
			 Grade = oCommonMethod.getValueByPatter(body, "<div class=\"(heading2_title.*?)\">\\s{1,}<h2 class=\"heading2_titleName\">");
			 //<span class="heading2_titleDetail is-type1">
			 Distance = oCommonMethod.getValueByPatter(body, "<span class=\"heading2_titleDetail is-type1\">.*?(\\d{1,}).*?</span>");

			 //heading2_title is-ippan
			 //heading2_title is-G1a
			 if(Grade.indexOf("heading2_title is-ippan")>-1){
				 Grade = "一般";
			 }else if(Grade.indexOf("heading2_title is-G3")>-1){
				 Grade = "ＧⅢ";
			 }else if(Grade.indexOf("heading2_title is-SGa")>-1){
				 Grade = "ＳＧ";
			 }else if(Grade.indexOf("heading2_title is-G2")>-1){
				 Grade = "ＧⅡ";
			 }else if(Grade.indexOf("heading2_title is-G1")>-1){
				 Grade = "ＧⅠ";
			 }
			 
			 if(Grade.length()<1)Grade=null;
			 //<span class="heading2_titleDetail is-type1">まつりだｏｎｅ1800m</span>
			 //<span class="heading2_titleDetail is-type1">予選1800m</span>
//			 RaceClass = oCommonMethod.getValueByPatter(raceInfo, "<span class=\"heading2_titleDetail.*?\">(.*?)</span>").replaceAll("\\d{4}m", "").trim();
			 RaceClass = oCommonMethod.getValueByPatter(raceInfo, "<span class=\"heading2_titleDetail.*?\">(.*?)</span>").replaceAll("\\d{4}m", "").replaceAll("\\s", "").replaceAll("<.*?>", "").replaceAll("　", "");
			 //<li class="is-active2"><span class="tab2_inner">5月17日<span>初日</span></span></li>
			 DaySequence = oCommonMethod.getValueByPatter(body, "<li class=\"is-active2\">\\s*<span class=\"tab2_inner\">.*?<span>(.*?)</span>\\s*</span></li>").trim();
			 //<span class="tab2_inner">5月17日<span>初日</span></span></li>
			 //>5月14日<span>初日</span></a></li>
//			 String startDate =oCommonMethod.getValueByPatter(body, ">(\\d{1,2}月\\d{1,2}日)<span>初日</span>");
			 String startDate =oCommonMethod.getValueByPatter(body, "tab2_tabs\">.*?>(\\d{1,2}月\\d{1,2}日)<span>.*?</span>");
			 String year = RaceDate.substring(0,4);
			 //5月17日
			 String month = oCommonMethod.getValueByPatter(startDate, "(\\d{1,2})月");
			 String day = oCommonMethod.getValueByPatter(startDate, "(\\d{1,2})日");
			 while(month.length()<2)month ="0"+month;
			 while(day.length()<2)day ="0"+day;
			 startDate =year+month+day;
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
			 
			 //===================  Start parse player  =================================
			 
			 String playerInfo = oCommonMethod.getValueByPatter(body, "(<table>\\s*<colgroup span=\"1\" style=\"width:25px;\">\\s*</colgroup>.*?</table>)");
//			System.out.println(playerInfo); 
			parsePlayer(playerInfo,RaceID,RaceNo,URaceID);
			
			//===================  Start updata player  =================================
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
			
			String fileName=Controller.sSaveFilePath+File.separator+raceDate+File.separator+"beforeInfo_trackId_"+trackId+File.separator+"raceNo_"+raceNo+"_"+System.currentTimeMillis()+".html";
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
					 String rawSt = oCommonMethod.getValueByPatter(tdNodes[0].toHtml(),"<span class=\"table1_boatImage1Time.*?>(.*?)</span>"); 
					 if(boatNo.length()>0&&rawSt.length()>0)
						 tab2ht.put(boatNo, rawSt+"_"+(i+1));
				 }
			}
			
//			Hashtable<String,String> tab1ht =new Hashtable<String,String>();
			//<tbody class="is-fs12 is-miss">
			String playerTable= oCommonMethod.getValueByPatter(body, "<table class=\"is-w748\">(.*?)</table>");
			Matcher m = oCommonMethod.getMatcherStrGroup(playerTable, "<tbody class=\"is-fs12.*?\">(.*?)</tbody>");
			while(m.find()){
				String player= m.group(1);
				 Parser trParser = Parser.createParser(player, null);
				 Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
				 for(int i=0;i<trNodes.length;i++){
					 String trValue = trNodes[i].toHtml();
					 Parser tdParser = Parser.createParser(trValue, null);
					 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
					 if(tdNodes.length==10){
//						<td class="is-boatColor1 is-fs14" rowspan="4">1</td>
						 String boatNo =tdNodes[0].toPlainTextString(); 
//					 	<td class="is-fs18 is-fBold" rowspan="4"><a href="/owpc/pc/data/racersearch/profile?toban=3647">伊藤　　雄二</a></td>
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
//						 String rawStart = tab2ht.get(boatNo);
						 String rawStart =null;
						 String course =null;
						 String MadeBy =convertString(tdNodes[6].toPlainTextString().replaceAll("&nbsp;", "")); 
						 String Description =convertString(oCommonMethod.getValueByPatter(tdNodes[7].toHtml(),"<span class=\"label4 is-type1\">(.*?)</span>").replaceAll("&nbsp;", "")); 
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
					//38歳/-
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
//		        	  ShowRate_Motor =Double.parseDouble(ShowRate_Motor)/100.0+"";
		        	  ShowRate_Motor ="0."+ShowRate_Motor.replaceAll("[^\\d]", "");
		          }
		          
		          String Show_BoatValue = oCommonMethod.getValueByPatter(tdNodes[7].toHtml(),"<td.*?>(.*?)</td>");
		          if(ShowValue.split("<br>").length==3){
		        	  Boat = Show_BoatValue.split("<br>")[0].trim();
		        	  ShowRate_Boat = Show_BoatValue.split("<br>")[1].trim();
//		        	  ShowRate_Boat =Double.parseDouble(ShowRate_Boat)/100.0+"";
		        	  ShowRate_Boat ="0."+ShowRate_Boat.replaceAll("[^\\d]", "");
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
	
	
	private Vector<String> getPreRaceUrl(String raceDate) {
		Vector<String>  v= new Vector<String>();
		try {
			//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170516
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay?hd="+raceDate;
			String basicBody = pageHelper.doGet(basicUrl);
			//<a href="/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517">11:01</a>
			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/racelist(.*?)\">(.*?)</a>");
			//?rno=1&jcd=02&hd=20170517
			while(m.find())
			{
				String url = m.group(1);
				String startTime = m.group(2).replaceAll("\\s", "");
				// 对开赛时间进行一个过滤 
//				String id = "Pre_"+url+"_"+startTime;
//				rp.AddID(id);
				v.add(url+"_"+startTime);
			}
			
			Matcher m1 = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<span class=\"is-payout2");
			//?rno=1&jcd=02&hd=20170517
			while(m1.find()){
				String url = m1.group(1);
				String id = url+"_";
				v.add(id);
			}
			
		} catch (Exception e){
			logger.error("",e);
			return v;
		}
		return v;
	}
	
	public static String convertString(String str){
		if(str==null||str.trim().replaceAll("&nbsp;", "").length()<1)return null;
		else return str;
	}
	
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		ParseYesterodayPreRaceBll p = new ParseYesterodayPreRaceBll();
//		Controller.sStartDate="20180406";
//		Controller.sEndDate="20180406";
		 List<String>  list = oCommonDB.getfixRaceID();
		 for(int i=0;i<list.size();i++){
			 String raceid = list.get(i); 
			 p.parsePreByRaceID(raceid);
		 }
//		 p.parsePreByRaceID("2017072100711");
		
//		String str ="2018030100101"
//+",2018030100102"
//+",2018030200112";
//		for(String raceid:str.split(",")){
//			p.parsePreByRaceID(raceid);
//		}
//		p.run();
//		p.parse(null);
	}
	
	public void saveBoatRaceToDB(String RaceID, String TrackId, String Grade, String RaceTitle,String  DaySequence, String RacePeriod,
			String  RaceDate, String RaceNo,String ExtractTime, String ScheduledStartTime,
			String TrackName, String RaceClass, String URaceID, String Cancelled,String  FixedStartDesc,String Distance){
		try {
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
