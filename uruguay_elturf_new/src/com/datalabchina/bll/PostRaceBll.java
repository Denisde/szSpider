package com.datalabchina.bll;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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

public class PostRaceBll implements Runnable{
	private static Logger logger = Logger.getLogger(PostRaceBll.class.getName());
	static PageHelper pageHelper = PageHelper.getPageHelper();
	static CommonDB oCommonDB =new CommonDB();
	Map<String,List<String>> map = new HashMap<String,List<String>>();
	 static CommonMethod oCommonMethod = new CommonMethod();
	static Hashtable<String,String> dateHt = new Hashtable<String,String>();
	@Override
	public void run() 
	{
		if(!login()){
			logger.error("Login faile !!!!!");
			return;
		}
		parse();
	}

	public void parse() {
		String startDate =Controller.sStartDate;
		String endDate =Controller.sEndDate;
		Vector<String> vDate = oCommonMethod.getHistoryDate(startDate,endDate);
			for(int i=0;i<vDate.size();i++) 
			{
				String rawRaceDate = vDate.get(i);
				String raceDate =  convertRaceDate(rawRaceDate);
				List<String> raceUrlList = getHistoryPage(raceDate);
				getRacePageAndParse(raceUrlList,rawRaceDate);
			}
	}
	
//		String fileName = "D:\\Denis\\elturf\\Test\\20170329\\174918.html";
	public void readDataFromDir(String sPathName)
	{
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
//		System.out.println(datelist);
	}

	//http://www.elturf.com/elturfcom/carreras-resultado-ver?id_carrera=173658
	public void test(String raceId){
		String url ="http://www.elturf.com/elturfcom/carreras-resultado-ver?id_carrera="+raceId;
		String body = pageHelper.doGet(url);
		String fileName =Controller.sSaveFilePath+File.separator+"test"+File.separator+oCommonMethod.getValueByPatter(url, "(\\d{1,6})")+".html";
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
//Martes 22 de Agosto del 2017
	public PostRaceBll()
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
				String fileName =Controller.sSaveFilePath+File.separator+raceDate.substring(0, 4)+File.separator+raceDate+File.separator+oCommonMethod.getValueByPatter(url, "id_carrera=(\\d{1,6})")+".html";
				FileDispose.saveFile(body, fileName);
				parsePage(fileName);
			}
		} catch (Exception e){
			logger.error("",e);
		}
	}

	public void readFromLocalBySql(){
		try {
//			 List<String> list = oCommonDB.getfixRaceDate("select  CONVERT(varchar(100),raceDate, 112),raceid ,count(*) from   uruguaydb..elturf_postrace_horse "+
//					 						" where  1=1  "+
//					 						" group by raceid,raceDate "+
//					 						" having count(*)>1 "+
//					 						" order by 1");
			 List<String> list = oCommonDB.getfixRaceDate("select  CONVERT(varchar(100),raceDate, 112),raceid ,count(*) from   uruguaydb..elturf_postrace_horse "+
					 						" where  1=1  "+
					 						" group by raceid,raceDate "+
					 						" having count(*)>1 "+
					 						" order by 1");
			 //20030528_12909 /home/szspider/websitefile/uruguay/elturf/postfile
			 for(int i=0;i<list.size();i++){
				 String raceDateAndRaceid = list.get(i);
				 String localFile = Controller.sSaveFilePath+File.separator+raceDateAndRaceid.split("_")[0]+File.separator+raceDateAndRaceid.split("_")[1]+".html";
				 logger.info("*******************Start parse localFile *********************"+localFile);
				 parsePage(localFile);
			 }
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	public void fixRaceByraceId(String raceId){
		try {
			String url = "http://elturf.com/elturfcom/carreras-resultado-ver?id_carrera="+raceId;
			//10890,10811,10758,10711
//			url ="http://elturf.com/elturfcom/carreras-resultado-ver?id_carrera=129195";
			String body = pageHelper.doGet(url);
			String fileName =Controller.sSaveFilePath+File.separator+"fix"+File.separator+oCommonMethod.getValueByPatter(url, "(\\d{1,6})")+".html";
			FileDispose.saveFile(body, fileName);
			parsePage(fileName);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void parsePage(String fileName) {
		String body = FileDispose.readFile(fileName);
		if(body==null){
			logger.info("******************Start parse from Web****************************");
			login();
			String raceID = oCommonMethod.getValueByPatter(fileName, "(\\d{1,6}).html");
			fixRaceByraceId(raceID);
			return ;
		}
		if(body.length()<1||body.trim().equals("404")) {
			logger.error("==============body is empty !!!==================");
			return;
		}
		// 解析dividend 的数据 
//		DividendBll d = new DividendBll();
//		d.parseDividend(fileName);
		try {
			String RawTrackName = oCommonMethod.getValueByPatter(body, "<td class=\"text-left\" style=\"width:45%;\">(.*?)</div>");
			if(RawTrackName==null||"".equals(RawTrackName))//col-xs-6 col-sm-6 col-md-6 col-lg-6 text-left
				RawTrackName =   oCommonMethod.getValueByPatter(body, "<div class=\"col-xs-6 col-sm-6 col-md-6 col-lg-6 text-left\">(.*?)</div>");
			
//			System.out.println(RawTrackName);
			String trackName =RawTrackName.replaceAll("\\d","").replace("Reunión", "").trim(); 
			
			//<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-right">Martes 22 de Agosto del 2017</div>
			String  rawRaceDate =  oCommonMethod.getValueByPatter(body, "<td class=\"text-right\" style=\"width:45%;\">(.*?)</td>");
			
			if(rawRaceDate==null||"".equals(rawRaceDate))
				rawRaceDate =   oCommonMethod.getValueByPatter(body, "<div class=\"col-xs-6 col-sm-6 col-md-6 col-lg-6 text-right\">(.*?)</div>");
			
//			System.out.println(rawRaceDate);
			String raceDate = getRaceDate(rawRaceDate);
			String raceNo = oCommonMethod.getValueByPatter(body, "<h3><strong>(\\d{1,2}).*?</strong>");
			String officeGoing =null;
			/* <td class="text-left" style="width:47.5%;">
				<h3><strong>1ª</strong> <small>(1124)</small></h3>Handicap (Hd)
 				<br>Pr. <strong>"Población Emergencia"</strong>
				<br>Productos tres años y más (P3ay+)
				<br>Indice 1 e Inf.
				<br>1000m  Arena (Normal)<br>14:00 hrs.<!--1-->
				</td>* */
			String raceInfo = oCommonMethod.getValueByPatter(body,"<td class=\"text-left\" style=\"width:47.5%;\">(.*?)</td>");
//			System.out.println(raceInfo);
			//<h3><strong>9ª</strong> <small>(297)</small></h3>
//			 Clásico (Cl) <strong>CH</strong>
//			<br>Pr. <strong>"Memo"</strong>
//			 <br>1300m  Arena
//			<br>18:07 hrs.
			String actualStartTime = oCommonMethod.getValueByPatter(raceInfo, "(\\d{2}:\\d{2})");
			String meetingNo =  oCommonMethod.getValueByPatter(raceInfo, "<small>\\((.*?)\\)</small>\\s*</h3>");
			String distance =oCommonMethod.getValueByPatter(raceInfo, "(\\d{3,4})m");
			String arr[] = raceInfo.split("<br>");
			String raceName = null;
			String raceTypeAge = null;
			String surfaceName = null;
			String rawCategory =null;
			
			rawCategory = arr[0].substring(arr[0].indexOf("</h3>")).replace("</h3>", "").replaceAll("<.*?>", "").trim();
			
			if(arr.length==7){
				raceName  = (arr[1].replaceAll("<.*?>", "")+arr[2]+arr[3]).replaceAll("'", "");
				raceTypeAge = arr[4];
				surfaceName =arr[5].replace(distance+"m","").trim();
			}else if(arr.length==6){
				raceName  = (arr[1].replaceAll("<.*?>", "")+arr[2]).replaceAll("'", "");
				raceTypeAge = arr[3];
				surfaceName =arr[4].replace(distance+"m","").trim();
			}else if(arr.length==5){
				raceName  = (arr[1].replaceAll("<.*?>", "")+arr[2]).replaceAll("'","''");
				surfaceName =arr[3].replace(distance+"m","").trim();
			}else if(arr.length==4){
				raceName  = (arr[1].replaceAll("<.*?>", "")).replaceAll("'","''");
				surfaceName =arr[2].replace(distance+"m","").trim();
			}
			//<strong>Tiempo:</strong> 0.58.76 1000m Arena (Normal)</div>
			String rawleaderFinishTime = oCommonMethod.getValueByPatter(body, "<strong>Tiempo:</strong>(.*?)</div>");
			String leaderFinishTime = getLeaderFinishTime(rawleaderFinishTime);
			/*
			 * <td class="text-right" style="width:47.5%;"><!--2-->
			 * <br><strong>$750.000</strong>  al 1ro<br>
			 * <strong>$180.000</strong>  al 2do<br>
			 * <strong>$105.000</strong>  al 3ro<br>
			 * <br><br>Bolsa de Premios<br><strong>$1.035.000</strong><!--2--></td>
			 * */
			/*
			 * <strong>CLP $900.000</strong>  al 1ro<br>
			 * <strong>CLP $180.000</strong>  al 2do<br>
			 * <strong>CLP $90.000</strong>  al 3ro<br>
			 * <strong>CLP $45.000</strong>  al 4to<br>
			 * <br><br>Bolsa de Premios<br><strong>CLP $1.215.000</strong><!--2-->
			 * */
			/*
			 * <strong>S/.5.200</strong>  al 1ro<br>
			 * <strong>S/.1.560</strong>  al 2do<br>
			 * <strong>S/.1.040</strong>  al 3ro<br>
			 * <strong>S/.520</strong>  al 4to<br><br>
			 * <br>Bolsa de Premios<br><strong>S/.8.320</strong><!--2-->
			 * */
			
			String prizeInfo = oCommonMethod.getValueByPatter(body, "<td class=\"text-right\" style=\"width:47.5%;\">(.*?)</td>");
			//<strong>CLP $900.000</strong>
//			String prize =oCommonMethod.getValueByPatter(body, "<br>\\s*Bolsa de Premios\\s*<br>\\s*<strong>.*?\\$(\\d{1,}\\.?\\d{1,}\\.?\\d{1,})\\s*</strong>").replaceAll("\\.", "");
////			String prize =oCommonMethod.getValueByPatter(body, "<br>\\s*Bolsa de Premios\\s*<br>\\s*<strong>.*?\\$(.*?)</strong>").replaceAll("\\.", "");
//			if(prize.length()<1) {
//				prize =oCommonMethod.getValueByPatter(body, "<br>\\s*Bolsa de Premios\\s*<br>\\s*<strong>.*?S/\\.(.*?)</strong>").replaceAll("\\.", "");
//			}
////			System.out.println(prizeInfo);
//			Hashtable<String,String> ht = new Hashtable<String,String>();
//			Matcher m = oCommonMethod.getMatcherStrGroup(prizeInfo, "<strong>.*?\\$(.*?)</strong>\\s*al\\s*(\\d{1,2}).*?<br>");
//			while(m.find()){
////				System.out.println(m.group(1)+"-------------------"+m.group(2));
//				ht.put("prize"+m.group(2),m.group(1).replaceAll("\\.", ""));
//			}
//			if(ht.size()<1) {
//				Matcher m1 = oCommonMethod.getMatcherStrGroup(prizeInfo, "<strong>.*?S/\\.(.*?)</strong>\\s*al\\s*(\\d{1,2}).*?<br>");
//				while(m1.find()){
////				System.out.println(m.group(1)+"-------------------"+m.group(2));
//					ht.put("prize"+m1.group(2),m1.group(1).replaceAll("\\.", ""));
//				}
//			}
			
			String prize =oCommonMethod.getValueByPatter(body, "<br>Bolsa de Premios<br>\\s*<strong>.*?\\$(\\d{1,}\\.?\\d{1,}\\.?\\d{1,})\\s*</strong>").replaceAll("\\.", "");
			//S/.10.915
			if(prize.length()<1) {
				prize =oCommonMethod.getValueByPatter(body, "<br>\\s*Bolsa de Premios\\s*<br>\\s*<strong>.*?S/?\\.?(\\d{1,}\\.?\\d{1,}\\.?\\d{1,})</strong>").replaceAll("\\.", "");
			}
//			System.out.println(prizeInfo);
			Hashtable<String,String> ht = new Hashtable<String,String>();
			Matcher m = oCommonMethod.getMatcherStrGroup(prizeInfo, "<strong>.*?\\$(\\d{1,}\\.?\\d{1,}\\.?\\d{1,})</strong>\\s*al\\s*(\\d{1,2}).*?<br>");
			while(m.find()){
//				System.out.println(m.group(1)+"-------------------"+m.group(2));
				ht.put("prize"+m.group(2),m.group(1).replaceAll("\\.", ""));
			}
			if(ht.size()<1) {
				Matcher m1 = oCommonMethod.getMatcherStrGroup(prizeInfo, "<strong>.*?S/?\\.(\\d{1,}\\.?\\d{1,}\\.?\\d{1,})</strong>\\s*al\\s*(\\d{1,2}).*?<br>");
				while(m1.find()){
//				System.out.println(m.group(1)+"-------------------"+m.group(2));
					ht.put("prize"+m1.group(2),m1.group(1).replaceAll("\\.", ""));
				}
			}
			
			
			String raceTitle =null;
			String raceId = oCommonMethod.getValueByPatter(fileName, "(\\d{1,6}).html");
			savePostRaceToDB(raceDate,trackName,raceNo,officeGoing,actualStartTime,
					raceName,leaderFinishTime,surfaceName,prize,ht,raceTitle,raceTypeAge,distance,
					meetingNo,raceId,rawleaderFinishTime,rawCategory);
			
			String horseInfo = oCommonMethod.getValueByPatter(body, "<table class=\"table table-hover table-condensed.*?\">(.*?)</table>");
			String fullJockeyNameLine = oCommonMethod.getValueByPatter(body, "<strong>Jinetes\\:</strong>(.*?)<br>\\s*<strong>");
			parseHorseInfo(horseInfo,raceDate,trackName,raceNo,raceId,fullJockeyNameLine);
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	
	private void parseHorseInfo(String horseInfo,String raceDate,String trackName,String raceNo,String raceId,String fullJockeyNameLine) {
		try {
			
			fullJockeyNameLine = fullJockeyNameLine.replaceAll("\\(h\\)", "");
			
			String[] JockeyFullNameArr = fullJockeyNameLine.split("\\(.*?°?\\)");
//			String[] JockeyFullNameArr = fullJockeyNameLine.split("\\(.*?°\\)");
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
//				HorseBll hb = new HorseBll();
//				hb.parsePage(webHorseId);
				
				//<td class="text-center esconder_resultados_phone1 esconder_resultados_phone2 esconder_resultados_tablet1 esconder_resultados_tablet2">3a</td>
				String horseAgeDesc = tdNodes[3].toPlainTextString();
//				<td class="text-right esconder_resultados_phone1 esconder_resultados_phone2 esconder_resultados_tablet1 esconder_resultados_tablet2">404k</td>
				String horseWeight = covertString(tdNodes[4].toPlainTextString().replace("k", ""));
				if(!oCommonMethod.isNumber(horseWeight))horseWeight =null;
				
//				<td class="text-center">pz</td>
				String rawbeatenDistance = tdNodes[5].toPlainTextString().trim().length()<1?null:tdNodes[5].toPlainTextString();
				
				String beatenDistance =GetBeatenDistance(rawbeatenDistance); 
//				System.out.println(GetBeatenDistance(rawbeatenDistance));
				//53,5  , 52/53,
				String hanciapWeight = tdNodes[6].toPlainTextString().replaceAll(" ", "").replaceAll(",", ".");
//				if(hanciapWeight.indexOf("/")>-1)hanciapWeight = oCommonMethod.getValueByPatter(hanciapWeight,"(\\\\\\d{1,2})");
				
				 hanciapWeight = oCommonMethod.getValueByPatter(hanciapWeight,"(\\d{1,2}\\.?\\d*)");
				
				if(hanciapWeight!=null&&hanciapWeight.trim().length()<1)hanciapWeight =null;
				
				String jockeyName =  covertString(tdNodes[7].toPlainTextString());
				
				String trainerName =  covertString(tdNodes[8].toPlainTextString());
				//11|
				String winOdds =covertString(tdNodes[9].toPlainTextString().replace(",", ".").replaceAll("|", ""));
				
				if(winOdds!=null&&winOdds.split("\\.").length==3)winOdds = winOdds.replaceFirst("\\.", "");
				
				if(winOdds!=null&&winOdds.endsWith("."))
					winOdds = winOdds.substring(0,winOdds.length()-1); 
				
				if(winOdds!=null) {
					if(!oCommonMethod.isNumber(winOdds.replaceAll("\\.", ""))){
						winOdds = null;
					}
				}
				winOdds = covertString(winOdds);
				
				String scratch = "0";
				if(RqwFinishPosition.indexOf("U")>-1)scratch ="1";
				String horseorgin = null;
				String Dam=null;
				String sireOfDam = null;
				String finishpositionSeq =null;
				
//() Alan Arce M., (1°) Juan Enriquez F., (2°) L. Valderrama, (4°) R Fernandez, (5°) Benjamin Cacha P., (6°) C. Trujillo C., (7°) J. Gihua, (U°) Raul Diaz T., (U°) Ch. Torres C.				
//(1°) Manuel Guerrero, (2°) Gustavo Vera, (2°) Nelson Figueroa M., (4°) Ivan Carcamo, (5°) Jesus Toro N., (6°) Jely Barril, (7°) Lennart P. Silva, (8°) Luis Aros H., (9°) Danilo Grisales, (10°) Leonardo Mardones G., (U°) Cristian A. Rojas E.<br>
//	(6°) Felipe Henríquez, (7°) Rodolfo Fuenzalida G., (8°) Benjamin Sancho, (9°) Carlos Abarca G., (10°) Bernardo León E., (11°) Joaquin Herrera, (12°) Marco A. Morales U., (U°) Victor Orrego F.
//(1°) Nelson Figueroa M., (2°) Luis Riquelme, (3°) Joaquin Herrera, (4°) Gustavo Vera, (5°) Jely Barril, (6°) Rafael Cisternas A., (7°) Cristian A. Rojas E., (8°) Moises Donoso G., (9°) Iván M. Alvarez, (10°) Cristian Caro, (U°) Danilo Grisales, (U°) Luis Perez<br>			
//	(1°) Martin J. Valle, (2°) Geronimo J Garcia, (3°) Francisco A. Arreguy (h), (4°) Facundo M. Coria, (5°) Elias Martinez, (6°) Sergio R. Barrionuevo, (7°) Luciano E. Cabrera, (8°) Gustavo E. Calvente, (9°) Francisco L. Fernandes G., (U°) Facundo S. Aguirre
				String JockeyFullName = oCommonMethod.getValueByPatter(fullJockeyNameLine, "\\("+RqwFinishPosition+"°?\\)(.*?),");
				if(JockeyFullName.length()<1) {
					JockeyFullName = oCommonMethod.getValueByPatter(fullJockeyNameLine, "\\("+RqwFinishPosition+"°\\)(.*?)<br>");
				}
				if(j<=JockeyFullNameArr.length) {
					String orderfullJockeyName = JockeyFullNameArr[j-1].replaceAll(",", "").trim();
					if(orderfullJockeyName.indexOf(JockeyFullName)<0) {
						JockeyFullName = orderfullJockeyName.replaceAll("<br>", "");
					}
				}
				
				
				savePostHorseToDB(raceDate,trackName,raceNo,webHorseId,horseName,horseorgin,clothNo,jockeyName,sire,
						Dam,sireOfDam,horseAgeDesc,rawbeatenDistance,winOdds,hanciapWeight,horseWeight,RqwFinishPosition,finishpositionSeq,scratch,
						beatenDistance,RawClothNo,raceId,trainerName,JockeyFullName);
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
			String rawClothNo, String raceId,String trainerName,String JockeyFullName) {
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
			sSql+=trainerName==null?"NULL,":"N'"+trainerName+"',";
			sSql+=JockeyFullName==null?"NULL":"N'"+JockeyFullName+"'";
			
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
			if(leaderFinishTime.trim().length()<1)
			{
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
	//Martes 22 de Agosto del 2017
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
		//http://elturf.com/elturfcom/dashboard-login-in
		String loginurl="http://elturf.com/elturfcom/dashboard-login-in";
//		pageHelper = testProxy(loginurl);
		String refer = "http://elturf.com/elturfcom/dashboard-login?msg_dash=2";
		try {
			List<NameValuePair> nvp =  new ArrayList<NameValuePair>();
//			String  userName ="939755738@qq.com";01
//			String  passWord ="123456";
			String  userName ="820129856@qq.com";
			String  passWord ="123456";
			/*
			 * form_contacto_usuario:820129856@qq.com
			 * form_contacto_passwd:123456
			 * */
			//form_contacto_usuario:
			//form_contacto_passwd
			nvp.add(new BasicNameValuePair("form_contacto_usuario", userName));
			nvp.add(new BasicNameValuePair("form_contacto_passwd",passWord));
			pageHelper.doPost(loginurl, refer, nvp);
			String homeUrl = "http://www.elturf.com/elturfcom/home";
			String body = pageHelper.doGet(homeUrl);
			//<li><a href="perfil"><span class="glyphicon glyphicon-user"></span> Perfil </a></li>		
			String str = oCommonMethod.getValueByPatter(body, "<span.*?></span>\\s*(.*?)\\s*</a>");
//			String str = oCommonMethod.getValueByPatter(body, "<strong>(939755738@qq.com)</strong>");
//			String str = oCommonMethod.getValueByPatter(body, "(820129856)");
			if(str.length()>1){
				logger.info("============================login Success !!!!====================");
				return flag;
			} else {
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
		PostRaceBll p = new PostRaceBll();
		Controller.sStartDate="20190304";
		Controller.sEndDate ="20190304";
		p.run();
//		p.parsePage("F:\\Denis\\elturf\\Test\\fix\\24257.html");
//		p.parse();
//		String arr ="1,2,3,4,5,6,7,8,9,4755,7250,15718,20700,23052,24482,50691,58624,66993,67805,97601";
//		for(int i=0;i<arr.split(",").length;i++){
//			p.fixRaceByraceId(arr.split(",")[i]+"");
//		}
//		p.fixRaceByraceId("210790");
//		p.readFromLocalBySql();
//		 List<String> list = oCommonDB.getfixRaceDate(" select distinct raceid  from "
//		 									+ "UruguayDB..Elturf_Postrace_Race where prize=0");	
		 //http://elturf.com/elturfcom/home
//		for(int i=0;i<list.size();i++){
//			String raceId =list.get(i);
//			p.fixRaceByraceId(raceId);
//		}
//		p.login();
//		p.fixRaceByraceId( "217591");
		
//		p.fixRaceFromSql();
	}
}
