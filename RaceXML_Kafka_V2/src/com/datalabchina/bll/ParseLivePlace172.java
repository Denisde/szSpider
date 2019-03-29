package com.datalabchina.bll;

import java.io.File;
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


public class ParseLivePlace172 {
	private static Logger logger = Logger.getLogger(ParseLivePlace172.class.getName());
	private static CommonDB oCommonDB =new CommonDB();
	private static CommonMethod oCommonMethod = new CommonMethod();	
	// <property name="FRDB">drivers=net.sourceforge.jtds.jdbc.Driver;url=jdbc:jtds:sqlserver://192.168.28.126:1433/FRDB;instance=inst12;user=spider;password=83862909</property>
//	private static String connectionString ="jdbc:sqlserver://192.168.120.216:1433;databaseName=FRDB;user=spider;password=83862909";
	private static String connectionString = Controller.connectionString;
//	static boolean bPerMeetingCommit = false;
//	static CachedRowSet HorsecachedRS = null;
	public static void parseFile(String fileName)
	{
		String  body = FileDispose.readFile(fileName);
		if(body==null||body.length()<1)return;
		logger.info("Parse FileName ************************" +fileName+"***************************************");
		try {
//			 HorsecachedRS = new CachedRowSetImpl();
//	         HorsecachedRS.setUrl(connectionString);
//	         
//	         HorsecachedRS.setCommand("select * from PmuInfoCentreKafka_XML_LiveOdds_ShowBetAndStakes where 1=2");
//	         HorsecachedRS.execute();     
			String RaceDate, TrackName, RaceNo, DayMeetingNo, TimeStamp, Corrupted, montant_enjeu_total, id_nav_reunion, id_nav_course, FileName, ExtractTime;
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find())
			{
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
				String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos>(.*?)</libelle_statut_infos>");
				String date_heure_generation = oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
				String type_document = oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>");
				String date_jour_reunion = oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>");
				
				Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(body, "<reunion>(.*?)</reunion>");
				while(reunionMatcher.find()){
					String oneReunion =reunionMatcher.group(1); 
					id_nav_reunion = oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					
					String lib_reunion = oCommonMethod.getValueByPatter(oneReunion, "<lib_reunion>(.*?)</lib_reunion>");
					String num_externe_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					DayMeetingNo = num_externe_reunion;
					
					String num_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					String code_hippo = oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					TrackName = code_hippo;
					
					String date_reunion = oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
					
					RaceDate = getDate(date_reunion);
					Hashtable<String,String> htRef= new Hashtable<String,String>();
					htRef.put("EnDirect", "0");
					Hashtable<String,String> htEvol= new Hashtable<String,String>();
					htEvol.put("EnDirect", "1");
					
					Matcher courseMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courseMatcher.find())
					{
						String onecourse = courseMatcher.group(1);
						id_nav_course = oCommonMethod.getValueByPatter(onecourse, "<id_nav_course>(.*?)</id_nav_course>");
						
						String num_course_pmu = oCommonMethod.getValueByPatter(onecourse, "<num_course_pmu>(.*?)</num_course_pmu>");
						RaceNo = num_course_pmu;
						String libcourt_prix_course = oCommonMethod.getValueByPatter(onecourse, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
//						TrackName = libcourt_prix_course;
						
						String heure_rap_ref = oCommonMethod.getValueByPatter(onecourse, "<heure_rap_ref>(.*?)</heure_rap_ref>");
						String heure_rap_evol = oCommonMethod.getValueByPatter(onecourse, "<heure_rap_evol>(.*?)</heure_rap_evol>");
						htEvol.put("Time",RaceDate+" "+heure_rap_evol);
						
						Matcher rapps_probsMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<rapps_probs>(.*?)</rapps_probs>");
						while(rapps_probsMatcher.find()){
							String onerapps_probs = rapps_probsMatcher.group(1);
							String audience_pari_course = oCommonMethod.getValueByPatter(onerapps_probs, "<audience_pari_course>(.*?)</audience_pari_course>");
							montant_enjeu_total = oCommonMethod.getValueByPatter(onerapps_probs, "<montant_enjeu_total>(.*?)</montant_enjeu_total>");
							
							montant_enjeu_total=montant_enjeu_total==null?null:montant_enjeu_total.replace(".", "").replace(",", ".");
							
							String heure_montant_enjeu_total = oCommonMethod.getValueByPatter(onerapps_probs, "<heure_montant_enjeu_total>(.*?)</heure_montant_enjeu_total>");
							Matcher rapps_probs_partMatcher = oCommonMethod.getMatcherStrGroup(onerapps_probs, "<rapp_prob_part>(.*?)</rapp_prob_part>");
							while(rapps_probs_partMatcher.find())
							{
									String onerapps_probs_part =rapps_probs_partMatcher.group(1); 
									String nom_cheval = oCommonMethod.getValueByPatter(onerapps_probs_part, "<nom_cheval>(.*?)</nom_cheval>");
									String num_partant = oCommonMethod.getValueByPatter(onerapps_probs_part, "<num_partant>(.*?)</num_partant>");
									String rapp_min = oCommonMethod.getValueByPatter(onerapps_probs_part, "<rapp_min>(.*?)</rapp_min>");
									String rapp_max = oCommonMethod.getValueByPatter(onerapps_probs_part, "<rapp_max>(.*?)</rapp_max>");
									if(rapp_min!=null&&!"NP".equals(rapp_min))
									htEvol.put("H_"+num_partant+"_min", rapp_min.replace(",", ""));
									if(rapp_max!=null&&!"NP".equals(rapp_max))
									htEvol.put("H_"+num_partant+"_max", rapp_max.replace(",", ""));
							}
							if(htEvol.get("Time")!=null){
								savePlaceToDB(RaceDate, TrackName, RaceNo, DayMeetingNo, htEvol,montant_enjeu_total, id_nav_reunion, id_nav_course, fileName);
							}
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
			logger.error("fileName = ****************"+fileName+"*******************************");
			logger.error("",e);
		}
	}
	
	private static void savePlaceToDB(String raceDate, String trackName,
		String raceNo, String dayMeetingNo, Hashtable<String, String> htEvol,
		String montant_enjeu_total, String id_nav_reunion,
		String id_nav_course, String fileName) {
		try {
			StringBuffer sSql =new StringBuffer(); 
			sSql=sSql.append(raceDate==null?"NULL,":"N'"+raceDate+"',");
			sSql=sSql.append(trackName==null?"NULL,":"N'"+trackName+"',");
			sSql=sSql.append(raceNo==null?"NULL,":"N'"+raceNo+"',");
			sSql=sSql.append(dayMeetingNo==null?"NULL,":"N'"+dayMeetingNo+"',");
			sSql=sSql.append(htEvol.get("Time")==null?"NULL,":"N'"+htEvol.get("Time")+"',");
			sSql=sSql.append(htEvol.get("H_01_min")==null?"NULL,":"N'"+htEvol.get("H_01_min")+"',");
			sSql=sSql.append(htEvol.get("H_01_max")==null?"NULL,":"N'"+htEvol.get("H_01_max")+"',");
			sSql=sSql.append(htEvol.get("H_02_min")==null?"NULL,":"N'"+htEvol.get("H_02_min")+"',");
			sSql=sSql.append(htEvol.get("H_02_max")==null?"NULL,":"N'"+htEvol.get("H_02_max")+"',");
			sSql=sSql.append(htEvol.get("H_03_min")==null?"NULL,":"N'"+htEvol.get("H_03_min")+"',");
			sSql=sSql.append(htEvol.get("H_03_max")==null?"NULL,":"N'"+htEvol.get("H_03_max")+"',");
			sSql=sSql.append(htEvol.get("H_04_min")==null?"NULL,":"N'"+htEvol.get("H_04_min")+"',");
			sSql=sSql.append(htEvol.get("H_04_max")==null?"NULL,":"N'"+htEvol.get("H_04_max")+"',");
			sSql=sSql.append(htEvol.get("H_05_min")==null?"NULL,":"N'"+htEvol.get("H_05_min")+"',");
			sSql=sSql.append(htEvol.get("H_05_max")==null?"NULL,":"N'"+htEvol.get("H_05_max")+"',");
			sSql=sSql.append(htEvol.get("H_06_min")==null?"NULL,":"N'"+htEvol.get("H_06_min")+"',");
			sSql=sSql.append(htEvol.get("H_06_max")==null?"NULL,":"N'"+htEvol.get("H_06_max")+"',");
			sSql=sSql.append(htEvol.get("H_07_min")==null?"NULL,":"N'"+htEvol.get("H_07_min")+"',");
			sSql=sSql.append(htEvol.get("H_07_max")==null?"NULL,":"N'"+htEvol.get("H_07_max")+"',");
			sSql=sSql.append(htEvol.get("H_08_min")==null?"NULL,":"N'"+htEvol.get("H_08_min")+"',");
			sSql=sSql.append(htEvol.get("H_08_max")==null?"NULL,":"N'"+htEvol.get("H_08_max")+"',");
			sSql=sSql.append(htEvol.get("H_09_min")==null?"NULL,":"N'"+htEvol.get("H_09_min")+"',");
			sSql=sSql.append(htEvol.get("H_09_max")==null?"NULL,":"N'"+htEvol.get("H_09_max")+"',");
			sSql=sSql.append(htEvol.get("H_10_min")==null?"NULL,":"N'"+htEvol.get("H_10_min")+"',");
			sSql=sSql.append(htEvol.get("H_10_max")==null?"NULL,":"N'"+htEvol.get("H_10_max")+"',");
			sSql=sSql.append(htEvol.get("H_11_min")==null?"NULL,":"N'"+htEvol.get("H_11_min")+"',");
			sSql=sSql.append(htEvol.get("H_11_max")==null?"NULL,":"N'"+htEvol.get("H_11_max")+"',");
			sSql=sSql.append(htEvol.get("H_12_min")==null?"NULL,":"N'"+htEvol.get("H_12_min")+"',");
			sSql=sSql.append(htEvol.get("H_12_max")==null?"NULL,":"N'"+htEvol.get("H_12_max")+"',");
			sSql=sSql.append(htEvol.get("H_13_min")==null?"NULL,":"N'"+htEvol.get("H_13_min")+"',");
			sSql=sSql.append(htEvol.get("H_13_max")==null?"NULL,":"N'"+htEvol.get("H_13_max")+"',");
			sSql=sSql.append(htEvol.get("H_14_min")==null?"NULL,":"N'"+htEvol.get("H_14_min")+"',");
			sSql=sSql.append(htEvol.get("H_14_max")==null?"NULL,":"N'"+htEvol.get("H_14_max")+"',");
			sSql=sSql.append(htEvol.get("H_15_min")==null?"NULL,":"N'"+htEvol.get("H_15_min")+"',");
			sSql=sSql.append(htEvol.get("H_15_max")==null?"NULL,":"N'"+htEvol.get("H_15_max")+"',");
			sSql=sSql.append(htEvol.get("H_16_min")==null?"NULL,":"N'"+htEvol.get("H_16_min")+"',");
			sSql=sSql.append(htEvol.get("H_16_max")==null?"NULL,":"N'"+htEvol.get("H_16_max")+"',");
			sSql=sSql.append(htEvol.get("H_17_min")==null?"NULL,":"N'"+htEvol.get("H_17_min")+"',");
			sSql=sSql.append(htEvol.get("H_17_max")==null?"NULL,":"N'"+htEvol.get("H_17_max")+"',");
			sSql=sSql.append(htEvol.get("H_18_min")==null?"NULL,":"N'"+htEvol.get("H_18_min")+"',");
			sSql=sSql.append(htEvol.get("H_18_max")==null?"NULL,":"N'"+htEvol.get("H_18_max")+"',");
			sSql=sSql.append(htEvol.get("H_19_min")==null?"NULL,":"N'"+htEvol.get("H_19_min")+"',");
			sSql=sSql.append(htEvol.get("H_19_max")==null?"NULL,":"N'"+htEvol.get("H_19_max")+"',");
			sSql=sSql.append(htEvol.get("H_20_min")==null?"NULL,":"N'"+htEvol.get("H_20_min")+"',");
			sSql=sSql.append(htEvol.get("H_20_max")==null?"NULL,":"N'"+htEvol.get("H_20_max")+"',");

//			sSql=sSql.append(htEvol.get("EnDirect")==null?"NULL,":"N'"+htEvol.get("EnDirect")+"',");
			sSql=sSql.append(htEvol.get("Corrupted")==null?"NULL,":"N'"+htEvol.get("Corrupted")+"',");
			sSql=sSql.append(montant_enjeu_total==null?"NULL,":"N'"+montant_enjeu_total+"',");
			sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
			sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
			sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"',");
			sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"'");
										
			logger.info("pr_PmuInfoCentreKafka_XML_Live_Place_V2_InsertData  " + sSql);
			oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Live_Place_V2_InsertData", sSql.toString());
			sSql=null;
			
		} catch (Exception e) {
			logger.error("",e);
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
	
	public static void InsertBulk(String tableName, CachedRowSet cachedRS) {
		SQLServerBulkCopy bulkCopy = null;
		try {
			// Note: if you are not using try-with-resources statements (as here),
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
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\172";
		new ParseLivePlace172().run(filePath);
	}
}
