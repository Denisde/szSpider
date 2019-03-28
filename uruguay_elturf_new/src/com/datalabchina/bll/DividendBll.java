package com.datalabchina.bll;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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

public class DividendBll implements Runnable{
	private static Logger logger = Logger.getLogger(DividendBll.class.getName());
	static PageHelper pageHelper = PageHelper.getPageHelper();
	static CommonDB oCommonDB =new CommonDB();
	Map<String,List<String>> map = new HashMap<String,List<String>>();
	 static CommonMethod oCommonMethod = new CommonMethod();
	 static Hashtable<String,String> dateHt = new Hashtable<String,String>();
	 public static void main(String[] args) {
		 DividendBll d = new DividendBll();
			d.run();
		}
	  @Override
		public void run() {
//		  	String fileName = "D:\\Denis\\elturf\\Test\\2017-3-28\\174918.html";
//		  	String fileName = "F:\\Denis\\elturf\\Test\\fix\\2624.html";
//		  	parseDividend(fileName);
		  	parseDividendFromDir();
		}
	  
		public DividendBll()
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
	  
	  
	  public void parseDividend(String fileName){
		  String body = FileDispose.readFile(fileName);
			if(body.length()<1) {
				logger.error("==============body is empty !!!==================");
				return;
			}
			try {
				String raceId = oCommonMethod.getValueByPatter(fileName, "(\\d{1,6}).html");
				
				String  rawRaceDate =  oCommonMethod.getValueByPatter(body, "<td class=\"text-right\" style=\"width:45%;\">(.*?)</td>");
				if(rawRaceDate==null||"".equals(rawRaceDate))
					rawRaceDate =   oCommonMethod.getValueByPatter(body, "<div class=\"col-xs-6 col-sm-6 col-md-6 col-lg-6 text-right\">(.*?)</div>");
				
//				System.out.println(rawRaceDate);
				String raceDate = getRaceDate(rawRaceDate);
				
				Hashtable<String,String> ht = new Hashtable<String,String>();
				Matcher dividendMatcher= oCommonMethod.getMatcherStrGroup(body, "<table class=\"table table-bordered table-hover table-condensed\">(.*?)</table>");
				while(dividendMatcher.find()){
					String dividengTable1 = dividendMatcher.group(1);
					Parser trParser = Parser.createParser(dividengTable1, null);
					Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
					List<String> GanadorList = new ArrayList<String>();	
					List<String> SegundoList= new ArrayList<String>();	
					List<String> TerceroList= new ArrayList<String>();	
					for (int j= 2; j < trNodes.length; j++) {
						String value = trNodes[j].toHtml();
						Parser tdParser = Parser.createParser(value, null);
						Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
						if(tdNodes.length==4){
							String horseNameAndClothNo = tdNodes[0].toPlainTextString();
							String clothNo = oCommonMethod.getValueByPatter(horseNameAndClothNo, "\\((.*?)\\)");
//							horseNameAndClothNoht.put("", value)
							ht.put((j-1)+"",clothNo);
	//						String horseName = horseNameAndClothNo.replace("(","").replace(")","").replace(clothNo, "").trim();
							GanadorList.add(tdNodes[1].toPlainTextString());
							SegundoList.add(tdNodes[2].toPlainTextString());
							TerceroList.add(tdNodes[3].toPlainTextString());
						}
					}
					if(GanadorList.size()<1)continue;
					String arr[]={"Ganador","Segundo1","Segundo2","Tercero1","Tercero2","Tercero3"};
					for(int i=0;i<arr.length;i++){
						String betTypeName = arr[i];
						String Combination = getCombination(betTypeName, ht);
						String H1 = null,H2 = null,H3 = null,H4 = null,H5=null;
						Map<String,String> map = getHValue(Combination);
						H1= (String)map.get("H1");
						H2= (String)map.get("H2");
						H3= (String)map.get("H3");
						H4= (String)map.get("H4");
						H5= (String)map.get("H5");
						H1 = "null".equals(H1)?null:H1;
						H2 = "null".equals(H2)?null:H2;
						H3 = "null".equals(H3)?null:H3;
						String rawDividend = getDividend(betTypeName,GanadorList,SegundoList,TerceroList);
						
						 String dividend = convertDividend(rawDividend);
						 saveDividendToDB(raceId,raceDate,betTypeName,Combination,dividend,rawDividend,H1,H2,H3,H4,H5);
					}
				}
				//******************************************************
				String dividendtable = oCommonMethod.getValueByPatter(body, "<table class=\"table table-bordered table-hover table-condensed\">\\s*<thead>" +
						"\\s*<tr class=\"active elturf_align_top\">\\s*<th colspan=\"2\" class=\"text-center\">\\s*<strong>Dividendos</strong>\\s*</th>\\s*</tr>\\s*</thead>(.*?)</table>");
				Parser trParser = Parser.createParser(dividendtable, null);
				Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
				for(int i=0;i<trNodes.length;i++){
					String value =trNodes[i].toHtml();
					Parser tdParser = Parser.createParser(value, null);
					Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
					String betTypeName = tdNodes[0].toPlainTextString();
					String  Rawdividend  =tdNodes[1].toPlainTextString();
					String dividend = convertDividend(Rawdividend);
					
					 String Combination = getCombination(betTypeName, ht);
					 String H1 = null,H2 = null,H3 = null,H4 = null,H5=null;
					 Map<String,String>  map = getHValue(Combination);
					 H1= (String)map.get("H1");
					 H2= (String)map.get("H2");
					 H3= (String)map.get("H3");
					 H4= (String)map.get("H4");
					 H5=(String)map.get("H5");
					 H1 = "null".equals(H1)?null:H1;
					 H2 = "null".equals(H2)?null:H2;
					 H3 = "null".equals(H3)?null:H3;
					 saveDividendToDB(raceId,raceDate,betTypeName,Combination,dividend,Rawdividend,H1,H2,H3,H4,H5);
				}
			} catch (Exception e) {
				logger.error("",e);
			}
	  }
	  
	  
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
	  
		private String convertDividend(String dividend) {
			try {
				if(dividend==null||dividend.trim().length()<1) return null;
				dividend = dividend.replace(",", ".").replaceAll(" ","").trim();
				if(dividend.split("\\.").length==3)
				{
					dividend = dividend.replaceFirst("\\.", "");
				}
				if(dividend.contains("Grupo")||dividend.contains("-")){
					return null;
				}
				if(oCommonMethod.getMatcherStrGroup(dividend, "(\\d{1,4}\\.?\\d{1,4})").find()){
					dividend =oCommonMethod.getValueByPatter(dividend, "(\\d{1,4}\\.?\\d{1,4})");
				}
				
			} catch (Exception e){
				logger.error("",e);
			}
			return dividend;
		}
	  private Map<String,String>  getHValue(String combination) {
			Map<String,String> map = new HashMap<String,String>();
				try {
					if(combination==null)return null;
					String Harr[]= combination.split("-");
					for(int i = 0;i<Harr.length;i++){
						map.put("H"+(i+1), Harr[i]);
					}
				} catch (Exception e) {
					logger.error("",e);
				}
			return map;
		}
	  
		private String getCombination(String betTypeName,Hashtable<String,String> ht)
		{
			String Combination= null;
			try {
					if("Ganador".equals(betTypeName)){
						Combination = ht.get(1+"").toString();
					}else if("Segundo1".equals(betTypeName)){
						Combination = ht.get(1+"")+"-"+ht.get(2+"");
					}else if("Segundo2".equals(betTypeName)){
						Combination = ht.get(1+"")+"-"+ht.get(2+"");
					}else if("Tercero1".equals(betTypeName)){
						Combination = ht.get(1+"")+"-"+ht.get(2+"")+"-"+ht.get(3+"");
					}else if("Tercero2".equals(betTypeName)){
						Combination = ht.get(1+"")+"-"+ht.get(2+"")+"-"+ht.get(3+"");
					}else if("Tercero3".equals(betTypeName)){
						Combination = ht.get(1+"")+"-"+ht.get(2+"")+"-"+ht.get(3+"");
					}else if(betTypeName.contains("Place")){
						Combination = ht.get(1+"").toString();
					}else if(betTypeName.contains("Exa")||betTypeName.contains("Qui")||betTypeName.contains("Dob")){
						Combination = ht.get(1+"")+"-"+ht.get(2+"");
					}else if(betTypeName.contains("Tri")||betTypeName.contains("Superfecta")){
						Combination = ht.get(1+"")+"-"+ht.get(2+"")+"-"+ht.get(3+"");
					}else {
						Combination ="999";
					}
			} catch (Exception e) {
				logger.error("",e);
			}
			return Combination;
		}
	  
	  private String getDividend(String betTypeName, List<String> ganadorList, List<String> segundoList, List<String> terceroList) {
			String dividend = null;
			try {
				if("Ganador".equals(betTypeName)){
					dividend = ganadorList.get(0).toString().replaceAll("&nbsp;", "").trim();
				} else if("Segundo1".equals(betTypeName)) {
					dividend = segundoList.get(0).toString().replaceAll("&nbsp;", "").trim();
				} else if("Segundo2".equals(betTypeName)) {
					dividend = segundoList.get(1).toString().replaceAll("&nbsp;", "").trim();
				} else if("Tercero1".equals(betTypeName)) {
					dividend = terceroList.get(0).toString().replaceAll("&nbsp;", "").trim();
				} else if("Tercero2".equals(betTypeName)) {
					dividend =terceroList.get(1).toString().replaceAll("&nbsp;", "").trim();
				} else if("Tercero3".equals(betTypeName)) {
					dividend = terceroList.get(2).toString().replaceAll("&nbsp;", "").trim();
				}
			} catch (Exception e) {
				logger.error("",e);
			}
			return dividend;
		}
	  
	 private void saveDividendToDB(String raceID, String raceDate,String betTypeName, 
				String combination, String dividend,String rawDividend,String H1,String H2,String H3,String H4,String H5) {
			try {
				String sSql ="";
				sSql+=raceID+",";
				sSql+="N'"+raceDate+"',";
				sSql+="NULL,";
				sSql+=betTypeName==null?"NULL,":"N'"+betTypeName+"',";
				sSql+=combination==null?"NULL,":"N'"+combination+"',";
				sSql+=H1==null?"NULL,":"N'"+H1+"',";
				sSql+=H2==null?"NULL,":"N'"+H2+"',";
				sSql+=H3==null?"NULL,":"N'"+H3+"',";
				sSql+=H4==null?"NULL,":"N'"+H4+"',";
				sSql+=H5==null?"NULL,":"N'"+H5+"',";
				sSql+=dividend==null?"NULL,":"N'"+dividend+"',";
//				sSql+=dividend==null||dividend.length()<1||!CommonFun.isDecimal(dividend)?"NULL,":"N'"+dividend.replaceAll(",","\\.").replaceAll("\\$","")+"',";
				sSql+=oCommonMethod.getCurrentTime()==null?"NULL,":"N'"+oCommonMethod.getCurrentTime()+"',";
				sSql+=rawDividend==null?"NULL":"N'"+rawDividend.replaceAll("&nbsp;", "").replaceAll("&nbsp", "")+"'";
				logger.info("pr_Elturf_Dividend_InsertData " + sSql);
				oCommonDB.execStoredProcedures("pr_Elturf_Dividend_insertData",sSql);
			}
			catch (Exception e) 
			{
				logger.error("",e);
			}
		}

	 public void parseDividendFromDir(){
		 try {
//				parseDividend("F:\\Denis\\elturf\\Test\\171460.html");
				Vector<String> vDate = oCommonMethod.getHistoryDate("20170101","20170619");
				for(int i=0;i<vDate.size();i++)
				{
					String raceDate = vDate.get(i);
					String filePath = Controller.sSaveFilePath+File.separator+raceDate;
					List<String > list = FileDispose.readLocalFileDir(filePath);
					for(int j=0;j<list.size();j++)
					{
						String fileName = list.get(j);
						parseDividend(fileName);
					}
				}
		} catch (Exception e) {
			logger.error("",e);
		}
	 }
}
