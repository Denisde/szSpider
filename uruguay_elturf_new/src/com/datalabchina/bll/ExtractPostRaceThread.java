package com.datalabchina.bll;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

public class ExtractPostRaceThread implements Runnable{
	private static Logger logger = Logger.getLogger(ExtractPostRaceThread.class.getName());
	static PageHelper pageHelper = PageHelper.getPageHelper();
	static CommonDB oCommonDB =new CommonDB();
	 static CommonMethod oCommonMethod = new CommonMethod();
	static Hashtable<String,String> dateHt = new Hashtable<String,String>();
	RacePool rp =  null;
	
	
	public ExtractPostRaceThread(RacePool rp){
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
		this.rp = rp;
	}
	
	@Override
	public void run() 
	{
		for(int i=0;i<rp.getVector().size();i++)
		{
			String raceDate = (String) rp.getVector().get(i);
			String sraceDate =  convertRaceDate(raceDate);
			List<String> raceUrlList = getHistoryPage(sraceDate);
			getRacePageAndParse(raceUrlList,sraceDate);
			raceDate = rp.GetID();
		}
	}

	public void parse() {
		String startDate =Controller.sStartDate;
		String endDate =Controller.sEndDate;
		Vector<String> vDate = oCommonMethod.getHistoryDate(startDate,endDate);
		for(int i=0;i<vDate.size();i++) {
			String rawRaceDate = vDate.get(i);
			String raceDate =  convertRaceDate(rawRaceDate);
			List<String> raceUrlList = getHistoryPage(raceDate);
			getRacePageAndParse(raceUrlList,rawRaceDate);
		}
	}
	
//		String fileName = "D:\\Denis\\elturf\\Test\\20170329\\174918.html";
//		String sPathName = "D:\\Denis\\elturf\\Test";
	public void readDataFromDir(String sPathName){
		List<String>  fileList = FileDispose.readLocalFileDir(sPathName);
		for(int i=0 ;i<fileList.size();i++){
			String fileName =fileList.get(i); 
			parsePage(fileName);
		}
	}
	
	public void fixRaceByIdFromSql(){
		List<String> idlist = oCommonDB.getRaceId();
		for(int i=0;i<idlist.size();i++){
			String id = idlist.get(i);
			test(id);
		}
	}
	

	//http://www.elturf.com/elturfcom/carreras-resultado-ver?id_carrera=173658
	public void test(String raceId){
		String url ="http://www.elturf.com/elturfcom/carreras-resultado-ver?id_carrera="+raceId;
		String body = pageHelper.doGet(url);
		String fileName =Controller.sSaveFilePath+File.separator+"test"+File.separator+oCommonMethod.getValueByPatter(url, "(\\d{2,6})")+".html";
		FileDispose.saveFile(body, fileName);
		parsePage(fileName);
	}
	
	private String convertRaceDate(String rawRaceDate) {
		try {
			if(rawRaceDate==null||rawRaceDate.length()!=8){
				logger.error("The date format is incorrect ,please check !!!");
				return "2017-1-1";	
			}
			return rawRaceDate.substring(0,4)+"-"+rawRaceDate.substring(4,6)+"-"+rawRaceDate.substring(6);
		} catch (Exception e) {
			logger.error("",e);
		}
		return "2017-1-1";
	}

	public ExtractPostRaceThread()
	{
//		login();
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
	
	//http://www.elturf.com/elturfcom/carreras-resultado-ver?id_carrera=158415
	private void getRacePageAndParse(List<String> raceUrlList,String raceDate) {
		if(raceUrlList==null){
			logger.error("================urlList is null!  please check!!!=================");
			return;
		}
		try {
			for(int i=0;i<raceUrlList.size();i++){
				String url = "http://www.elturf.com/elturfcom/"+raceUrlList.get(i);
				//10890,10811,10758,10711
//				url ="http://elturf.com/elturfcom/carreras-resultado-ver?id_carrera=129195";
				String body = pageHelper.doGet(url);
				String fileName =Controller.sSaveFilePath+File.separator+raceDate+File.separator+oCommonMethod.getValueByPatter(url, "id_carrera=(\\d{1,6})")+".html";
				FileDispose.saveFile(body, fileName);
				parsePage(fileName);
			}
		} catch (Exception e){
			logger.error("",e);
		}
	}

	public void fixRaceByraceId(String raceId){
		try {
			String url = "http://elturf.com/elturfcom/carreras-resultado-ver?id_carrera="+raceId;
			//10890,10811,10758,10711
//			url ="http://elturf.com/elturfcom/carreras-resultado-ver?id_carrera=129195";
			String body = pageHelper.doGet(url);
			String fileName =Controller.sSaveFilePath+File.separator+"fix"+File.separator+oCommonMethod.getValueByPatter(url, "(\\d{2,6})")+".html";
			FileDispose.saveFile(body, fileName);
			parsePage(fileName);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void parsePage(String fileName) {
		String body = FileDispose.readFile(fileName);
		if(body.length()<1||body.trim().equals("404")) {
			logger.error("==============body is empty !!!==================");
			return;
		}
		try {
			String RawTrackName = oCommonMethod.getValueByPatter(body, "<td class=\"text-left\" style=\"width:45%;\">(.*?)</td>");
//			System.out.println(RawTrackName);
			String trackName =RawTrackName.replaceAll("\\d","").replace("Reunión", "").trim(); 
			String  rawRaceDate =  oCommonMethod.getValueByPatter(body, "<td class=\"text-right\" style=\"width:45%;\">(.*?)</td>");
//			System.out.println(rawRaceDate);
			String raceDate = getRaceDate(rawRaceDate);
			String raceNo = oCommonMethod.getValueByPatter(body, "<h3><strong>(\\d{1,2}).*?</strong>");
			String officeGoing =null;
			/*
			 * <td class="text-left" style="width:47.5%;">
				<h3><strong>1ª</strong> <small>(1124)</small></h3>Handicap (Hd)
 				<br>Pr. <strong>"Población Emergencia"</strong>
				<br>Productos tres años y más (P3ay+)
				<br>Indice 1 e Inf.
				<br>1000m  Arena (Normal)<br>14:00 hrs.<!--1-->
				</td>* */
			String raceInfo = oCommonMethod.getValueByPatter(body,"<td class=\"text-left\" style=\"width:47.5%;\">(.*?)</td>");
//			System.out.println(raceInfo);
			String actualStartTime = oCommonMethod.getValueByPatter(raceInfo, "(\\d{2}:\\d{2})");
			String meetingNo =  oCommonMethod.getValueByPatter(raceInfo, "<small>\\((.*?)\\)</small>\\s*</h3>");
			String distance =oCommonMethod.getValueByPatter(raceInfo, "(\\d{3,4})m");
			String arr[] = raceInfo.split("<br>");
			String raceName = null;
			String raceTypeAge = null;
			String surfaceName = null;
			if(arr.length==6){
				raceName  = (arr[1].replaceAll("<.*?>", "")+arr[2]).replaceAll("'", "");
				raceTypeAge = arr[3];
				surfaceName =arr[4].replace(distance+"m","").trim();
			}else if(arr.length==5){
				raceName  = (arr[1].replaceAll("<.*?>", "")+arr[2]).replaceAll("'","''");
				surfaceName =arr[3].replace(distance+"m","").trim();
			}
			//<strong>Tiempo:</strong> 0.58.76 1000m Arena (Normal)</div>
			String rawleaderFinishTime = oCommonMethod.getValueByPatter(body, "<strong>Tiempo:</strong>(.*?)</div>");
			String leaderFinishTime = getLeaderFinishTime(rawleaderFinishTime);
			/*
			 * <td class="text-right" style="width:47.5%;"><!--2-->
			 * <br><strong>$750.000</strong>  al 1ro<br>
			 * <strong>$180.000</strong>  al 2do<br>
			 * <strong>$105.000</strong>  al 3ro<br>
			 * <br><br>Bolsa de Premios
				<br><strong>$1.035.000</strong><!--2--></td>
			 * */
			String prizeInfo = oCommonMethod.getValueByPatter(body, "<td class=\"text-right\" style=\"width:47.5%;\">(.*?)</td>");
			String prize =oCommonMethod.getValueByPatter(body, "<br>\\s*Bolsa de Premios\\s*<br>\\s*<strong>\\$(.*?)</strong>").replaceAll("\\.", "");
//			System.out.println(prizeInfo);
			Matcher m = oCommonMethod.getMatcherStrGroup(prizeInfo, "<strong>\\$(.*?)</strong>\\s*al\\s*(\\d{1,2}).*?<br>");
			Hashtable<String,String> ht = new Hashtable<String,String>();
			while(m.find()){
//				System.out.println(m.group(1)+"-------------------"+m.group(2));
				ht.put("prize"+m.group(2),m.group(1).replaceAll("\\.", ""));
			}
			String raceTitle =null;
			String rawCategory =null;
			String raceId = oCommonMethod.getValueByPatter(fileName, "(\\d{1,6}).html");
			savePostRaceToDB(raceDate,trackName,raceNo,officeGoing,actualStartTime,raceName,leaderFinishTime,surfaceName,prize,
					ht,raceTitle,raceTypeAge,distance,meetingNo,raceId,rawleaderFinishTime,rawCategory);
			
			String horseInfo = oCommonMethod.getValueByPatter(body, "<table class=\"table table-hover table-condensed\">(.*?)</table>");
			parseHorseInfo(horseInfo,raceDate,trackName,raceNo,raceId);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	
	private void parseHorseInfo(String horseInfo,String raceDate,String trackName,String raceNo,String raceId) {
		try {
			Parser trParser = Parser.createParser(horseInfo, null);
			Node[] trNodes = trParser.extractAllNodesThatAre(TableRow.class);
			
			for (int j= 2; j < trNodes.length; j++) {
				String value = trNodes[j].toHtml();
				Parser tdParser = Parser.createParser(value, null);
				Node[] tdNodes = tdParser.extractAllNodesThatAre(TableColumn.class);
				String RqwFinishPosition = tdNodes[0].toPlainTextString().replace("°", "");
//				String finishPosition = oCommonMethod.getValueByPatter(RqwFinishPosition, "(\\d{1,2})");
				
				String RawClothNo =tdNodes[1].toPlainTextString();
				String clothNo = oCommonMethod.getValueByPatter(RawClothNo, "(\\d{1,2})");
				
				
				String horseName = covertString(oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"<strong>(.*?)</strong>"));
				//<span class=""> (Crary)</span>
				String sire =  covertString(oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"<span class=\"\">\\s*\\((.*?)\\)\\s*</span>"));
				
				String webHorseId = oCommonMethod.getValueByPatter(tdNodes[2].toHtml(),"caja_menu_ejemplar_(\\d{3,7})_").trim();
				
				HorseBll hb = new HorseBll();
				hb.parsePage(webHorseId);
				
				//<td class="text-center esconder_resultados_phone1 esconder_resultados_phone2 esconder_resultados_tablet1 esconder_resultados_tablet2">3a</td>
				String horseAgeDesc = tdNodes[3].toPlainTextString();
//				<td class="text-right esconder_resultados_phone1 esconder_resultados_phone2 esconder_resultados_tablet1 esconder_resultados_tablet2">404k</td>
				String horseWeight = covertString(tdNodes[4].toPlainTextString().replace("k", ""));
				if(!oCommonMethod.isNumber(horseWeight))horseWeight =null;
				
//				<td class="text-center">pz</td>
				String rawbeatenDistance = tdNodes[5].toPlainTextString().trim().length()<1?null:tdNodes[5].toPlainTextString();
				
				String beatenDistance =GetBeatenDistance(rawbeatenDistance); 
//				System.out.println(GetBeatenDistance(rawbeatenDistance));
				
				String hanciapWeight = tdNodes[6].toPlainTextString();
				if(!oCommonMethod.isNumber(hanciapWeight))hanciapWeight =null;
				if(hanciapWeight!=null&&hanciapWeight.trim().length()<1)hanciapWeight =null;
				
				
				String jockeyName =  covertString(tdNodes[7].toPlainTextString());
				
				String trainerName =  covertString(tdNodes[8].toPlainTextString());
				//11|
				String winOdds =covertString( tdNodes[9].toPlainTextString().replace(",", ".").replaceAll("|", ""));
				if(winOdds!=null&&winOdds.split("\\.").length==3)winOdds = winOdds.replaceFirst("\\.", "");
				if(winOdds!=null){
					if(!oCommonMethod.isNumber(winOdds.replaceAll("\\.", ""))){
						winOdds = null;
					}
				}
				
				
				String scratch = "0";
				if(RqwFinishPosition.indexOf("U")>-1)scratch ="1";
				String horseorgin = null;
				String Dam=null;
				String sireOfDam = null;
				String finishpositionSeq =null;
				savePostHorseToDB(raceDate,trackName,raceNo,webHorseId,horseName,horseorgin,clothNo,jockeyName,sire,
						Dam,sireOfDam,horseAgeDesc,rawbeatenDistance,winOdds,hanciapWeight,horseWeight,RqwFinishPosition,finishpositionSeq,scratch,
						beatenDistance,RawClothNo,raceId,trainerName);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	   private static String  covertString(String str){
	    	if(str==null||str.trim().length()<1)
	    		return null;
	    	else
	    		return str.trim().replaceAll("'", "''");
	    }
	private void savePostHorseToDB(String raceDate, String trackName,
			String raceNo, String webHorseId, String horseName,
			String horseorgin, String ClothNo, String jockeyName,
			String sire, String dam, String sireOfDam, String horseAgeDesc,
			String rawbeatenDistance, String winOdds, String hanciapWeight,
			String horseWeight, String rqwFinishPosition,
			String finishpositionSeq, String scratch, String beatenDistance,
			String rawClothNo, String raceId,String trainerName) {
		try {
			String sSql ="";
			sSql+=raceDate==null?"NULL,":"N'"+raceDate+"',";
			sSql+=trackName==null?"NULL,":"N'"+trackName+"',";
			sSql+=raceNo==null?"NULL,":"N'"+raceNo+"',";
			sSql+=webHorseId==null?"NULL,":"N'"+webHorseId+"',";
			sSql+=horseName==null?"NULL,":"N'"+horseName+"',";
			sSql+=horseorgin==null?"NULL,":"N'"+horseorgin+"',";
			sSql+=ClothNo==null?"NULL,":"N'"+ClothNo+"',";
			sSql+=jockeyName==null?"NULL,":"N'"+jockeyName+"',";
			sSql+=sire==null?"NULL,":"N'"+sire+"',";
			sSql+=dam==null?"NULL,":"N'"+dam+"',";
			sSql+=sireOfDam==null?"NULL,":"N'"+sireOfDam+"',";
			sSql+=horseAgeDesc==null?"NULL,":"N'"+horseAgeDesc+"',";
			sSql+=rawbeatenDistance==null?"NULL,":"N'"+rawbeatenDistance+"',";
			sSql+=winOdds==null?"NULL,":"N'"+winOdds+"',";
			sSql+=hanciapWeight==null?"NULL,":"N'"+hanciapWeight+"',";
			sSql+=horseWeight==null?"NULL,":"N'"+horseWeight+"',";
			sSql+=rqwFinishPosition==null?"NULL,":"N'"+rqwFinishPosition+"',";
			sSql+=finishpositionSeq==null?"NULL,":"N'"+finishpositionSeq+"',";
			sSql+=scratch==null?"NULL,":"N'"+scratch+"',";
			sSql+="N'"+oCommonMethod.getCurrentTime()+"',";
			sSql+=beatenDistance==null?"NULL,":"N'"+beatenDistance+"',";
			sSql+=rawClothNo==null?"NULL,":"N'"+rawClothNo+"',";
			sSql+=raceId==null?"NULL,":"N'"+raceId+"',";
			sSql+=trainerName==null?"NULL":"N'"+trainerName+"'";
			
			logger.info("pr_Elturf_PostRace_Horse_InsertData sql :" + sSql);
			oCommonDB.execStoredProcedures("pr_Elturf_PostRace_Horse_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	private void savePostRaceToDB(String raceDate, String trackName,
			String raceNo, String officeGoing, String actualStartTime,
			String raceName, String leaderFinishTime, String surfaceName,String prize,
			Hashtable<String, String> ht, String raceTitle, String raceTypeAge,
			String distance, String meetingNo, String raceId,
			String rawleaderFinishTime, String rawCategory) {
		try {
			String sSql ="";
			sSql+=raceDate==null?"NULL,":"N'"+raceDate+"',";
			sSql+=trackName==null?"NULL,":"N'"+trackName+"',";
			sSql+=raceNo==null?"NULL,":"N'"+raceNo+"',";
			sSql+=officeGoing==null?"NULL,":"N'"+officeGoing+"',";
			sSql+=actualStartTime==null?"NULL,":"N'"+actualStartTime+"',";
			sSql+=raceName==null?"NULL,":"N'"+raceName+"',";
			sSql+=leaderFinishTime==null?"NULL,":"N'"+leaderFinishTime+"',";
			sSql+=surfaceName==null?"NULL,":"N'"+surfaceName+"',";
			sSql+=prize==null?"NULL,":"N'"+prize+"',";
			sSql+=ht.get("prize1")==null?"NULL,":"N'"+ht.get("prize1")+"',";
			sSql+=ht.get("prize2")==null?"NULL,":"N'"+ht.get("prize2")+"',";
			sSql+=ht.get("prize3")==null?"NULL,":"N'"+ht.get("prize3")+"',";
			sSql+=ht.get("prize4")==null?"NULL,":"N'"+ht.get("prize4")+"',";
			sSql+=ht.get("prize5")==null?"NULL,":"N'"+ht.get("prize5")+"',";
			sSql+=ht.get("prize6")==null?"NULL,":"N'"+ht.get("prize6")+"',";
			sSql+=ht.get("prize7")==null?"NULL,":"N'"+ht.get("prize7")+"',";
			sSql+=ht.get("prize8")==null?"NULL,":"N'"+ht.get("prize8")+"',";
			sSql+=ht.get("prize9")==null?"NULL,":"N'"+ht.get("prize9")+"',";
			sSql+=ht.get("prize10")==null?"NULL,":"N'"+ht.get("prize10")+"',";
			sSql+=raceTitle==null?"NULL,":"N'"+raceTitle+"',";
			sSql+=raceTypeAge==null?"NULL,":"N'"+raceTypeAge+"',";
			sSql+=distance==null?"NULL,":"N'"+distance+"',";
			sSql+=meetingNo==null?"NULL,":"N'"+meetingNo+"',";
			sSql+="N'"+oCommonMethod.getCurrentTime()+"',";
			sSql+=raceId==null?"NULL,":"N'"+raceId+"',";
			sSql+=rawleaderFinishTime==null?"NULL,":"N'"+rawleaderFinishTime.replaceAll("'", "''")+"',";
			sSql+=rawCategory==null?"NULL":"N'"+rawCategory+"'";
			
			logger.info("pr_Elturf_PostRace_Race_InsertData sql :" + sSql);
			oCommonDB.execStoredProcedures("pr_Elturf_PostRace_Race_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	public static String GetBeatenDistance(String strHtml) {
		if(strHtml==null)return null;
		double dBeatenDistance = 0d;
		try
		{			
			String str = strHtml.trim();
			if (str == null || str.equals("") || str.length()==0)
				return null;
			
			String patter = "(\\d{1,3})?(¼|¾|½)";
			Matcher matcher = oCommonMethod.getMatcherStrGroup(str, patter);
			if(matcher.find())
			{
				int iFirst = 0;
				if(matcher.group(1)!=null)
					iFirst = Integer.parseInt(matcher.group(1));
				String fenshu = matcher.group(2);
				if(fenshu.equals("½"))
					dBeatenDistance = iFirst + 0.5;
				else if(fenshu.equals("¼"))
					dBeatenDistance = iFirst + 0.25;
				else if(fenshu.equals("¾"))
					dBeatenDistance = iFirst + 0.75;
				return dBeatenDistance+"";
			}
		    if (oCommonMethod.isNumber(str)) 
		    {
				dBeatenDistance = Double.parseDouble(str);
		    }
		    else
		    {
		    	String bdstr = oCommonMethod.getValueByPatter(str, "([A-Za-z]{2,15})");
		    	if(bdstr!=null)
		    	{
//		    		return GetBeatenDistanceValuebyName(bdstr).getBeatendistanceValue();
		    	}
		    	else
		    	{
//		    		return null;
		    	}
			}
		} 
		catch (Exception e) 
		{			
			logger.error(e.toString());
		}
		return dBeatenDistance+"";		
	}
	
	
	private String getLeaderFinishTime(String leaderFinishTime)
	{
//		1.06.01 //	1.13    //no reg.   //"1:14:68"    //12,01 //0.57.3/5 
		//Tiempo: 59.33
		try
		{
			leaderFinishTime = oCommonMethod.getValueByPatter(leaderFinishTime, "(\\d{1,2}\\.\\d{1,2}\\.\\d{1,2})").trim();
			if(leaderFinishTime.trim().length()<1){
				leaderFinishTime = oCommonMethod.getValueByPatter(leaderFinishTime, "(\\d{1,2}\\.\\d{1,2})").trim();
			}
			
//			if(leaderFinishTime==null) return"0.00";
			if(leaderFinishTime==null||leaderFinishTime.trim().contains("no reg")||leaderFinishTime.trim().length()<1)
			{
				return"0.00";
			}
			
			leaderFinishTime = leaderFinishTime.replaceAll(":", "\\.").replaceAll(",", "\\.");
			String[] array = leaderFinishTime.split("\\.");
			int minutes = Integer.parseInt(array[0])*60;
			double seconds =0;
			if(array.length==3){
				if(array[2].contains("/"))
				{
					array[2] = array[2].substring(0,array[2].indexOf("/")).trim();
				}
				if(array[1].contains("/"))
				{
					array[1] = array[1].substring(0,array[1].indexOf("/")).trim();
				}
				seconds = Double.parseDouble(array[1].trim()+"."+array[2].trim());
			}
			//0.57 4/5
			else if(array.length==2){
				if(array[1].contains(" ")&&array[1].contains("/")){
					array[1] = array[1].substring(0,array[1].indexOf("/")).trim().split(" ")[0]+"."+array[1].substring(0,array[1].indexOf("/")).trim().split(" ")[1];
				}
				seconds = Double.parseDouble(array[1]);
			}
			return (seconds+minutes)+"";
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return "0";
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

	//http://www.elturf.com/elturfcom/carreras-ultimos-resultados
	//http://www.elturf.com/elturfcom/carreras-buscar-programas?fecha_reunion=2017-3-28#
	private List<String>  getHistoryPage(String raceDate) {
//		String raceDate ="2017-3-28";
		String  raceUrl ="http://www.elturf.com/elturfcom/carreras-buscar-programas?fecha_reunion="+raceDate+"#";
		String body = pageHelper.doGet(raceUrl);
		List<String> raceUrlList = new ArrayList<String>();
		try {
			//carreras-resultado-ver?id_carrera=174918
			Matcher m = oCommonMethod.getMatcherStrGroup(body, "<a href=\"(carreras-resultado-ver.*?)\">");
			while(m.find()){
				String oneRaceUrl = m.group(1);
				if(oneRaceUrl.indexOf("class")>-1) continue;
				raceUrlList.add(oneRaceUrl);
			}
		} catch (Exception e) {
			logger.info("",e);
		}
		return raceUrlList;
	}

	
	private boolean login() {
		boolean flag = true;
		//http://elturf.com/elturfcom/dashboard-login-in
		String loginurl="http://elturf.com/elturfcom/dashboard-login-in";
		String refer = "http://elturf.com/elturfcom/dashboard-login?msg_dash=2";
		try {
			List<NameValuePair> nvp =  new ArrayList<NameValuePair>();
//			String  userName ="939755738@qq.com";
//			String  passWord ="123456";
			String  userName ="820129856@qq.com";
			String  passWord ="123456";
			/*
			 * form_contacto_usuario:820129856@qq.com
				form_contacto_passwd:123456
			 * */
			nvp.add(new BasicNameValuePair("form_contacto_usuario", userName));
			nvp.add(new BasicNameValuePair("form_contacto_passwd",passWord));
			pageHelper.doPost(loginurl, refer, nvp);
			String homeUrl = "http://www.elturf.com/elturfcom/home";
			String body = pageHelper.doGet(homeUrl);
			String str = oCommonMethod.getValueByPatter(body, "<strong>("+userName+")</strong>");
//			String str = oCommonMethod.getValueByPatter(body, "<strong>(939755738@qq.com)</strong>");
//			String str = oCommonMethod.getValueByPatter(body, "(820129856)");
			if(str.length()>1){
				logger.info("============================login Success !!!!====================");
				return flag;
			}else{
				flag =false ;
				return flag;
			}
		} catch (Exception e) {
			logger.error("",e);
			return false;
		}
	}
	//Sábado 9 de Agosto del 2003
	public static void main(String[] args) {
		ExtractPostRaceThread p = new ExtractPostRaceThread();
//		p.run();
//		p.parsePage("D:\\Denis\\elturf\\Test\\fix\\10925.html");

		String str ="56,0";
		System.out.println(str.replaceAll(",", "."));
		p.login();
		String startDate =  "20100101";
		String endDate = "20170613";
		Vector<String> vDate = oCommonMethod.getHistoryDate(startDate,endDate);
		RacePool rp = new RacePool();
		for(int i=0;i<vDate.size();i++){
			String raceDate = vDate.get(i);
			rp.AddID(raceDate);
		}
		
		for(int i=1;i<=5;i++)
		{
			new Thread(new ExtractPostRaceThread(rp)).start();
		}
//		p.parse();
		
//		String arr[] ={"10890","10811","10758","10711","71244","71243","71242","71298","71299",
//				"71300","71552","71553","10956","10959","10963","10968","10939","10938","10934",
//				"10932","10930","10928","10927","10926","10925"};
//		String arr1[] ={"9378"};
		
//		 List<String> list = oCommonDB.getfixRaceDate("select distinct raceid from  elturf_postrace_horse where winodds is null ");
//		for(int i=0;i<list.size();i++){
//			String raceId =list.get(i);
//			p.fixRaceByraceId(raceId);
////			p.fixRaceByraceId("7972");
//		}
	}
}
