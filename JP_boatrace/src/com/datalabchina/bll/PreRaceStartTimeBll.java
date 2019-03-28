package com.datalabchina.bll;

import java.util.Vector;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.datalabchina.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.PageHelper;

public class PreRaceStartTimeBll implements Runnable{
	private static Logger logger = Logger.getLogger(PreRaceStartTimeBll.class.getName());
	private PageHelper pageHelper = PageHelper.getPageHelper();
	private CommonDB oCommonDB =new CommonDB();
	private CommonMethod oCommonMethod = new CommonMethod();
	public void run(){
		String startDate = Controller.sStartDate;
		String endDate = Controller.sEndDate;
		Vector<String> vDate = CommonMethod.getBetwenneDate(startDate, endDate);
		try {
			for(int i =0;i<vDate.size();i++) {
				String raceDate =vDate.get(i);
				this.ParsePre(raceDate);
			}
		}catch (Exception e){
			logger.error("",e);
		}
	}
//http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517
//	private void getMainPage(String sraceDate,RacePool rp){
		public void ParsePre(String raceDate){
			try {
				//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170516
				String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay?hd="+raceDate;
				String basicBody = pageHelper.doGet(basicUrl);
				//<a href="/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517">11:01</a>
				Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/racelist(\\?rno=(\\d{1,2})&jcd=(\\d{1,2})&hd=(\\d{8}))\">(.*?)</a>");
				//?rno=1&jcd=02&hd=20170517
				while(m.find())
				{
					String raceNo = m.group(2);
					String trackId = m.group(3);
					String startTime = m.group(5).replaceAll("\\s", "");
					 while(raceNo.length()<2)raceNo= "0"+raceNo; 
					 while(trackId.length()<3)trackId= "0"+trackId; 
					 String RaceID =raceDate+trackId+raceNo;
					 String  URaceID = raceDate+"12"+trackId+raceNo;
					 String ExtratTime = oCommonMethod.getCurrentTime();
					 saveRaceStartTimeToDB(RaceID,URaceID,startTime,ExtratTime);
				}
			} catch (Exception e){
				logger.error("",e);
			}
		}

	//http://www.boatrace.jp/owpc/pc/race/beforeinfo?rno=4&jcd=24&hd=20170720
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		PreRaceStartTimeBll p = new PreRaceStartTimeBll();
		p.run();
	}
	
	public void saveRaceStartTimeToDB(String RaceID, String uRaceId, String StartTime, String ExtractTime){
		try {
			String sSql ="";
			sSql+=RaceID+",";
			sSql+=uRaceId==null?"NULL,":"N'"+uRaceId+"',";
			sSql+=StartTime==null?"NULL,":"N'"+StartTime+"',";
			sSql+=ExtractTime==null?"NULL":"N'"+ExtractTime+"'";
			logger.info("pr_BoatRace_Race_StartTime_InsertData " + sSql);
			oCommonDB.execStoredProcedures("pr_BoatRace_Race_StartTime_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
}	
