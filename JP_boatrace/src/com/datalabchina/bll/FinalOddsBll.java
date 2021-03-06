package com.datalabchina.bll;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
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

public class FinalOddsBll implements Runnable{
	private static Logger logger = Logger.getLogger(FinalOddsBll.class.getName());
	private PageHelper pageHelper = PageHelper.getPageHelper();
	private static CommonDB oCommonDB =new CommonDB();
	private CommonMethod oCommonMethod = new CommonMethod();
	DateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public Hashtable<String,String> ht = new Hashtable<String,String>(); 
	public boolean isUseTraead = false;
//	public boolean isUseTraead = true; 
//http://www.boatrace.jp/owpc/pc/race/odds2tf?rno=1&jcd=18&hd=20170724 ----->2連単・2連複
//http://www.boatrace.jp/owpc/pc/race/oddsk?rno=1&jcd=18&hd=20170724 ----->拡連複
//http://www.boatrace.jp/owpc/pc/race/oddstf?rno=1&jcd=18&hd=20170724----->単勝・複勝
//	http://www.boatrace.jp/owpc/pc/race/odds3t?rno=1&jcd=18&hd=20170724 ----->3連単
//	http://www.boatrace.jp/owpc/pc/race/odds3f?rno=1&jcd=18&hd=20170724 ----->3連複
	
	public static void main(String[] args) {
		FinalOddsBll o= new FinalOddsBll();
//		while(true){
		Controller.sStartDate = "20181106"; 
		Controller.sEndDate = "20181106"; 
		o.run();
//		List<String>  list = oCommonDB.getfixRaceID();
//		int lenth = list.size();
//		for(int i=0;i<lenth;i++){
//			String raceId = list.get(i);
//			o.fixFinOddsByRaceId(raceId);
//		}
//		o.fixFinOddsByRaceId("2018071300312");
	//		}
	}
	
	@Override
	public void run() {
		try {
			if(isUseTraead){
				runLiveFIinalOdds();
			}
			else{
				runFIinalOdds();
			}
		} catch (Exception e){
			logger.error("",e);
		}
	}
	
	public void runFIinalOdds(){
		String startDate = Controller.sStartDate;
		String endDate = Controller.sEndDate;
		Vector<String> vDate = CommonMethod.getBetwenneDate(startDate, endDate);
		for(int i =0;i<vDate.size();i++) {
			String raceDate =vDate.get(i);
			this.parseOdds(raceDate);
		}
	}
	
	// 使用多线程 
	public void runLiveFIinalOdds(){
		RacePool rp = new RacePool();
		String startDate = Controller.sStartDate;
		String endDate = Controller.sEndDate;
		Vector<String> vDate = CommonMethod.getBetwenneDate(startDate, endDate);
		try {
			for(int i =0;i<vDate.size();i++) {
				String raceDate =vDate.get(i);
				int threadNum = 3;
				for(int j=1;j<=threadNum;j++)
				{
					Thread thread = new Thread( new ParsePageThread("TaskThread-"+j,rp));
					thread.start();
				}
				this.getOddsRaceUrl(raceDate,rp);
				for(int k=0;k<threadNum;k++)
				{
					rp.AddID("exit");
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

//http://www.boatrace.jp/owpc/pc/race/odds2tf?rno=1&jcd=18&hd=20170724 ----->2連単・2連複
//http://www.boatrace.jp/owpc/pc/race/oddsk?rno=1&jcd=18&hd=20170724 ----->拡連複
//http://www.boatrace.jp/owpc/pc/race/oddstf?rno=1&jcd=18&hd=20170724----->単勝・複勝
//	http://www.boatrace.jp/owpc/pc/race/odds3t?rno=1&jcd=18&hd=20170724 ----->3連単
//	http://www.boatrace.jp/owpc/pc/race/odds3f?rno=1&jcd=18&hd=20170724 ----->3連複
	private void parseOdds(String sraceDate) {
		Vector<String> vUrl = getOddsRaceUrl(sraceDate);
		logger.info("VUrl  size : =================" +vUrl.size());
		String basicUrl = "http://www.boatrace.jp/owpc/pc/race";
		try {//?rno=1&jcd=02&hd=20170517
		for(int i=0;i<vUrl.size();i++){
			String oddsTypeArr [] ={"odds2tf","oddsk","oddstf","odds3t","odds3f"};
			for(int j=0;j<oddsTypeArr.length;j++){
				String oddsType = oddsTypeArr[j];
				String mainUrl =basicUrl+"/"+oddsType+vUrl.get(i);
//				String mainUrl =basicUrl+"/"+oddsType+"?rno=8&jcd=07&hd=20170725";
				this.parse( mainUrl,oddsType);
			}
		}
	} catch (Exception e) {
		logger.error("",e);
	}
}

	public  void fixFinOddsByRaceId(String raceId){
		String yyyyMMdd  = raceId.substring(0,8);
		String trackId = raceId.substring(8,11);
		if(trackId.startsWith("00"))
			trackId = trackId.replace("00", "0");
		else 
			trackId = Integer.parseInt(trackId)+"";
		
		String raceNo = Integer.parseInt(raceId.substring(11))+"";
		String basicUrl = "http://www.boatrace.jp/owpc/pc/race";
		try {
//			String mainUrl =basicUrl+"/"+oddsType+"?rno=8&jcd=07&hd=20170725";
			String url ="rno="+raceNo+"&jcd="+trackId+"&hd="+yyyyMMdd;
			String oddsTypeArr [] ={"odds2tf","oddsk","oddstf","odds3t","odds3f"};
			for(int j=0;j<oddsTypeArr.length;j++){
				String oddsType = oddsTypeArr[j];
				String mainUrl =basicUrl+"/"+oddsType+"?"+url;
//				String mainUrl =basicUrl+"/"+oddsType+"?rno=8&jcd=07&hd=20170725";
				this.parse( mainUrl,oddsType);
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
			 String  H1_2_3, H1_2_4, H1_2_5, H1_2_6, H1_3_4, H1_3_5, H1_3_6, H1_4_5, H1_4_6, H1_5_6, H2_3_4, H2_3_5, H2_3_6, H2_4_5, H2_4_6, H2_5_6, H3_4_5, H3_4_6, H3_5_6, H4_5_6;
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
			 
//			 Hashtable<String,String> tht = new Hashtable<String,String>();
//			 String x ="";
//			 String y ="";
//			 String z ="";
//			 for(int i=0;i<trNodes.length;i++){
//				 String trValue = trNodes[i].toHtml();
//				 Parser tdParser = Parser.createParser(trValue, null);
//				 Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
//				 for(int j=0;j<tdNodes.length;j++){
//					 if(tdNodes.length==18){
//						 
//					 }else if(tdNodes.length==12){
//						 
//					 }
//				 }
//			 }
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
			sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
			sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
//			sSql+=timeStamp==null?"NULL":"N'"+timeStamp+"'";
			if(sSql.indexOf("NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL")<0){
				logger.info("pr_BoatRace_FinalT_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_BoatRace_FinalT_InsertData ", sSql);	
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	public static String  convertString(String str){
		if(str==null||str.length()<1)return null;
		
		if(str.indexOf("欠場")>-1)return null;
		
		return str.trim();
	}

	private void parseTi(String fileName ,String raceID,String timeStamp,String extractTime,boolean isFinal) {
		try {
//			fileName = "D:\\Denis\\Jpboat\\test\\20170724\\trackId_02\\raceNo_1_odds3t.html";
			String body = FileDispose.readFile(fileName);
//			raceID = "2017072400201";
//			timeStamp =oCommonMethod.getCurrentTime();
//			extractTime =oCommonMethod.getCurrentTime();
			String  IsFinal = "0";
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
			if(isAllNull(Tiht))
				saveTiToDB(raceID, timeStamp, IsFinal,Tiht,extractTime,CorruptedOdds);
			
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
//				sSql =sSql.replaceAll("N'0\\.0'", "NULL");
//				sSql =sSql.replaceAll("N'0\\.00'", "NULL");
//				sSql =sSql.replaceAll("N'0'", "NULL");
				sSql+=corruptedOdds==null?"NULL":"N'"+corruptedOdds+"'";
//				sSql+=timeStamp==null?"NULL":"N'"+timeStamp+"'";
//				sSql =sSql.replaceAll(",N'0.0',", ",NULL,");
//				logger.info("pr_Keirin_LiveTi_InsertData sql :" + sSql);
				str = str+"("+sSql+"),"+"\r\n";
			}
			str = "  insert into BoatRace_FinalTi VALUES "+str.substring(0,str.length()-3);
			logger.info(str);
			oCommonDB.insertToDB(str);
			//pr_Consolidate_Log_TiWprob_Live
//			oCommonDB.execStoredProcedures("pr_Consolidate_Log_TiWprob_Live",raceId+",'"+timeStamp+"'");
//			logger.info("success exec sp:  pr_Consolidate_Log_TiWprob_Live "+raceId+",'"+timeStamp+"''");
			
		}catch (Exception e) {
			logger.error("",e);
		}
	}
	
	
	private void parsePW(String fileName ,String raceID,String timeStamp,String extractTime,boolean isFinal) {
		try {
//			fileName = "D:\\Denis\\Jpboat\\test\\20170724\\trackId_02\\raceNo_1_oddstf.html";
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
				 saveWtoDB(raceID,timeStamp,IsFinal,wht,extractTime);
			 
			 String Pbody = oCommonMethod.getValueByPatter(body, "複勝オッズ</span>.*?<div class=\"table1\">(.*?)</div>");
//				System.out.println(Ebody);
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
			 
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void savePtoDB(String raceID, String timeStamp,String isFinal,
			Hashtable<String, String> pht, String extractTime,String CorruptedOdds) {
		String sSql ="";
		sSql+=raceID+",";
//		sSql+=timeStamp==null?"NULL,":"N'"+timeStamp+"',";
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
		
		sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
		sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
		
		logger.info("pr_BoatRace_FinalP_InsertData " + sSql);
		oCommonDB.execStoredProcedures("pr_BoatRace_FinalP_InsertData ", sSql);	
	}

	private void saveWtoDB(String raceID, String timeStamp,String IsFinal,Hashtable<String, String> wht, String extractTime) {
		String sSql ="";
		sSql+=raceID+",";
//		sSql+=timeStamp==null?"NULL,":"N'"+timeStamp+"',";
//		sSql+="N'0',";
		sSql+=IsFinal==null?"NULL,":"N'"+IsFinal+"',";
		sSql+=wht.get("w_1")==null?"NULL,":"N'"+wht.get("w_1")+"',";
		sSql+=wht.get("w_2")==null?"NULL,":"N'"+wht.get("w_2")+"',";
		sSql+=wht.get("w_3")==null?"NULL,":"N'"+wht.get("w_3")+"',";
		sSql+=wht.get("w_4")==null?"NULL,":"N'"+wht.get("w_4")+"',";
		sSql+=wht.get("w_5")==null?"NULL,":"N'"+wht.get("w_5")+"',";
		sSql+=wht.get("w_6")==null?"NULL,":"N'"+wht.get("w_6")+"',";
		
		sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
		sSql+="N'0'";
		
		logger.info("pr_BoatRace_FinalW_InsertData " + sSql);
		oCommonDB.execStoredProcedures("pr_BoatRace_FinalW_InsertData ", sSql);	
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
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void saveQWtoDB(String raceID, String timeStamp,String IsFinal,Hashtable<String, String> qwht, String extractTime,String CorruptedOdds) {

		try {
			String sSql ="";
			sSql+=raceID+",";
//			sSql+=timeStamp==null?"NULL,":"N'"+timeStamp+"',";
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
			
			sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
			sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
			
			logger.info("pr_BoatRace_FinalQW_InsertData " + sSql);
			oCommonDB.execStoredProcedures("pr_BoatRace_FinalQW_InsertData ", sSql);	
			
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
				 saveEtoDB(raceID,timeStamp,IsFinal,eht,extractTime,CorruptedOdds);
			
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
				 saveQtoDB(raceID,timeStamp,IsFinal,qht,extractTime,CorruptedOdds);
			 
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void saveQtoDB(String raceID, String timeStamp,String IsFinal,Hashtable<String, String> qht, String extractTime,String CorruptedOdds) {
		try {
			String sSql ="";
			sSql+=raceID+",";
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
			
			sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
			sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
			
			logger.info("pr_BoatRace_FinalQ_InsertData " + sSql);
			oCommonDB.execStoredProcedures("pr_BoatRace_FinalQ_InsertData ", sSql);	
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void saveEtoDB(String raceID,String timeStamp,String IsFinal,Hashtable<String, String> eht, String extractTime,String CorruptedOdds) {
		try {
			String sSql ="";
			sSql+=raceID+",";
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
			
			sSql+=extractTime==null?"NULL,":"N'"+extractTime+"',";
			sSql+=CorruptedOdds==null?"NULL":"N'"+CorruptedOdds+"'";
//			sSql+=timeStamp==null?"NULL":"N'"+timeStamp+"'";
			
			logger.info("pr_BoatRace_FinalE_InsertData " + sSql);
			oCommonDB.execStoredProcedures("pr_BoatRace_FinalE_InsertData ", sSql);	
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void parse(String mainUrl,String oddsType) {
		try {
			String raceDate = oCommonMethod.getValueByPatter(mainUrl, "(\\d{8})").replaceAll("\\s", "");
			String raceNo = oCommonMethod.getValueByPatter(mainUrl, "rno=(\\d{1,2})&").replaceAll("\\s", "");
			String trackId = oCommonMethod.getValueByPatter(mainUrl, "jcd=(\\d{1,2})&").replaceAll("\\s", "");
			String body= pageHelper.doGet(mainUrl);
			String fileName=Controller.sSaveFilePath+File.separator+raceDate+File.separator+"trackId_"+trackId+File.separator+"raceNo_"+raceNo+"_"+oddsType+".html";
			FileDispose.saveFile(body, fileName);
			
			 while(raceNo.length()<2)raceNo= "0"+raceNo; 
			 while(trackId.length()<3)trackId= "0"+trackId; 
			 String RaceID =raceDate+trackId+raceNo;
			 //<p class="tab4_refreshText">オッズ更新時間 16:00</p>
			 String rawTimeStemp = oCommonMethod.getValueByPatter(body,"<p class=\"tab4_refreshText\">.*?(\\d{1,2}:\\d{1,2}).*?</p>");
			 
			 boolean isFinal=false;
			 String extractTime =oCommonMethod.getCurrentTime();
			 Date raceStartTime = CommonMethod.DateSubHour(yyyyMMddHHmm.parse(extractTime),1);
			 
			 String timeStamp =null;
			 if(rawTimeStemp.replaceAll("", "'").length()>1){
				 timeStamp = raceDate+" "+ rawTimeStemp;
			 }else{
				 timeStamp = yyyyMMddHHmm.format(raceStartTime);
//				 String tab_time = oCommonMethod.getValueByPatter(body, "<p class=\"tab4_time\">(.*?)</p>");
//				 if(tab_time.indexOf("締切")>-1){
					 isFinal =true;
//				 }
			 }
			if("odds2tf".equals(oddsType)){
				//dbo.Kyotei_finalQ dbo.Kyotei_finalE  --2連単オッズ --2連複オッズ
				logger.info("===============Start Parse FinalQE ======================");
				this.parseQE(fileName,RaceID,timeStamp,extractTime,isFinal);
			}else if("oddsk".equals(oddsType)){
				//dbo.Kyotei_finalQW  --拡連複
				logger.info("===============Start Parse FinalQW ======================");
				this.parseQW(fileName,RaceID,timeStamp,extractTime,isFinal);
			}else if("oddstf".equals(oddsType)){
				//dbo.Kyotei_finalP  dbo.Kyotei_finalW  --単勝オッズ --複勝オッズ
				logger.info("===============Start Parse FinalPW ======================");
				this.parsePW(fileName,RaceID,timeStamp,extractTime,isFinal);
			}else if("odds3t".equals(oddsType)){
				//dbo.Kyotei_finalTi  --3連単
				logger.info("===============Start Parse FinalTi ======================");
				this.parseTi(fileName,RaceID,timeStamp,extractTime,isFinal);
			}else if("odds3f".equals(oddsType)){
				//dbo.Kyotei_finalT   --3連複 
				logger.info("===============Start Parse FinalT ======================");
				this.parseT(fileName,RaceID,timeStamp,extractTime,isFinal);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void getOddsRaceUrl(String raceDate, RacePool rp) {
		try {
			//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170720
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay?hd="+raceDate;
			String basicBody = pageHelper.doGet(basicUrl);
//			rp.Clear();
			//<a href="/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517">11:01</a>
			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<span class=\"is-payout2");
//			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/racelist(.*?)\">(.*?)</a>");
			//?rno=1&jcd=02&hd=20170517
			while(m.find()){
				String url = m.group(1);
				rp.AddID("FinalOdds_"+url);
			}
		} catch (Exception e){
			logger.error("",e);
		}
	}
	
	private Vector<String> getOddsRaceUrl(String sraceDate) {
		Vector<String>  v= new Vector<String>();
		try {
			//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170516
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay?hd="+sraceDate;
			String basicBody = pageHelper.doGet(basicUrl);
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

	private boolean isAllNull(Hashtable<String, String> tiht) {
		boolean flag = false;
		try {
			for(Iterator<String> iterator=tiht.keySet().iterator();iterator.hasNext();){
				String key=iterator.next();
				if(tiht.get(key)!=null||!"null".equals(tiht.get(key))){
					flag = true;
					return flag;
				}
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		logger.info(tiht);
		logger.info("************************The data is all  null ************************");
		return flag;
	}
	
}
