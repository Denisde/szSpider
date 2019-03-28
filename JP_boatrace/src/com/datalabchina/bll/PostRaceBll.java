package com.datalabchina.bll;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;
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

public class PostRaceBll implements Runnable{
	private static Logger logger = Logger.getLogger(PostRaceBll.class.getName());
	private PageHelper pageHelper = PageHelper.getPageHelper();
	private CommonDB oCommonDB =new CommonDB();
	private Hashtable<String,String> ht =new Hashtable<String,String>();
	private CommonMethod oCommonMethod = new CommonMethod();
	public static Hashtable<String,String> htBeatenDistance = null;
	public boolean isUseTraead = false;
	private Hashtable<String,String> htBetType = null;
	@Override
	public void run() {
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
						Thread thread = new Thread(new ParsePageThread("TaskThread-"+j,rp));
						thread.start();
					}
					
					this.getPostRaceUrl(raceDate,rp);
					
					for(int k=0;k<threadNum;k++)
					{
						rp.AddID("exit");
					}
				} else{
					this.parsePost(raceDate);
				}
			}
		}catch (Exception e){
			logger.error("",e);
			for(int i=0;i<3;i++)
			{
				rp.AddID("exit");
			}
		}finally{
			logger.info("wait page over parse, main thread sleep 3 minutes...");
			try {
				Thread.sleep(3*60*1000);
			} catch (InterruptedException e) {
				logger.error("",e);
			}
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> start to exec pr_Consolidate_PostRace_ByOneDay ,'"+ Controller.sStartDate+"'");
			oCommonDB.execStoredProcedures("pr_Consolidate_PostRace_ByOneDay", "'"+ Controller.sStartDate+"','"+Controller.sEndDate+"'");
		}
	}
	
	public PostRaceBll()
	{
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
	private void parsePost(String sraceDate) {
			Vector<String> vUrl = getPostRaceUrl(sraceDate);
			logger.info("VUrl =================" +vUrl.size());
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/raceresult";
			try {
				//?rno=1&jcd=02&hd=20170517_11:26
				for(int i=0;i<vUrl.size();i++){
//					http://www.boatrace.jp/owpc/pc/race/raceresult?rno=5&jcd=01&hd=20170721
					String mainUrl =basicUrl+vUrl.get(i).split("_")[0];
//					String startTime= vUrl.get(i).split("_")[1].replace(":", "").replaceAll("\\s", "");
					String raceDate = oCommonMethod.getValueByPatter(mainUrl, "(\\d{8})").replaceAll("\\s", "");
					String raceNo = oCommonMethod.getValueByPatter(mainUrl, "rno=(\\d{1,2})&").replaceAll("\\s", "");
					String trackId = oCommonMethod.getValueByPatter(mainUrl, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
//					String raceId =raceDate+"_"+trackId+"_"+raceNo; 
					String body= pageHelper.doGet(mainUrl);
					String fileName=Controller.sSaveFilePath+File.separator+raceDate.substring(0, 4)+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+".html";
					FileDispose.saveFile(body, fileName);
					parse(fileName);
				}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	public void parsePostByRaceID(String raceId){
		//http://www.boatrace.jp/owpc/pc/race/raceresult?rno=1&jcd=02&hd=20170517
		//2017051700501
		try {
			String raceDate = raceId.substring(0,8);
			String raceNo = raceId.substring(11);
			String  trackId= raceId.substring(8,11).replaceFirst("0", "");
			String url ="http://www.boatrace.jp/owpc/pc/race/raceresult?rno="+raceNo+"&jcd="+trackId+"&hd="+raceDate;
			String body= pageHelper.doGet(url);
			String fileName=Controller.sSaveFilePath+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+".html";
			FileDispose.saveFile(body, fileName);
			parse(fileName);
		}catch (Exception e){
			logger.error("",e);
		}
	}
	
	private void parse(String fileName) {
		String RaceID=null, TrackID=null, TrackName=null, RaceTitle=null, RaceDate=null, RaceNo=null, Weather=null, 
		Wave=null, WindDirection=null, WindSpeed=null, Temperature=null,WaterTemp=null,
		FlowSpeed=null, WaterLine=null, TideUP=null, TideDown=null, WindDesc=null,
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
			 //<div class="weather1_bodyUnit is-direction"><p class="weather1_bodyUnitImage is-direction11"></p>
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
				 saveBoatRaceToDB(RaceID, TrackID, TrackName, RaceTitle, RaceDate, RaceNo, Weather, Wave, 
						 WindDirection, WindSpeed, Temperature, WaterTemp, FlowSpeed, WaterLine, TideUP, TideDown, 
						 WindDesc, ExtractTime, WinTactics, Remark, URaceID, CancelledDesc, Distance,Compass_Direction);
			 }
			 //==========================> Start parse Dividend
			 parseDividend(body,RaceDate,RaceNo,TrackID);
			 
			 //==========================> Start parse player 
			 String playertable = oCommonMethod.getValueByPatter(body, "<table class=\"is-w495\">.*?<thead>\\s*<tr class=\"is-fs14\">\\s*<th>着</th>(.*?)</table>");
			 Hashtable<String ,String> Actual_STht =  getActual_ST(body);
			 Matcher m = oCommonMethod.getMatcherStrGroup(playertable, "<tbody>(.*?)</tbody>");
			 while(m.find()){
				 String  PlayerID=null, PlayerName=null, BoatNo=null, FinishPosition=null, RawFinishPosition=null, FinishTime=null, 
				 Actual_ST=null, StartPosition=null, PlayerWeight=null;
				 String onePlayer =  m.group(1);
				 Parser tdParser = Parser.createParser(onePlayer, null);
		         Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
//		         <td class="is-p20-0"><span class="is-fs12">4283</span><span class="is-fs18 is-fBold">石井　　裕美</span></td>
		         PlayerID = oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"<span class=\"is-fs12\">(.*?)</span>");
		         PlayerName = oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"<span class=\"is-fs18 is-fBold\">(.*?)</span>");
		         BoatNo =tdNodes[1].toPlainTextString();
		         RawFinishPosition = tdNodes[0].toPlainTextString().replaceAll("\\s*","");
		         FinishPosition = ht.get(RawFinishPosition);
//		         if(FinishPosition==null)Scratch ="1";
//		         if(FinishPosition==null)continue;
		         //<td>1&#39;55"8</td>
		         String rawFinishTime =  tdNodes[3].toPlainTextString().replaceAll("\\s*","");
		         FinishTime =  getFinishTime(rawFinishTime);
		         if(Actual_STht.get(BoatNo)!=null){
		        	 Actual_ST = Actual_STht.get(BoatNo).split("_")[0];
		        	 StartPosition = Actual_STht.get(BoatNo).split("_")[1];
		         }
		         saveBoatPlayerToDB(RaceID, RaceNo, PlayerID, PlayerName, BoatNo, FinishPosition,
		        		 RawFinishPosition, FinishTime, Actual_ST, ExtractTime, URaceID, StartPosition, PlayerWeight);
			 }	
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void parseDividend(String body,String RaceDate,String RaceNo,String TrackID) {
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
	
	public static String convertString(String str){
		if(str==null||str.replaceAll("\\s","").trim().length()<1)return null;
		else return str;
	}
	
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

	//http://www.boatrace.jp/owpc/pc/race/raceresult?rno=1&jcd=03&hd=20170720
	private Vector<String> getPostRaceUrl(String sraceDate) {
		Vector<String>  v= new Vector<String>();
		try {
			//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170516
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay?hd="+sraceDate;
			String basicBody = pageHelper.doGet(basicUrl);
			//<a href="/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517">11:01</a>
//			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/raceresult(.*?)\">.*?</a>");
//			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<span class=\"is-payout2");
			//?rno=1&jcd=02&hd=20170517
			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">");
			while(m.find()){
				String url = m.group(1);
				v.add(url);
			}
			v =CommonMethod.removeDuplicate(v);
		} catch (Exception e){
			logger.error("",e);
			return v;
		}
		return v;
	}
	
	private void getPostRaceUrl(String sraceDate,RacePool rp) {
		try {
			//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170720
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay?hd="+sraceDate;
			String basicBody = pageHelper.doGet(basicUrl);
			//<a href="/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517">11:01</a>
//			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/raceresult(.*?)\">.*?</a>");
			//<span class="is-payout2"
			//<span class="is-fBold is-fColor1">不成立</span>
//			<td class="cellbg c6-2" data-href="/owpc/pc/race/raceresult?rno=2&jcd=12&hd=20170906"><span class="is-fBold is-fColor1">
			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<span class=\"is-payout2");
//			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\"");
			//?rno=1&jcd=02&hd=20170517
			while(m.find()){
				String url = m.group(1);
				String id = "Post_"+url;
				rp.AddID(id);
			}
			Matcher m1 = oCommonMethod.getMatcherStrGroup(basicBody, "href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<span class=\"is\\-fBold is\\-fColor1\">不成立");
			while(m1.find()){
				String url = m1.group(1);
				String id = "Post_"+url;
				rp.AddID(id);
			}
			
			Matcher m2 = oCommonMethod.getMatcherStrGroup(basicBody, "href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<div class=\"is-lineH20\">\\s*<span class=\"is-payout2");
			while(m2.find()){
				String url = m2.group(1);
				String id = "Post_"+url;
				rp.AddID(id);
			}
			
		} catch (Exception e){
			logger.error("",e);
		}
	}


	public static void main(String[] args) {
		PostRaceBll p = new PostRaceBll();
//		String fileName ="D:\\Denis\\Jpboat\\test\\20170721\\trackId_05\\raceNo_6.html";
//		p.parse(fileName);
//		p.run();
		/*2009013100507
		*/
		p.parsePostByRaceID("2010043000812");
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
}	
