package com.datalabchina.bll;

import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.datalabchina.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.PageHelper;
import com.datalabchina.common.RacePool;

public class PostRaceWeatherBll implements Runnable{
	private static Logger logger = Logger.getLogger(PostRaceWeatherBll.class.getName());
	private PageHelper pageHelper = PageHelper.getPageHelper();
	private CommonDB oCommonDB =new CommonDB();
	private Hashtable<String,String> ht =new Hashtable<String,String>();
	private CommonMethod oCommonMethod = new CommonMethod();
	public static Hashtable<String,String> htBeatenDistance = null;
//	public boolean isUseTraead = true;
	public boolean isUseTraead = false;
	private Hashtable<String,String> htBetType = null;
	@Override
	public void run() {
		RacePool rp = new RacePool();
		String startDate = Controller.sStartDate;
		String endDate = Controller.sEndDate;
//		String startDate = "20170721";
//		String endDate = "20170721";
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
		}
	}
	
	public PostRaceWeatherBll()
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
				for(int i=0;i<vUrl.size();i++)
				{
//					http://www.boatrace.jp/owpc/pc/race/raceresult?rno=5&jcd=01&hd=20170721
					String mainUrl =basicUrl+vUrl.get(i).split("_")[0];
//					String startTime= vUrl.get(i).split("_")[1].replace(":", "").replaceAll("\\s", "");
					String raceDate = oCommonMethod.getValueByPatter(mainUrl, "(\\d{8})").replaceAll("\\s", "");
					String raceNo = oCommonMethod.getValueByPatter(mainUrl, "rno=(\\d{1,2})&").replaceAll("\\s", "");
					String trackId = oCommonMethod.getValueByPatter(mainUrl, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
//					String raceId =raceDate+"_"+trackId+"_"+raceNo; 
					String body= pageHelper.doGet(mainUrl);
					parse(body,raceDate,raceNo,trackId);
				}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	public void parsePostByRaceID(String raceId){
		//http://www.boatrace.jp/owpc/pc/race/raceresult?rno=1&jcd=02&hd=20170517
		try {
			//2017051700501
			String raceDate = raceId.substring(0,8);
			String raceNo = raceId.substring(11).replaceFirst("0", "");
			String  trackId= raceId.substring(8,11).replaceFirst("0", "");
			String url ="http://www.boatrace.jp/owpc/pc/race/raceresult?rno="+raceNo+"&jcd="+trackId+"&hd="+raceDate;
			String body= pageHelper.doGet(url);
//			String fileName=Controller.sSaveFilePath+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+".html";
//			FileDispose.saveFile(body, fileName);
			parse(body,raceDate,raceNo,trackId);
		}catch (Exception e){
			logger.error("",e);
		}
	}
	
	private void parse(String body,String RaceDate,String RaceNo,String TrackID) {
//		fileName = "D:\\Denis\\Jpboat\\test\\20170720\\trackId_11\\raceNo_3.html";
//		String body = FileDispose.readFile(fileName);
		String RaceID =null, TrackName =null, Weather =null, Wave =null, 
		WindDirection =null, WindSpeed =null, Temperature =null, WaterTemp =null, FlowSpeed =null, WaterLine =null, TideUP =null, TideDown =null, ExtractTime
				,WindDesc=null,Compass_Direction=null;;
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
			 
			 saveBoatRaceWeatherToDB(RaceID, TrackID, TrackName, RaceDate, RaceNo, Weather, Wave,
					 WindDirection, WindSpeed, Temperature, WaterTemp, FlowSpeed, WaterLine, TideUP, TideDown, ExtractTime,WindDesc,Compass_Direction);
			 
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	public static String convertString(String str){
		if(str==null||str.replaceAll("\\s", "'").length()<1)return null;
		else return str.replaceAll("&nbsp;", "");
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
			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">");
			//?rno=1&jcd=02&hd=20170517
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
			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<span class=\"is-payout2");
			//?rno=1&jcd=02&hd=20170517
			while(m.find()){
				String url = m.group(1);
				String id = "Post_"+url;
				rp.AddID(id);
			}
		} catch (Exception e){
			logger.error("",e);
		}
	}


	public static void main(String[] args) {
		PostRaceWeatherBll p = new PostRaceWeatherBll();
		p.run();
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

}	
