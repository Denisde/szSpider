	package com.datalabchina.bll;
	
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
import com.datalabchina.common.PageHelper;
	
	public class DividendBll implements Runnable{
		private static Logger logger = Logger.getLogger(DividendBll.class.getName());
		static PageHelper pageHelper = PageHelper.getPageHelper();
		static CommonDB oCommonDB =new CommonDB();
		static CommonMethod oCommonMethod = new CommonMethod();
		private Hashtable<String,String> htBetType = null;
		boolean isGetHistoryUrl = false;
		@Override
		public void run() {
			String startDate = Controller.sStartDate;
			String endDate = Controller.sEndDate;
//			startDate = "20170906";
//			endDate = "20170906";
			Vector<String> vDate = CommonMethod.getBetwenneDate(startDate, endDate);
			for(int i =0;i<vDate.size();i++){
				String raceDate =vDate.get(i);
				getMainPage(raceDate);
			}
			
		}
		
		private void getMainPage(String sraceDate) {
			Vector<String> vUrl = getPostRaceUrl(sraceDate);
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/raceresult";
			try {
				//?rno=1&jcd=02&hd=20170517_11:26
				for(int i=0;i<vUrl.size();i++){
					//http://www.boatrace.jp/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517
					String mainUrl =basicUrl+vUrl.get(i).split("_")[0];
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
		
		//http://www.boatrace.jp/owpc/pc/race/raceresult?rno=1&jcd=03&hd=20170720
		private Vector<String> getPostRaceUrl(String sraceDate) {
			Vector<String>  v= new Vector<String>();
			try {
				//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170516
				String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay?&hd="+sraceDate;
				String basicBody = pageHelper.doGet(basicUrl);
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
		
		public static void main(String[] args) {
			DividendBll p = new DividendBll();
			p.run();
//			p.parse(null);
		}
		public DividendBll()
		{
			htBetType = new Hashtable<String,String>();
			htBetType.put("Win", "1");
			htBetType.put("Place", "2");
			htBetType.put("Exacta", "3");
			htBetType.put("Quinella", "4");
			htBetType.put("Tierce", "5");
			htBetType.put("Trio", "6");
			htBetType.put("QuinellaPlace", "7");
		}
		private void parse(String body,String RaceDate,String RaceNo,String TrackID) {
//			fileName = "D:\\Denis\\Jpboat\\test\\20170720\\trackId_11\\raceNo_3.html";
//			String body = FileDispose.readFile(fileName);
			if(body==null||body.length()<100) {logger.error("raceBody  is empty please check !!!!!!!!!!!!!!!!!!");return;}	
			String RaceID=null, Uraceid=null,   ExtractTime=null;
			while(RaceNo.length()<2)RaceNo= "0"+RaceNo; 
			while(TrackID.length()<3)TrackID= "0"+TrackID; 
			RaceID =RaceDate+TrackID+RaceNo;
			Uraceid = RaceDate+"12"+TrackID+RaceNo;
			try {
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

//							 System.out.println(BetTypeName+"++++++++"+Dividend+Combination+Popularity);
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
//							 System.out.println(BetTypeName+"++++++++"+Dividend+Combination+Popularity);
							 saveDividendToDB(RaceID, Uraceid, RaceDate, RaceNo, BetTypeID, Combination, Dividend, Popularity, ExtractTime, BetTypeName);
						 }
					 }
				 }
			} catch (Exception e) {
				logger.error("",e);
			}finally{
					logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> start to exec pr_Consolidate_Dividend ,'"+Uraceid+"',"+Uraceid+"'");
					oCommonDB.execStoredProcedures("pr_Consolidate_Dividend ", "'"+Uraceid+"','"+Uraceid+"'");
			}
		}
		public static String convertString(String str){
			if(str==null||str.replaceAll("\\s","").trim().length()<1)return null;
			else return str;
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
	}	
