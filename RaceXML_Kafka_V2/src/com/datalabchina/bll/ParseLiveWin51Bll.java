package com.datalabchina.bll;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;

import javax.sql.rowset.CachedRowSet;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.datalabchina.controler.Controller;
import com.datalabchina.common.CommonDB;
//import com.datalabchina.Controller;
//import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import com.sun.rowset.CachedRowSetImpl;


public class ParseLiveWin51Bll {
	private static Logger logger = Logger.getLogger(ParseLiveWin51Bll.class.getName());
	 CommonDB oCommonDB =new CommonDB();
	private static CommonMethod oCommonMethod = new CommonMethod();	
//	private static String connectionString ="jdbc:sqlserver://192.168.120.216:1433;databaseName=FRDB;user=spider;password=83862909";
	private static String connectionString = Controller.connectionString;
	
	static boolean bPerMeetingCommit = false;
	static CachedRowSet HorsecachedRS = null;
	public void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\51_RAPP_PROB_SG_EVOL/77727687.xml";
		String  body = FileDispose.readFile(fileName);
		try {
//			 HorsecachedRS = new CachedRowSetImpl();
//	         HorsecachedRS.setUrl(connectionString);
	         
//	         HorsecachedRS.setCommand("select * from PmuInfoCentreKafka_XML_LiveOdds where 1=2");
//	         HorsecachedRS.execute();      
			String RaceDate, TrackName, RaceNo, DayMeetingNo,EnDirect=null, Corrupted=null, montant_enjeu_total, id_nav_reunion, id_nav_course, ExtractTime;
			
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find()){
				String oneJours = JoursMatcher.group(1);
				//trackName
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
//				TrackName = code_statut_infos;
				String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos>(.*?)</libelle_statut_infos>");
				
				String date_heure_generation = oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
				
				String type_document = oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>");
				
				String date_jour_reunion = oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>");
				
				Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(oneJours, "<reunion>(.*?)</reunion>");
				while(reunionMatcher.find()){
					String oneReunion = reunionMatcher.group(1);
				
					 id_nav_reunion= oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					//DayMeetingNo
					String  num_externe_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					DayMeetingNo = num_externe_reunion;
							
					String  code_hippo= oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					TrackName = code_hippo;
					
					String  type_reunion= oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>");
					//raceDate 
					String  date_reunion= oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
					RaceDate = getDate(date_reunion);
					
					String  num_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					
					Matcher courses = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courses.find()){
						String course = courses.group(1);
						id_nav_course = oCommonMethod.getValueByPatter(course, "<id_nav_course>(.*?)</id_nav_course>");
						
						//raceNo
						String num_course_pmu = oCommonMethod.getValueByPatter(course, "<num_course_pmu>(.*?)</num_course_pmu>");
						RaceNo = num_course_pmu;
						
						String libcourt_prix_course = oCommonMethod.getValueByPatter(course, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
//						TrackName = libcourt_prix_course;
						
						String ind_quadrio = oCommonMethod.getValueByPatter(course, "<ind_quadrio>(.*?)</ind_quadrio>");
						
						Hashtable<String,String> htRef= new Hashtable<String,String>();
						htRef.put("EnDirect", "0");
						Hashtable<String,String> htEvol= new Hashtable<String,String>();
						htEvol.put("EnDirect", "1");
						//上一条数据更新时间 
						String heure_rap_ref = oCommonMethod.getValueByPatter(course, "<heure_rap_ref>(.*?)</heure_rap_ref>");
						htRef.put("Time", RaceDate+" "+heure_rap_ref);
						//下一条数据更新时间 
						String heure_rap_evol = oCommonMethod.getValueByPatter(course, "<heure_rap_evol>(.*?)</heure_rap_evol>");
						htEvol.put("Time",RaceDate+" "+heure_rap_evol);
						
						String audience_pari_course = oCommonMethod.getValueByPatter(course, "<audience_pari_course>(.*?)</audience_pari_course>");
						montant_enjeu_total = oCommonMethod.getValueByPatter(course, "<montant_enjeu_total>(.*?)</montant_enjeu_total>");
						
						Matcher rapps_probs_part = oCommonMethod.getMatcherStrGroup(course, "<rapp_prob_part>(.*?)</rapp_prob_part>");
						
						while(rapps_probs_part.find()){
							String rapp_prob_part = rapps_probs_part.group(1);
							//horseName
							String nom_cheval = oCommonMethod.getValueByPatter(rapp_prob_part, "<nom_cheval>(.*?)</nom_cheval>");
							//clothNo
							String num_partant = oCommonMethod.getValueByPatter(rapp_prob_part, "<num_partant>(.*?)</num_partant>");
							//H_A H_B ....
							String ecurie_part = oCommonMethod.getValueByPatter(rapp_prob_part, "<ecurie_part>(.*?)</ecurie_part>");
							//上一条数据
							String rapp_ref = oCommonMethod.getValueByPatter(rapp_prob_part, "<rapp_ref>(.*?)</rapp_ref>");
							rapp_ref=rapp_ref==null?null:rapp_ref.replace(".", "").replace(",", ".");
							// H_16=NP
							if(rapp_ref!=null&&!"NP".equals(rapp_ref))
								htRef.put("H_"+num_partant,rapp_ref);
							if(ecurie_part!=null&&rapp_ref!=null&&!"NP".equals(rapp_ref))
								htRef.put("H_"+ecurie_part,rapp_ref);
							//下一条数据
							String rapp_evol = oCommonMethod.getValueByPatter(rapp_prob_part, "<rapp_evol>(.*?)</rapp_evol>");
							rapp_evol=rapp_evol==null?null:rapp_evol.replace(".", "").replace(",", ".");
							if(rapp_evol!=null&&!"NP".equals(rapp_evol))
								htEvol.put("H_"+num_partant, rapp_evol);
							if(ecurie_part!=null&&!"NP".equals(rapp_evol)&&rapp_evol!=null)
								htEvol.put("H_"+ecurie_part, rapp_evol);
							
							String favori = oCommonMethod.getValueByPatter(rapp_prob_part, "<favori>(.*?)</favori>");
							String signes = oCommonMethod.getValueByPatter(rapp_prob_part, "<signes>(.*?)</signes>");
							signes=signes==null?null:signes.replace(".", "").replace(",", ".");
						}
//						System.out.println(htRef);
//						System.out.println(htEvol);
//						montant_enjeu_total=montant_enjeu_total==null?null:montant_enjeu_total.replace(".", "").replace(",", ".");
						montant_enjeu_total=montant_enjeu_total==null?null:montant_enjeu_total.replace(".", "").replace(",", ".");
						if(htEvol.get("Time")!=null){
//							saveWinToDB(RaceDate, TrackName, RaceNo, DayMeetingNo, htRef, montant_enjeu_total, id_nav_reunion, id_nav_course, fileName);
							saveWinToDB(RaceDate, TrackName, RaceNo, DayMeetingNo, htEvol,montant_enjeu_total, id_nav_reunion, id_nav_course, fileName);
						}
						rapps_probs_part=null;
					}
					courses=null;
				}
			}
//			if (bPerMeetingCommit == false) {
//				InsertBulk("PmuInfoCentreKafka_XML_LiveOdds",HorsecachedRS);							
//			}
		}
		catch (Exception e) 
		{
			logger.error("error fileName:"+fileName+"\n\n",e);
		}
	}
	
	private void saveWinToDB(String raceDate, String trackName, String raceNo,
			String dayMeetingNo, Hashtable<String, String> htRef,
			String montant_enjeu_total, String id_nav_reunion,
			String id_nav_course, String fileName) {
		try {
			StringBuffer sSql =new StringBuffer(); 
			sSql=sSql.append(raceDate==null?"NULL,":"N'"+raceDate+"',");
			sSql=sSql.append(trackName==null?"NULL,":"N'"+trackName+"',");
			sSql=sSql.append(raceNo==null?"NULL,":"N'"+raceNo+"',");
			sSql=sSql.append(dayMeetingNo==null?"NULL,":"N'"+dayMeetingNo+"',");
			sSql=sSql.append(htRef.get("Time")==null?"NULL,":"N'"+htRef.get("Time")+"',");
			sSql=sSql.append(htRef.get("H_1")==null?"NULL,":"N'"+htRef.get("H_1")+"',");
			sSql=sSql.append(htRef.get("H_2")==null?"NULL,":"N'"+htRef.get("H_2")+"',");
			sSql=sSql.append(htRef.get("H_3")==null?"NULL,":"N'"+htRef.get("H_3")+"',");
			sSql=sSql.append(htRef.get("H_4")==null?"NULL,":"N'"+htRef.get("H_4")+"',");
			sSql=sSql.append(htRef.get("H_5")==null?"NULL,":"N'"+htRef.get("H_5")+"',");
			sSql=sSql.append(htRef.get("H_6")==null?"NULL,":"N'"+htRef.get("H_6")+"',");
			sSql=sSql.append(htRef.get("H_7")==null?"NULL,":"N'"+htRef.get("H_7")+"',");
			sSql=sSql.append(htRef.get("H_8")==null?"NULL,":"N'"+htRef.get("H_8")+"',");
			sSql=sSql.append(htRef.get("H_9")==null?"NULL,":"N'"+htRef.get("H_9")+"',");
			sSql=sSql.append(htRef.get("H_10")==null?"NULL,":"N'"+htRef.get("H_10")+"',");
			sSql=sSql.append(htRef.get("H_11")==null?"NULL,":"N'"+htRef.get("H_11")+"',");
			sSql=sSql.append(htRef.get("H_12")==null?"NULL,":"N'"+htRef.get("H_12")+"',");
			sSql=sSql.append(htRef.get("H_13")==null?"NULL,":"N'"+htRef.get("H_13")+"',");
			sSql=sSql.append(htRef.get("H_14")==null?"NULL,":"N'"+htRef.get("H_14")+"',");
			sSql=sSql.append(htRef.get("H_15")==null?"NULL,":"N'"+htRef.get("H_15")+"',");
			sSql=sSql.append(htRef.get("H_16")==null?"NULL,":"N'"+htRef.get("H_16")+"',");
			sSql=sSql.append(htRef.get("H_17")==null?"NULL,":"N'"+htRef.get("H_17")+"',");
			sSql=sSql.append(htRef.get("H_18")==null?"NULL,":"N'"+htRef.get("H_18")+"',");
			sSql=sSql.append(htRef.get("H_19")==null?"NULL,":"N'"+htRef.get("H_19")+"',");
			sSql=sSql.append(htRef.get("H_20")==null?"NULL,":"N'"+htRef.get("H_20")+"',");
			sSql=sSql.append(htRef.get("H_A")==null?"NULL,":"N'"+htRef.get("H_A")+"',");
			sSql=sSql.append(htRef.get("H_B")==null?"NULL,":"N'"+htRef.get("H_B")+"',");
			sSql=sSql.append(htRef.get("H_C")==null?"NULL,":"N'"+htRef.get("H_C")+"',");
			sSql=sSql.append(htRef.get("H_D")==null?"NULL,":"N'"+htRef.get("H_D")+"',");
			sSql=sSql.append(htRef.get("H_E")==null?"NULL,":"N'"+htRef.get("H_E")+"',");
			sSql=sSql.append(htRef.get("H_F")==null?"NULL,":"N'"+htRef.get("H_F")+"',");
			sSql=sSql.append(htRef.get("H_AB")==null?"NULL,":"N'"+htRef.get("H_AB")+"',");
			sSql=sSql.append(htRef.get("H_BB")==null?"NULL,":"N'"+htRef.get("H_BB")+"',");
			sSql=sSql.append(htRef.get("H_CB")==null?"NULL,":"N'"+htRef.get("H_CB")+"',");
			sSql=sSql.append(htRef.get("H_DB")==null?"NULL,":"N'"+htRef.get("H_DB")+"',");
			sSql=sSql.append(htRef.get("H_EB")==null?"NULL,":"N'"+htRef.get("H_EB")+"',");
			sSql=sSql.append(htRef.get("H_FB")==null?"NULL,":"N'"+htRef.get("H_FB")+"',");
			sSql=sSql.append(htRef.get("EnDirect")==null?"NULL,":"N'"+htRef.get("EnDirect")+"',");
			sSql=sSql.append(htRef.get("Corrupted")==null?"NULL,":"N'"+htRef.get("Corrupted")+"',");
			sSql=sSql.append(montant_enjeu_total==null?"NULL,":"N'"+montant_enjeu_total+"',");
			sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
			sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
			sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"',");
			sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"'");
										
			logger.info("pr_PmuInfoCentreKafka_XML_Live_Win_V2_InsertData  " + sSql);
			oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Live_Win_V2_InsertData", sSql.toString());
			sSql=null;
			
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}

	public static void InsertBulk(String tableName, CachedRowSet cachedRS) {
		SQLServerBulkCopy bulkCopy = null;
		try {

			// Note: if you are not using try-with-resources statements (as
			// here),
			// you must remember to call close() on any Connection, Statement,
			// ResultSet, and SQLServerBulkCopy objects that you create.
			// Open a sourceConnection to the AdventureWorks database.
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			// Connection sourceConnection =
			// DriverManager.getConnection(connectionString);
			// sourceConnection =
			// DriverManager.getConnection("jdbc:sqlserver://192.168.120.216:1433;databaseName=FRDB;user=spider;password=83862909");

			bulkCopy = new SQLServerBulkCopy(connectionString);
			SQLServerBulkCopyOptions copyOptions = new SQLServerBulkCopyOptions();
			copyOptions.setBulkCopyTimeout(0);
			bulkCopy.setBulkCopyOptions(copyOptions);
			bulkCopy.setDestinationTableName(tableName);

			StopWatch sw = new StopWatch();
			sw.start();
			try {
				bulkCopy.writeToServer(cachedRS);
			} catch (Exception e) {
				logger.error("", e);
			}
			bulkCopy.close();

			sw.stop();
			logger.info("insert " + cachedRS.size() + " rows, used : "+ sw.getTime() / 1000 + " s");

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			try {
				if (cachedRS != null)
					cachedRS.close();
				if (bulkCopy != null)
					bulkCopy.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	public void run(String pathName)
	{
		try {
			List<String> fileList = FileDispose.readLocalFileDir(pathName);
			String bak_path = pathName+File.separator+"ExtractedFiles"+File.separator;
			 File dir = new File(bak_path);  
			 if(!dir.exists())
				 CommonMethod.createDir(bak_path);
			for(int i=0;i<fileList.size();i++)
			{
				 String fileName= fileList.get(i);
				 parseFile(fileName);
			}
			
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	public static String getDate(String str){
		String s = null;
		try {
			if(str.contains(" ")){
				String arr[] = str.split(" ");
				if(arr[0].contains("-")){
					s = arr[0].split("-")[2]+"-"+arr[0].split("-")[1]+"-"+arr[0].split("-")[0]+" "+arr[1]+""+".000";
				} 
			}else{
				s = str.split("-")[2]+"-"+str.split("-")[1]+"-"+str.split("-")[0];
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return s;
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\51_RAPP_PROB_SG_EVOL";
		new ParseLiveWin51Bll().run(filePath);
	}
}
