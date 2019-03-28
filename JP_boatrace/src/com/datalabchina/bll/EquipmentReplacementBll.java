package com.datalabchina.bll;

import java.util.Hashtable;
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
import com.datalabchina.common.PageHelper;

public class EquipmentReplacementBll {
	private static Logger logger = Logger.getLogger(PreRaceBll.class.getName());
	private PageHelper pageHelper = PageHelper.getPageHelper();
	private CommonDB oCommonDB =new CommonDB();
	private CommonMethod oCommonMethod = new CommonMethod();
	private Hashtable<String,String> ht =new Hashtable<String,String>();
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		EquipmentReplacementBll p = new EquipmentReplacementBll();
		p.run();
//		p.parsePreByRaceID("2017072602310");
//		p.parse(null);
	}
	
	public EquipmentReplacementBll(){
		ht.put("１", "1");
		ht.put("２", "2");
		ht.put("３", "3");
		ht.put("４", "4");
		ht.put("５", "5");
		ht.put("６", "6");
	}
	
	
	public void run() {
		String startDate = Controller.sStartDate;
		String endDate = Controller.sEndDate;
		Vector<String> vDate = CommonMethod.getBetwenneDate(startDate, endDate);
		for(int i =0;i<vDate.size();i++) {
			String raceDate =vDate.get(i);
			this.ParsePre(raceDate);
		}
	}

	public void ParsePre(String sraceDate){
		Vector<String> vUrl = getPreRaceUrl(sraceDate);
		logger.info("VUrl =================" +vUrl.size());
		//http://www.boatrace.jp/owpc/pc/race/beforeinfo?rno=4&jcd=20&hd=20130904
		String basicUrl = "http://www.boatrace.jp/owpc/pc/race/beforeinfo";
		try {
			//?rno=1&jcd=02&hd=20170517_11:26
			for(int i=0;i<vUrl.size();i++){
				String mainUrl =basicUrl+vUrl.get(i).split("_")[0];
//				String mainUrl ="http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=01&hd=20170720";
//				String startTime =null;
//				if(vUrl.get(i).split("_").length==1){
//					startTime= "0000";
//				}else{
//					startTime= vUrl.get(i).split("_")[1].replace(":", "").replaceAll("\\s", "");
//				}
				String raceDate = oCommonMethod.getValueByPatter(mainUrl, "(\\d{8})").replaceAll("\\s", "");
				String raceNo = oCommonMethod.getValueByPatter(mainUrl, "rno=(\\d{1,2})&").replaceAll("\\s", "");
				String trackId = oCommonMethod.getValueByPatter(mainUrl, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
				String body= pageHelper.doGet(mainUrl);
//				String fileName=Controller.sSaveFilePath+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+"_"+startTime+"_0000"+".html";
//				FileDispose.saveFile(body, fileName);
				parse(body,raceDate,raceNo,trackId);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	

	private void parse(String body, String raceDate, String raceNo,String trackId) {
		try {
			 while(raceNo.length()<2)raceNo= "0"+raceNo; 
			 while(trackId.length()<3)trackId= "0"+trackId; 
			 String RaceID =raceDate+trackId+raceNo;
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
							 String playerId =oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"toban=(\\d{1,})\">"); 
							 //<ul class="labelGroup1">
							 // <li><span class="label4 is-type1">ピストン×２</span></li>
							 // <li><span class="label4 is-type1">リング×４</span></li>
							 // <li><span class="label4 is-type1">シリンダ&nbsp;</span></li>
							 // </ul>
							 //リング×１ リング×２ 電気&nbsp; リング×２ ギヤ&nbsp;ピストン×２リング×１ピストン×２リング×４シリンダ&nbsp;リング×３
							 String EquipmentHtml =tdNodes[7].toHtml(); 
							 Matcher m1 =  oCommonMethod.getMatcherStrGroup(EquipmentHtml, "<li>\\s*<span class=\"label\\d* is-type\\d*\">(.*?)</span>\\s*</li>");
							 while(m1.find()){
								 String  Equipment = m1.group(1).replaceAll("&nbsp;", "");
								 String noofChange = "1";
								 if(m1.group(1).split("×").length==2){
									 Equipment =m1.group(1).split("×")[0];
									 noofChange= ht.get(m1.group(1).split("×")[1].replaceAll("\\s", ""));
								 }
								 saveToDB(RaceID,playerId,Equipment,noofChange);
							 }
						}
				 }
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}


	private void saveToDB(String raceID, String playerId, String equipment,String noofChange) {
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
				//	 rp.AddID(id);
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
}
