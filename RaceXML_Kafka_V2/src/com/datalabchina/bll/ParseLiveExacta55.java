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

public class ParseLiveExacta55 {
	private static Logger logger = Logger.getLogger(ParseLiveExacta55.class.getName());
	private static CommonDB oCommonDB =new CommonDB();
	private static CommonMethod oCommonMethod = new CommonMethod();	
//	private static String connectionString ="jdbc:sqlserver://192.168.120.216:1433;databaseName=FRDB;user=spider;password=83862909";
	private static String connectionString = Controller.connectionString;
	static boolean bPerMeetingCommit = false;
	static CachedRowSet HorsecachedRS = null;
	
	public static void parseFile(String fileName)
	{
		String  body = FileDispose.readFile(fileName);
		if(body==null||body.length()<1)return;
		logger.info("Parse FileName ************************" +fileName+"***************************************");
		try {

//			 HorsecachedRS = new CachedRowSetImpl();
//	         HorsecachedRS.setUrl(connectionString);
//	         
//	         HorsecachedRS.setCommand("select * from PmuInfoCentreKafka_XML_LiveOdds_Exacta where 1=2");
//	         HorsecachedRS.execute();        
			String RaceDate, TrackName, RaceNo, DayMeetingNo, TimeStamp,EnDirect, CorruptedOdds, 
			montant_enjeu_total, id_nav_reunion, id_nav_course, FileName, ExtractTime;
			
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find())
			{
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
				String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos>(.*?)</libelle_statut_infos>");
				String date_heure_generation = oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
				String type_document = oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>");
				String date_jour_reunion = oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>");
				Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(oneJours, "<reunion>(.*?)</reunion>");
				
				while(reunionMatcher.find()){
					String oneReunion =reunionMatcher.group(1); 
					id_nav_reunion = oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					
					String num_externe_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					
					DayMeetingNo = num_externe_reunion;
					
					String code_hippo = oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					TrackName = code_hippo;
					
					String type_reunion = oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>");
					String date_reunion = oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
					
					RaceDate = getDate(date_reunion);
					
					String num_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>"); 
					
					Matcher courseMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courseMatcher.find())
					{
						String onecourse = courseMatcher.group(1);
						Hashtable<String,String> htRef= new Hashtable<String,String>();
						htRef.put("EnDirect", "0");
						Hashtable<String,String> htEvol= new Hashtable<String,String>();
						htEvol.put("EnDirect", "1");
						
						id_nav_course = oCommonMethod.getValueByPatter(onecourse, "<id_nav_course>(.*?)</id_nav_course>");
						
						 String num_course_pmu = oCommonMethod.getValueByPatter(onecourse, "<num_course_pmu>(.*?)</num_course_pmu>");
						 RaceNo = num_course_pmu;
						 
						 String libcourt_prix_course = oCommonMethod.getValueByPatter(onecourse, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
						
//						TrackName = libcourt_prix_course;
						String heure_rap_ref = oCommonMethod.getValueByPatter(onecourse, "<heure_rap_ref>(.*?)</heure_rap_ref>");
						htRef.put("Time", RaceDate+" "+heure_rap_ref);
						String heure_rap_evol = oCommonMethod.getValueByPatter(onecourse, "<heure_rap_evol>(.*?)</heure_rap_evol>");
						htEvol.put("Time",RaceDate+" "+heure_rap_evol);
						
						Matcher rapps_probsMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<rapps_probs>(.*?)</rapps_probs>");
						while(rapps_probsMatcher.find()){
							String onerapps_probs = rapps_probsMatcher.group(1);
							String audience_pari_course = oCommonMethod.getValueByPatter(onerapps_probs, "<audience_pari_course>(.*?)</audience_pari_course>");
							Matcher rapps_probs_partMatcher = oCommonMethod.getMatcherStrGroup(onerapps_probs, "<rapps_probs_part>(.*?)</rapps_probs_part>");
							while(rapps_probs_partMatcher.find()){
								 String onerapps_probs_part =rapps_probs_partMatcher.group(1); 
								 String num_part = oCommonMethod.getValueByPatter(onerapps_probs_part, "<num_part>(.*?)</num_part>");
								 String nom_cheval = oCommonMethod.getValueByPatter(onerapps_probs_part, "<nom_cheval>(.*?)</nom_cheval>");
								 String ecurie_part = oCommonMethod.getValueByPatter(onerapps_probs_part, "<ecurie_part>(.*?)</ecurie_part>");
								 Matcher combinaisonMatcher = oCommonMethod.getMatcherStrGroup(onerapps_probs_part, "<combinaison>(.*?)</combinaison>");
									while(combinaisonMatcher.find())
									{
										String oneCombinaison = combinaisonMatcher.group(1);
										String num_part_1 = oCommonMethod.getValueByPatter(oneCombinaison, "<num_part_1>(.*?)</num_part_1>");
//										String statut_part_1 = oCommonMethod.getValueByPatter(oneCombinaison, "<statut_part_1>(.*?)</statut_part_1>");
										String num_part_2 = oCommonMethod.getValueByPatter(oneCombinaison, "<num_part_2>(.*?)</num_part_2>");
//										String statut_part_2 = oCommonMethod.getValueByPatter(oneCombinaison, "<statut_part_2>(.*?)</statut_part_2>");
										
										String rapp_ref = oCommonMethod.getValueByPatter(oneCombinaison, "<rapp_ref>(.*?)</rapp_ref>");
										if(rapp_ref!=null&&!"NP".equals(rapp_ref))
											htRef.put("H_"+num_part_1+"_"+num_part_2,rapp_ref);
										if(ecurie_part!=null&&!"NP".equals(rapp_ref)&&rapp_ref!=null)
											htRef.put("H_"+ecurie_part,rapp_ref);
										
										String rapp_evol = oCommonMethod.getValueByPatter(oneCombinaison, "<rapp_evol>(.*?)</rapp_evol>");
										rapp_evol=rapp_evol==null?null:rapp_evol.replace(".", "").replace(",", ".");
										if(rapp_evol!=null&&!"NP".equals(rapp_evol))
											htEvol.put("H_"+num_part_1+"_"+num_part_2, rapp_evol);
										if(ecurie_part!=null&&!"NP".equals(rapp_evol)&&rapp_evol!=null)
											htEvol.put("H_"+ecurie_part, rapp_evol);

									}
							}
							if(htEvol.get("Time")!=null){
								saveExactaToDB(RaceDate, TrackName, RaceNo, DayMeetingNo, htEvol, id_nav_reunion, id_nav_course, fileName);
							}
						}
					}
				}
			}
//			if (bPerMeetingCommit == false) {
//				InsertBulk("PmuInfoCentreKafka_XML_LiveOdds_Exacta",HorsecachedRS);							
//			}
		}
		catch (Exception e) 
		{
			logger.error("fileName = ****************"+fileName+"*******************************");
			logger.error("",e);
		}
	}
	
	private static void saveExactaToDB(String raceDate, String trackName,
			String raceNo, String dayMeetingNo,
			Hashtable<String, String> htRef, String id_nav_reunion,
			String id_nav_course, String fileName) {
		try {
			StringBuffer sSql =new StringBuffer(); 
			sSql=sSql.append(raceDate==null?"NULL,":"N'"+raceDate+"',");
			sSql=sSql.append(trackName==null?"NULL,":"N'"+trackName+"',");
			sSql=sSql.append(raceNo==null?"NULL,":"N'"+raceNo+"',");
			sSql=sSql.append(dayMeetingNo==null?"NULL,":"N'"+dayMeetingNo+"',");
			sSql=sSql.append(htRef.get("Time")==null?"NULL,":"N'"+htRef.get("Time")+"',");
			sSql=sSql.append(htRef.get("H_1_2")==null?"NULL,":"N'"+htRef.get("H_1_2")+"',");
			sSql=sSql.append(htRef.get("H_1_3")==null?"NULL,":"N'"+htRef.get("H_1_3")+"',");
			sSql=sSql.append(htRef.get("H_1_4")==null?"NULL,":"N'"+htRef.get("H_1_4")+"',");
			sSql=sSql.append(htRef.get("H_1_5")==null?"NULL,":"N'"+htRef.get("H_1_5")+"',");
			sSql=sSql.append(htRef.get("H_1_6")==null?"NULL,":"N'"+htRef.get("H_1_6")+"',");
			sSql=sSql.append(htRef.get("H_1_7")==null?"NULL,":"N'"+htRef.get("H_1_7")+"',");
			sSql=sSql.append(htRef.get("H_1_8")==null?"NULL,":"N'"+htRef.get("H_1_8")+"',");
			sSql=sSql.append(htRef.get("H_1_9")==null?"NULL,":"N'"+htRef.get("H_1_9")+"',");
			sSql=sSql.append(htRef.get("H_1_10")==null?"NULL,":"N'"+htRef.get("H_1_10")+"',");
			sSql=sSql.append(htRef.get("H_1_11")==null?"NULL,":"N'"+htRef.get("H_1_11")+"',");
			sSql=sSql.append(htRef.get("H_1_12")==null?"NULL,":"N'"+htRef.get("H_1_12")+"',");
			sSql=sSql.append(htRef.get("H_1_13")==null?"NULL,":"N'"+htRef.get("H_1_13")+"',");
			sSql=sSql.append(htRef.get("H_1_14")==null?"NULL,":"N'"+htRef.get("H_1_14")+"',");
			sSql=sSql.append(htRef.get("H_1_15")==null?"NULL,":"N'"+htRef.get("H_1_15")+"',");
			sSql=sSql.append(htRef.get("H_1_16")==null?"NULL,":"N'"+htRef.get("H_1_16")+"',");
			sSql=sSql.append(htRef.get("H_1_17")==null?"NULL,":"N'"+htRef.get("H_1_17")+"',");
			sSql=sSql.append(htRef.get("H_1_18")==null?"NULL,":"N'"+htRef.get("H_1_18")+"',");
			sSql=sSql.append(htRef.get("H_1_19")==null?"NULL,":"N'"+htRef.get("H_1_19")+"',");
			sSql=sSql.append(htRef.get("H_1_20")==null?"NULL,":"N'"+htRef.get("H_1_20")+"',");
			
			sSql=sSql.append(htRef.get("H_2_1")==null?"NULL,":"N'"+htRef.get("H_2_1")+"',");
			sSql=sSql.append(htRef.get("H_2_3")==null?"NULL,":"N'"+htRef.get("H_2_3")+"',");
			sSql=sSql.append(htRef.get("H_2_4")==null?"NULL,":"N'"+htRef.get("H_2_4")+"',");
			sSql=sSql.append(htRef.get("H_2_5")==null?"NULL,":"N'"+htRef.get("H_2_5")+"',");
			sSql=sSql.append(htRef.get("H_2_6")==null?"NULL,":"N'"+htRef.get("H_2_6")+"',");
			sSql=sSql.append(htRef.get("H_2_7")==null?"NULL,":"N'"+htRef.get("H_2_7")+"',");
			sSql=sSql.append(htRef.get("H_2_8")==null?"NULL,":"N'"+htRef.get("H_2_8")+"',");
			sSql=sSql.append(htRef.get("H_2_9")==null?"NULL,":"N'"+htRef.get("H_2_9")+"',");
			sSql=sSql.append(htRef.get("H_2_10")==null?"NULL,":"N'"+htRef.get("H_2_10")+"',");
			sSql=sSql.append(htRef.get("H_2_11")==null?"NULL,":"N'"+htRef.get("H_2_11")+"',");
			sSql=sSql.append(htRef.get("H_2_12")==null?"NULL,":"N'"+htRef.get("H_2_12")+"',");
			sSql=sSql.append(htRef.get("H_2_13")==null?"NULL,":"N'"+htRef.get("H_2_13")+"',");
			sSql=sSql.append(htRef.get("H_2_14")==null?"NULL,":"N'"+htRef.get("H_2_14")+"',");
			sSql=sSql.append(htRef.get("H_2_15")==null?"NULL,":"N'"+htRef.get("H_2_15")+"',");
			sSql=sSql.append(htRef.get("H_2_16")==null?"NULL,":"N'"+htRef.get("H_2_16")+"',");
			sSql=sSql.append(htRef.get("H_2_17")==null?"NULL,":"N'"+htRef.get("H_2_17")+"',");
			sSql=sSql.append(htRef.get("H_2_18")==null?"NULL,":"N'"+htRef.get("H_2_18")+"',");
			sSql=sSql.append(htRef.get("H_2_19")==null?"NULL,":"N'"+htRef.get("H_2_19")+"',");
			sSql=sSql.append(htRef.get("H_2_20")==null?"NULL,":"N'"+htRef.get("H_2_20")+"',");
			
			sSql=sSql.append(htRef.get("H_3_1")==null?"NULL,":"N'"+htRef.get("H_3_1")+"',");
			sSql=sSql.append(htRef.get("H_3_2")==null?"NULL,":"N'"+htRef.get("H_3_2")+"',");
			sSql=sSql.append(htRef.get("H_3_4")==null?"NULL,":"N'"+htRef.get("H_3_4")+"',");
			sSql=sSql.append(htRef.get("H_3_5")==null?"NULL,":"N'"+htRef.get("H_3_5")+"',");
			sSql=sSql.append(htRef.get("H_3_6")==null?"NULL,":"N'"+htRef.get("H_3_6")+"',");
			sSql=sSql.append(htRef.get("H_3_7")==null?"NULL,":"N'"+htRef.get("H_3_7")+"',");
			sSql=sSql.append(htRef.get("H_3_8")==null?"NULL,":"N'"+htRef.get("H_3_8")+"',");
			sSql=sSql.append(htRef.get("H_3_9")==null?"NULL,":"N'"+htRef.get("H_3_9")+"',");
			sSql=sSql.append(htRef.get("H_3_10")==null?"NULL,":"N'"+htRef.get("H_3_10")+"',");
			sSql=sSql.append(htRef.get("H_3_11")==null?"NULL,":"N'"+htRef.get("H_3_11")+"',");
			sSql=sSql.append(htRef.get("H_3_12")==null?"NULL,":"N'"+htRef.get("H_3_12")+"',");
			sSql=sSql.append(htRef.get("H_3_13")==null?"NULL,":"N'"+htRef.get("H_3_13")+"',");
			sSql=sSql.append(htRef.get("H_3_14")==null?"NULL,":"N'"+htRef.get("H_3_14")+"',");
			sSql=sSql.append(htRef.get("H_3_15")==null?"NULL,":"N'"+htRef.get("H_3_15")+"',");
			sSql=sSql.append(htRef.get("H_3_16")==null?"NULL,":"N'"+htRef.get("H_3_16")+"',");
			sSql=sSql.append(htRef.get("H_3_17")==null?"NULL,":"N'"+htRef.get("H_3_17")+"',");
			sSql=sSql.append(htRef.get("H_3_18")==null?"NULL,":"N'"+htRef.get("H_3_18")+"',");
			sSql=sSql.append(htRef.get("H_3_19")==null?"NULL,":"N'"+htRef.get("H_3_19")+"',");
			sSql=sSql.append(htRef.get("H_3_20")==null?"NULL,":"N'"+htRef.get("H_3_20")+"',");

			sSql=sSql.append(htRef.get("H_4_1")==null?"NULL,":"N'"+htRef.get("H_4_1")+"',");
			sSql=sSql.append(htRef.get("H_4_2")==null?"NULL,":"N'"+htRef.get("H_4_2")+"',");
			sSql=sSql.append(htRef.get("H_4_3")==null?"NULL,":"N'"+htRef.get("H_4_3")+"',");
			sSql=sSql.append(htRef.get("H_4_5")==null?"NULL,":"N'"+htRef.get("H_4_5")+"',");
			sSql=sSql.append(htRef.get("H_4_6")==null?"NULL,":"N'"+htRef.get("H_4_6")+"',");
			sSql=sSql.append(htRef.get("H_4_7")==null?"NULL,":"N'"+htRef.get("H_4_7")+"',");
			sSql=sSql.append(htRef.get("H_4_8")==null?"NULL,":"N'"+htRef.get("H_4_8")+"',");
			sSql=sSql.append(htRef.get("H_4_9")==null?"NULL,":"N'"+htRef.get("H_4_9")+"',");
			sSql=sSql.append(htRef.get("H_4_10")==null?"NULL,":"N'"+htRef.get("H_4_10")+"',");
			sSql=sSql.append(htRef.get("H_4_11")==null?"NULL,":"N'"+htRef.get("H_4_11")+"',");
			sSql=sSql.append(htRef.get("H_4_12")==null?"NULL,":"N'"+htRef.get("H_4_12")+"',");
			sSql=sSql.append(htRef.get("H_4_13")==null?"NULL,":"N'"+htRef.get("H_4_13")+"',");
			sSql=sSql.append(htRef.get("H_4_14")==null?"NULL,":"N'"+htRef.get("H_4_14")+"',");
			sSql=sSql.append(htRef.get("H_4_15")==null?"NULL,":"N'"+htRef.get("H_4_15")+"',");
			sSql=sSql.append(htRef.get("H_4_16")==null?"NULL,":"N'"+htRef.get("H_4_16")+"',");
			sSql=sSql.append(htRef.get("H_4_17")==null?"NULL,":"N'"+htRef.get("H_4_17")+"',");
			sSql=sSql.append(htRef.get("H_4_18")==null?"NULL,":"N'"+htRef.get("H_4_18")+"',");
			sSql=sSql.append(htRef.get("H_4_19")==null?"NULL,":"N'"+htRef.get("H_4_19")+"',");
			sSql=sSql.append(htRef.get("H_4_20")==null?"NULL,":"N'"+htRef.get("H_4_20")+"',");
			
			sSql=sSql.append(htRef.get("H_5_1")==null?"NULL,":"N'"+htRef.get("H_5_1")+"',");
			sSql=sSql.append(htRef.get("H_5_2")==null?"NULL,":"N'"+htRef.get("H_5_2")+"',");
			sSql=sSql.append(htRef.get("H_5_3")==null?"NULL,":"N'"+htRef.get("H_5_3")+"',");
			sSql=sSql.append(htRef.get("H_5_4")==null?"NULL,":"N'"+htRef.get("H_5_4")+"',");
			sSql=sSql.append(htRef.get("H_5_6")==null?"NULL,":"N'"+htRef.get("H_5_6")+"',");
			sSql=sSql.append(htRef.get("H_5_7")==null?"NULL,":"N'"+htRef.get("H_5_7")+"',");
			sSql=sSql.append(htRef.get("H_5_8")==null?"NULL,":"N'"+htRef.get("H_5_8")+"',");
			sSql=sSql.append(htRef.get("H_5_9")==null?"NULL,":"N'"+htRef.get("H_5_9")+"',");
			sSql=sSql.append(htRef.get("H_5_10")==null?"NULL,":"N'"+htRef.get("H_5_10")+"',");
			sSql=sSql.append(htRef.get("H_5_11")==null?"NULL,":"N'"+htRef.get("H_5_11")+"',");
			sSql=sSql.append(htRef.get("H_5_12")==null?"NULL,":"N'"+htRef.get("H_5_12")+"',");
			sSql=sSql.append(htRef.get("H_5_13")==null?"NULL,":"N'"+htRef.get("H_5_13")+"',");
			sSql=sSql.append(htRef.get("H_5_14")==null?"NULL,":"N'"+htRef.get("H_5_14")+"',");
			sSql=sSql.append(htRef.get("H_5_15")==null?"NULL,":"N'"+htRef.get("H_5_15")+"',");
			sSql=sSql.append(htRef.get("H_5_16")==null?"NULL,":"N'"+htRef.get("H_5_16")+"',");
			sSql=sSql.append(htRef.get("H_5_17")==null?"NULL,":"N'"+htRef.get("H_5_17")+"',");
			sSql=sSql.append(htRef.get("H_5_18")==null?"NULL,":"N'"+htRef.get("H_5_18")+"',");
			sSql=sSql.append(htRef.get("H_5_19")==null?"NULL,":"N'"+htRef.get("H_5_19")+"',");
			sSql=sSql.append(htRef.get("H_5_20")==null?"NULL,":"N'"+htRef.get("H_5_20")+"',");
			
			sSql=sSql.append(htRef.get("H_6_1")==null?"NULL,":"N'"+htRef.get("H_6_1")+"',");
			sSql=sSql.append(htRef.get("H_6_2")==null?"NULL,":"N'"+htRef.get("H_6_2")+"',");
			sSql=sSql.append(htRef.get("H_6_3")==null?"NULL,":"N'"+htRef.get("H_6_3")+"',");
			sSql=sSql.append(htRef.get("H_6_4")==null?"NULL,":"N'"+htRef.get("H_6_4")+"',");
			sSql=sSql.append(htRef.get("H_6_5")==null?"NULL,":"N'"+htRef.get("H_6_5")+"',");
			sSql=sSql.append(htRef.get("H_6_7")==null?"NULL,":"N'"+htRef.get("H_6_7")+"',");
			sSql=sSql.append(htRef.get("H_6_8")==null?"NULL,":"N'"+htRef.get("H_6_8")+"',");
			sSql=sSql.append(htRef.get("H_6_9")==null?"NULL,":"N'"+htRef.get("H_6_9")+"',");
			sSql=sSql.append(htRef.get("H_6_10")==null?"NULL,":"N'"+htRef.get("H_6_10")+"',");
			sSql=sSql.append(htRef.get("H_6_11")==null?"NULL,":"N'"+htRef.get("H_6_11")+"',");
			sSql=sSql.append(htRef.get("H_6_12")==null?"NULL,":"N'"+htRef.get("H_6_12")+"',");
			sSql=sSql.append(htRef.get("H_6_13")==null?"NULL,":"N'"+htRef.get("H_6_13")+"',");
			sSql=sSql.append(htRef.get("H_6_14")==null?"NULL,":"N'"+htRef.get("H_6_14")+"',");
			sSql=sSql.append(htRef.get("H_6_15")==null?"NULL,":"N'"+htRef.get("H_6_15")+"',");
			sSql=sSql.append(htRef.get("H_6_16")==null?"NULL,":"N'"+htRef.get("H_6_16")+"',");
			sSql=sSql.append(htRef.get("H_6_17")==null?"NULL,":"N'"+htRef.get("H_6_17")+"',");
			sSql=sSql.append(htRef.get("H_6_18")==null?"NULL,":"N'"+htRef.get("H_6_18")+"',");
			sSql=sSql.append(htRef.get("H_6_19")==null?"NULL,":"N'"+htRef.get("H_6_19")+"',");
			sSql=sSql.append(htRef.get("H_6_20")==null?"NULL,":"N'"+htRef.get("H_6_20")+"',");
			
			sSql=sSql.append(htRef.get("H_7_1")==null?"NULL,":"N'"+htRef.get("H_7_1")+"',");
			sSql=sSql.append(htRef.get("H_7_2")==null?"NULL,":"N'"+htRef.get("H_7_2")+"',");
			sSql=sSql.append(htRef.get("H_7_3")==null?"NULL,":"N'"+htRef.get("H_7_3")+"',");
			sSql=sSql.append(htRef.get("H_7_4")==null?"NULL,":"N'"+htRef.get("H_7_4")+"',");
			sSql=sSql.append(htRef.get("H_7_5")==null?"NULL,":"N'"+htRef.get("H_7_5")+"',");
			sSql=sSql.append(htRef.get("H_7_6")==null?"NULL,":"N'"+htRef.get("H_7_6")+"',");
			sSql=sSql.append(htRef.get("H_7_8")==null?"NULL,":"N'"+htRef.get("H_7_8")+"',");
			sSql=sSql.append(htRef.get("H_7_9")==null?"NULL,":"N'"+htRef.get("H_7_9")+"',");
			sSql=sSql.append(htRef.get("H_7_10")==null?"NULL,":"N'"+htRef.get("H_7_10")+"',");
			sSql=sSql.append(htRef.get("H_7_11")==null?"NULL,":"N'"+htRef.get("H_7_11")+"',");
			sSql=sSql.append(htRef.get("H_7_12")==null?"NULL,":"N'"+htRef.get("H_7_12")+"',");
			sSql=sSql.append(htRef.get("H_7_13")==null?"NULL,":"N'"+htRef.get("H_7_13")+"',");
			sSql=sSql.append(htRef.get("H_7_14")==null?"NULL,":"N'"+htRef.get("H_7_14")+"',");
			sSql=sSql.append(htRef.get("H_7_15")==null?"NULL,":"N'"+htRef.get("H_7_15")+"',");
			sSql=sSql.append(htRef.get("H_7_16")==null?"NULL,":"N'"+htRef.get("H_7_16")+"',");
			sSql=sSql.append(htRef.get("H_7_17")==null?"NULL,":"N'"+htRef.get("H_7_17")+"',");
			sSql=sSql.append(htRef.get("H_7_18")==null?"NULL,":"N'"+htRef.get("H_7_18")+"',");
			sSql=sSql.append(htRef.get("H_7_19")==null?"NULL,":"N'"+htRef.get("H_7_19")+"',");
			sSql=sSql.append(htRef.get("H_7_20")==null?"NULL,":"N'"+htRef.get("H_7_20")+"',");
		
			sSql=sSql.append(htRef.get("H_8_1")==null?"NULL,":"N'"+htRef.get("H_8_1")+"',");
			sSql=sSql.append(htRef.get("H_8_2")==null?"NULL,":"N'"+htRef.get("H_8_2")+"',");
			sSql=sSql.append(htRef.get("H_8_3")==null?"NULL,":"N'"+htRef.get("H_8_3")+"',");
			sSql=sSql.append(htRef.get("H_8_4")==null?"NULL,":"N'"+htRef.get("H_8_4")+"',");
			sSql=sSql.append(htRef.get("H_8_5")==null?"NULL,":"N'"+htRef.get("H_8_5")+"',");
			sSql=sSql.append(htRef.get("H_8_6")==null?"NULL,":"N'"+htRef.get("H_8_6")+"',");
			sSql=sSql.append(htRef.get("H_8_7")==null?"NULL,":"N'"+htRef.get("H_8_7")+"',");
			sSql=sSql.append(htRef.get("H_8_9")==null?"NULL,":"N'"+htRef.get("H_8_9")+"',");
			sSql=sSql.append(htRef.get("H_8_10")==null?"NULL,":"N'"+htRef.get("H_8_10")+"',");
			sSql=sSql.append(htRef.get("H_8_11")==null?"NULL,":"N'"+htRef.get("H_8_11")+"',");
			sSql=sSql.append(htRef.get("H_8_12")==null?"NULL,":"N'"+htRef.get("H_8_12")+"',");
			sSql=sSql.append(htRef.get("H_8_13")==null?"NULL,":"N'"+htRef.get("H_8_13")+"',");
			sSql=sSql.append(htRef.get("H_8_14")==null?"NULL,":"N'"+htRef.get("H_8_14")+"',");
			sSql=sSql.append(htRef.get("H_8_15")==null?"NULL,":"N'"+htRef.get("H_8_15")+"',");
			sSql=sSql.append(htRef.get("H_8_16")==null?"NULL,":"N'"+htRef.get("H_8_16")+"',");
			sSql=sSql.append(htRef.get("H_8_17")==null?"NULL,":"N'"+htRef.get("H_8_17")+"',");
			sSql=sSql.append(htRef.get("H_8_18")==null?"NULL,":"N'"+htRef.get("H_8_18")+"',");
			sSql=sSql.append(htRef.get("H_8_19")==null?"NULL,":"N'"+htRef.get("H_8_19")+"',");
			sSql=sSql.append(htRef.get("H_8_20")==null?"NULL,":"N'"+htRef.get("H_8_20")+"',");
		
			sSql=sSql.append(htRef.get("H_9_1")==null?"NULL,":"N'"+htRef.get("H_9_1")+"',");
			sSql=sSql.append(htRef.get("H_9_2")==null?"NULL,":"N'"+htRef.get("H_9_2")+"',");
			sSql=sSql.append(htRef.get("H_9_3")==null?"NULL,":"N'"+htRef.get("H_9_3")+"',");
			sSql=sSql.append(htRef.get("H_9_4")==null?"NULL,":"N'"+htRef.get("H_9_4")+"',");
			sSql=sSql.append(htRef.get("H_9_5")==null?"NULL,":"N'"+htRef.get("H_9_5")+"',");
			sSql=sSql.append(htRef.get("H_9_6")==null?"NULL,":"N'"+htRef.get("H_9_6")+"',");
			sSql=sSql.append(htRef.get("H_9_7")==null?"NULL,":"N'"+htRef.get("H_9_7")+"',");
			sSql=sSql.append(htRef.get("H_9_8")==null?"NULL,":"N'"+htRef.get("H_9_8")+"',");
			sSql=sSql.append(htRef.get("H_9_10")==null?"NULL,":"N'"+htRef.get("H_9_10")+"',");
			sSql=sSql.append(htRef.get("H_9_11")==null?"NULL,":"N'"+htRef.get("H_9_11")+"',");
			sSql=sSql.append(htRef.get("H_9_12")==null?"NULL,":"N'"+htRef.get("H_9_12")+"',");
			sSql=sSql.append(htRef.get("H_9_13")==null?"NULL,":"N'"+htRef.get("H_9_13")+"',");
			sSql=sSql.append(htRef.get("H_9_14")==null?"NULL,":"N'"+htRef.get("H_9_14")+"',");
			sSql=sSql.append(htRef.get("H_9_15")==null?"NULL,":"N'"+htRef.get("H_9_15")+"',");
			sSql=sSql.append(htRef.get("H_9_16")==null?"NULL,":"N'"+htRef.get("H_9_16")+"',");
			sSql=sSql.append(htRef.get("H_9_17")==null?"NULL,":"N'"+htRef.get("H_9_17")+"',");
			sSql=sSql.append(htRef.get("H_9_18")==null?"NULL,":"N'"+htRef.get("H_9_18")+"',");
			sSql=sSql.append(htRef.get("H_9_19")==null?"NULL,":"N'"+htRef.get("H_9_19")+"',");
			sSql=sSql.append(htRef.get("H_9_20")==null?"NULL,":"N'"+htRef.get("H_9_20")+"',");
	
			sSql=sSql.append(htRef.get("H_10_1")==null?"NULL,":"N'"+htRef.get("H_10_1")+"',");
			sSql=sSql.append(htRef.get("H_10_2")==null?"NULL,":"N'"+htRef.get("H_10_2")+"',");
			sSql=sSql.append(htRef.get("H_10_3")==null?"NULL,":"N'"+htRef.get("H_10_3")+"',");
			sSql=sSql.append(htRef.get("H_10_4")==null?"NULL,":"N'"+htRef.get("H_10_4")+"',");
			sSql=sSql.append(htRef.get("H_10_5")==null?"NULL,":"N'"+htRef.get("H_10_5")+"',");
			sSql=sSql.append(htRef.get("H_10_6")==null?"NULL,":"N'"+htRef.get("H_10_6")+"',");
			sSql=sSql.append(htRef.get("H_10_7")==null?"NULL,":"N'"+htRef.get("H_10_7")+"',");
			sSql=sSql.append(htRef.get("H_10_8")==null?"NULL,":"N'"+htRef.get("H_10_8")+"',");
			sSql=sSql.append(htRef.get("H_10_9")==null?"NULL,":"N'"+htRef.get("H_10_9")+"',");
			sSql=sSql.append(htRef.get("H_10_11")==null?"NULL,":"N'"+htRef.get("H_10_11")+"',");
			sSql=sSql.append(htRef.get("H_10_12")==null?"NULL,":"N'"+htRef.get("H_10_12")+"',");
			sSql=sSql.append(htRef.get("H_10_13")==null?"NULL,":"N'"+htRef.get("H_10_13")+"',");
			sSql=sSql.append(htRef.get("H_10_14")==null?"NULL,":"N'"+htRef.get("H_10_14")+"',");
			sSql=sSql.append(htRef.get("H_10_15")==null?"NULL,":"N'"+htRef.get("H_10_15")+"',");
			sSql=sSql.append(htRef.get("H_10_16")==null?"NULL,":"N'"+htRef.get("H_10_16")+"',");
			sSql=sSql.append(htRef.get("H_10_17")==null?"NULL,":"N'"+htRef.get("H_10_17")+"',");
			sSql=sSql.append(htRef.get("H_10_18")==null?"NULL,":"N'"+htRef.get("H_10_18")+"',");
			sSql=sSql.append(htRef.get("H_10_19")==null?"NULL,":"N'"+htRef.get("H_10_19")+"',");
			sSql=sSql.append(htRef.get("H_10_20")==null?"NULL,":"N'"+htRef.get("H_10_20")+"',");
		
			sSql=sSql.append(htRef.get("H_11_1")==null?"NULL,":"N'"+htRef.get("H_11_1")+"',");
			sSql=sSql.append(htRef.get("H_11_2")==null?"NULL,":"N'"+htRef.get("H_11_2")+"',");
			sSql=sSql.append(htRef.get("H_11_3")==null?"NULL,":"N'"+htRef.get("H_11_3")+"',");
			sSql=sSql.append(htRef.get("H_11_4")==null?"NULL,":"N'"+htRef.get("H_11_4")+"',");
			sSql=sSql.append(htRef.get("H_11_5")==null?"NULL,":"N'"+htRef.get("H_11_5")+"',");
			sSql=sSql.append(htRef.get("H_11_6")==null?"NULL,":"N'"+htRef.get("H_11_6")+"',");
			sSql=sSql.append(htRef.get("H_11_7")==null?"NULL,":"N'"+htRef.get("H_11_7")+"',");
			sSql=sSql.append(htRef.get("H_11_8")==null?"NULL,":"N'"+htRef.get("H_11_8")+"',");
			sSql=sSql.append(htRef.get("H_11_9")==null?"NULL,":"N'"+htRef.get("H_11_9")+"',");
			sSql=sSql.append(htRef.get("H_11_10")==null?"NULL,":"N'"+htRef.get("H_11_10")+"',");
			sSql=sSql.append(htRef.get("H_11_12")==null?"NULL,":"N'"+htRef.get("H_11_12")+"',");
			sSql=sSql.append(htRef.get("H_11_13")==null?"NULL,":"N'"+htRef.get("H_11_13")+"',");
			sSql=sSql.append(htRef.get("H_11_14")==null?"NULL,":"N'"+htRef.get("H_11_14")+"',");
			sSql=sSql.append(htRef.get("H_11_15")==null?"NULL,":"N'"+htRef.get("H_11_15")+"',");
			sSql=sSql.append(htRef.get("H_11_16")==null?"NULL,":"N'"+htRef.get("H_11_16")+"',");
			sSql=sSql.append(htRef.get("H_11_17")==null?"NULL,":"N'"+htRef.get("H_11_17")+"',");
			sSql=sSql.append(htRef.get("H_11_18")==null?"NULL,":"N'"+htRef.get("H_11_18")+"',");
			sSql=sSql.append(htRef.get("H_11_19")==null?"NULL,":"N'"+htRef.get("H_11_19")+"',");
			sSql=sSql.append(htRef.get("H_11_20")==null?"NULL,":"N'"+htRef.get("H_11_20")+"',");
		
			sSql=sSql.append(htRef.get("H_12_1")==null?"NULL,":"N'"+htRef.get("H_12_1")+"',");
			sSql=sSql.append(htRef.get("H_12_2")==null?"NULL,":"N'"+htRef.get("H_12_2")+"',");
			sSql=sSql.append(htRef.get("H_12_3")==null?"NULL,":"N'"+htRef.get("H_12_3")+"',");
			sSql=sSql.append(htRef.get("H_12_4")==null?"NULL,":"N'"+htRef.get("H_12_4")+"',");
			sSql=sSql.append(htRef.get("H_12_5")==null?"NULL,":"N'"+htRef.get("H_12_5")+"',");
			sSql=sSql.append(htRef.get("H_12_6")==null?"NULL,":"N'"+htRef.get("H_12_6")+"',");
			sSql=sSql.append(htRef.get("H_12_7")==null?"NULL,":"N'"+htRef.get("H_12_7")+"',");
			sSql=sSql.append(htRef.get("H_12_8")==null?"NULL,":"N'"+htRef.get("H_12_8")+"',");
			sSql=sSql.append(htRef.get("H_12_9")==null?"NULL,":"N'"+htRef.get("H_12_9")+"',");
			sSql=sSql.append(htRef.get("H_12_10")==null?"NULL,":"N'"+htRef.get("H_12_10")+"',");
			sSql=sSql.append(htRef.get("H_12_11")==null?"NULL,":"N'"+htRef.get("H_12_11")+"',");
			sSql=sSql.append(htRef.get("H_12_13")==null?"NULL,":"N'"+htRef.get("H_12_13")+"',");
			sSql=sSql.append(htRef.get("H_12_14")==null?"NULL,":"N'"+htRef.get("H_12_14")+"',");
			sSql=sSql.append(htRef.get("H_12_15")==null?"NULL,":"N'"+htRef.get("H_12_15")+"',");
			sSql=sSql.append(htRef.get("H_12_16")==null?"NULL,":"N'"+htRef.get("H_12_16")+"',");
			sSql=sSql.append(htRef.get("H_12_17")==null?"NULL,":"N'"+htRef.get("H_12_17")+"',");
			sSql=sSql.append(htRef.get("H_12_18")==null?"NULL,":"N'"+htRef.get("H_12_18")+"',");
			sSql=sSql.append(htRef.get("H_12_19")==null?"NULL,":"N'"+htRef.get("H_12_19")+"',");
			sSql=sSql.append(htRef.get("H_12_20")==null?"NULL,":"N'"+htRef.get("H_12_20")+"',");
	
			sSql=sSql.append(htRef.get("H_13_1")==null?"NULL,":"N'"+htRef.get("H_13_1")+"',");
			sSql=sSql.append(htRef.get("H_13_2")==null?"NULL,":"N'"+htRef.get("H_13_2")+"',");
			sSql=sSql.append(htRef.get("H_13_3")==null?"NULL,":"N'"+htRef.get("H_13_3")+"',");
			sSql=sSql.append(htRef.get("H_13_4")==null?"NULL,":"N'"+htRef.get("H_13_4")+"',");
			sSql=sSql.append(htRef.get("H_13_5")==null?"NULL,":"N'"+htRef.get("H_13_5")+"',");
			sSql=sSql.append(htRef.get("H_13_6")==null?"NULL,":"N'"+htRef.get("H_13_6")+"',");
			sSql=sSql.append(htRef.get("H_13_7")==null?"NULL,":"N'"+htRef.get("H_13_7")+"',");
			sSql=sSql.append(htRef.get("H_13_8")==null?"NULL,":"N'"+htRef.get("H_13_8")+"',");
			sSql=sSql.append(htRef.get("H_13_9")==null?"NULL,":"N'"+htRef.get("H_13_9")+"',");
			sSql=sSql.append(htRef.get("H_13_10")==null?"NULL,":"N'"+htRef.get("H_13_10")+"',");
			sSql=sSql.append(htRef.get("H_13_11")==null?"NULL,":"N'"+htRef.get("H_13_11")+"',");
			sSql=sSql.append(htRef.get("H_13_12")==null?"NULL,":"N'"+htRef.get("H_13_12")+"',");
			sSql=sSql.append(htRef.get("H_13_14")==null?"NULL,":"N'"+htRef.get("H_13_14")+"',");
			sSql=sSql.append(htRef.get("H_13_15")==null?"NULL,":"N'"+htRef.get("H_13_15")+"',");
			sSql=sSql.append(htRef.get("H_13_16")==null?"NULL,":"N'"+htRef.get("H_13_16")+"',");
			sSql=sSql.append(htRef.get("H_13_17")==null?"NULL,":"N'"+htRef.get("H_13_17")+"',");
			sSql=sSql.append(htRef.get("H_13_18")==null?"NULL,":"N'"+htRef.get("H_13_18")+"',");
			sSql=sSql.append(htRef.get("H_13_19")==null?"NULL,":"N'"+htRef.get("H_13_19")+"',");
			sSql=sSql.append(htRef.get("H_13_20")==null?"NULL,":"N'"+htRef.get("H_13_20")+"',");

			sSql=sSql.append(htRef.get("H_14_1")==null?"NULL,":"N'"+htRef.get("H_14_1")+"',");
			sSql=sSql.append(htRef.get("H_14_2")==null?"NULL,":"N'"+htRef.get("H_14_2")+"',");
			sSql=sSql.append(htRef.get("H_14_3")==null?"NULL,":"N'"+htRef.get("H_14_3")+"',");
			sSql=sSql.append(htRef.get("H_14_4")==null?"NULL,":"N'"+htRef.get("H_14_4")+"',");
			sSql=sSql.append(htRef.get("H_14_5")==null?"NULL,":"N'"+htRef.get("H_14_5")+"',");
			sSql=sSql.append(htRef.get("H_14_6")==null?"NULL,":"N'"+htRef.get("H_14_6")+"',");
			sSql=sSql.append(htRef.get("H_14_7")==null?"NULL,":"N'"+htRef.get("H_14_7")+"',");
			sSql=sSql.append(htRef.get("H_14_8")==null?"NULL,":"N'"+htRef.get("H_14_8")+"',");
			sSql=sSql.append(htRef.get("H_14_9")==null?"NULL,":"N'"+htRef.get("H_14_9")+"',");
			sSql=sSql.append(htRef.get("H_14_10")==null?"NULL,":"N'"+htRef.get("H_14_10")+"',");
			sSql=sSql.append(htRef.get("H_14_11")==null?"NULL,":"N'"+htRef.get("H_14_11")+"',");
			sSql=sSql.append(htRef.get("H_14_12")==null?"NULL,":"N'"+htRef.get("H_14_12")+"',");
			sSql=sSql.append(htRef.get("H_14_13")==null?"NULL,":"N'"+htRef.get("H_14_13")+"',");
			sSql=sSql.append(htRef.get("H_14_15")==null?"NULL,":"N'"+htRef.get("H_14_15")+"',");
			sSql=sSql.append(htRef.get("H_14_16")==null?"NULL,":"N'"+htRef.get("H_14_16")+"',");
			sSql=sSql.append(htRef.get("H_14_17")==null?"NULL,":"N'"+htRef.get("H_14_17")+"',");
			sSql=sSql.append(htRef.get("H_14_18")==null?"NULL,":"N'"+htRef.get("H_14_18")+"',");
			sSql=sSql.append(htRef.get("H_14_19")==null?"NULL,":"N'"+htRef.get("H_14_19")+"',");
			sSql=sSql.append(htRef.get("H_14_20")==null?"NULL,":"N'"+htRef.get("H_14_20")+"',");
			
			sSql=sSql.append(htRef.get("H_15_1")==null?"NULL,":"N'"+htRef.get("H_15_1")+"',");
			sSql=sSql.append(htRef.get("H_15_2")==null?"NULL,":"N'"+htRef.get("H_15_2")+"',");
			sSql=sSql.append(htRef.get("H_15_3")==null?"NULL,":"N'"+htRef.get("H_15_3")+"',");
			sSql=sSql.append(htRef.get("H_15_4")==null?"NULL,":"N'"+htRef.get("H_15_4")+"',");
			sSql=sSql.append(htRef.get("H_15_5")==null?"NULL,":"N'"+htRef.get("H_15_5")+"',");
			sSql=sSql.append(htRef.get("H_15_6")==null?"NULL,":"N'"+htRef.get("H_15_6")+"',");
			sSql=sSql.append(htRef.get("H_15_7")==null?"NULL,":"N'"+htRef.get("H_15_7")+"',");
			sSql=sSql.append(htRef.get("H_15_8")==null?"NULL,":"N'"+htRef.get("H_15_8")+"',");
			sSql=sSql.append(htRef.get("H_15_9")==null?"NULL,":"N'"+htRef.get("H_15_9")+"',");
			sSql=sSql.append(htRef.get("H_15_10")==null?"NULL,":"N'"+htRef.get("H_15_10")+"',");
			sSql=sSql.append(htRef.get("H_15_11")==null?"NULL,":"N'"+htRef.get("H_15_11")+"',");
			sSql=sSql.append(htRef.get("H_15_12")==null?"NULL,":"N'"+htRef.get("H_15_12")+"',");
			sSql=sSql.append(htRef.get("H_15_13")==null?"NULL,":"N'"+htRef.get("H_15_13")+"',");
			sSql=sSql.append(htRef.get("H_15_14")==null?"NULL,":"N'"+htRef.get("H_15_14")+"',");
			sSql=sSql.append(htRef.get("H_15_16")==null?"NULL,":"N'"+htRef.get("H_15_16")+"',");
			sSql=sSql.append(htRef.get("H_15_17")==null?"NULL,":"N'"+htRef.get("H_15_17")+"',");
			sSql=sSql.append(htRef.get("H_15_18")==null?"NULL,":"N'"+htRef.get("H_15_18")+"',");
			sSql=sSql.append(htRef.get("H_15_19")==null?"NULL,":"N'"+htRef.get("H_15_19")+"',");
			sSql=sSql.append(htRef.get("H_15_20")==null?"NULL,":"N'"+htRef.get("H_15_20")+"',");

			sSql=sSql.append(htRef.get("H_16_1")==null?"NULL,":"N'"+htRef.get("H_16_1")+"',");
			sSql=sSql.append(htRef.get("H_16_2")==null?"NULL,":"N'"+htRef.get("H_16_2")+"',");
			sSql=sSql.append(htRef.get("H_16_3")==null?"NULL,":"N'"+htRef.get("H_16_3")+"',");
			sSql=sSql.append(htRef.get("H_16_4")==null?"NULL,":"N'"+htRef.get("H_16_3")+"',");
			sSql=sSql.append(htRef.get("H_16_5")==null?"NULL,":"N'"+htRef.get("H_16_4")+"',");
			sSql=sSql.append(htRef.get("H_16_6")==null?"NULL,":"N'"+htRef.get("H_16_5")+"',");
			sSql=sSql.append(htRef.get("H_16_7")==null?"NULL,":"N'"+htRef.get("H_16_6")+"',");
			sSql=sSql.append(htRef.get("H_16_8")==null?"NULL,":"N'"+htRef.get("H_16_7")+"',");
			sSql=sSql.append(htRef.get("H_16_9")==null?"NULL,":"N'"+htRef.get("H_16_8")+"',");
			sSql=sSql.append(htRef.get("H_16_10")==null?"NULL,":"N'"+htRef.get("H_16_9")+"',");
			sSql=sSql.append(htRef.get("H_16_11")==null?"NULL,":"N'"+htRef.get("H_16_10")+"',");
			sSql=sSql.append(htRef.get("H_16_12")==null?"NULL,":"N'"+htRef.get("H_16_11")+"',");
			sSql=sSql.append(htRef.get("H_16_13")==null?"NULL,":"N'"+htRef.get("H_16_12")+"',");
			sSql=sSql.append(htRef.get("H_16_14")==null?"NULL,":"N'"+htRef.get("H_16_13")+"',");
			sSql=sSql.append(htRef.get("H_16_15")==null?"NULL,":"N'"+htRef.get("H_16_14")+"',");
			sSql=sSql.append(htRef.get("H_16_17")==null?"NULL,":"N'"+htRef.get("H_16_15")+"',");
			sSql=sSql.append(htRef.get("H_16_18")==null?"NULL,":"N'"+htRef.get("H_16_18")+"',");
			sSql=sSql.append(htRef.get("H_16_19")==null?"NULL,":"N'"+htRef.get("H_16_19")+"',");
			sSql=sSql.append(htRef.get("H_16_20")==null?"NULL,":"N'"+htRef.get("H_16_20")+"',");

			sSql=sSql.append(htRef.get("H_17_1")==null?"NULL,":"N'"+htRef.get("H_17_1")+"',");
			sSql=sSql.append(htRef.get("H_17_2")==null?"NULL,":"N'"+htRef.get("H_17_2")+"',");
			sSql=sSql.append(htRef.get("H_17_3")==null?"NULL,":"N'"+htRef.get("H_17_3")+"',");
			sSql=sSql.append(htRef.get("H_17_4")==null?"NULL,":"N'"+htRef.get("H_17_4")+"',");
			sSql=sSql.append(htRef.get("H_17_5")==null?"NULL,":"N'"+htRef.get("H_17_5")+"',");
			sSql=sSql.append(htRef.get("H_17_6")==null?"NULL,":"N'"+htRef.get("H_17_6")+"',");
			sSql=sSql.append(htRef.get("H_17_7")==null?"NULL,":"N'"+htRef.get("H_17_7")+"',");
			sSql=sSql.append(htRef.get("H_17_8")==null?"NULL,":"N'"+htRef.get("H_17_8")+"',");
			sSql=sSql.append(htRef.get("H_17_9")==null?"NULL,":"N'"+htRef.get("H_17_9")+"',");
			sSql=sSql.append(htRef.get("H_17_10")==null?"NULL,":"N'"+htRef.get("H_17_10")+"',");
			sSql=sSql.append(htRef.get("H_17_11")==null?"NULL,":"N'"+htRef.get("H_17_11")+"',");
			sSql=sSql.append(htRef.get("H_17_12")==null?"NULL,":"N'"+htRef.get("H_17_12")+"',");
			sSql=sSql.append(htRef.get("H_17_13")==null?"NULL,":"N'"+htRef.get("H_17_13")+"',");
			sSql=sSql.append(htRef.get("H_17_14")==null?"NULL,":"N'"+htRef.get("H_17_14")+"',");
			sSql=sSql.append(htRef.get("H_17_15")==null?"NULL,":"N'"+htRef.get("H_17_15")+"',");
			sSql=sSql.append(htRef.get("H_17_16")==null?"NULL,":"N'"+htRef.get("H_17_16")+"',");
			sSql=sSql.append(htRef.get("H_17_18")==null?"NULL,":"N'"+htRef.get("H_17_18")+"',");
			sSql=sSql.append(htRef.get("H_17_19")==null?"NULL,":"N'"+htRef.get("H_17_19")+"',");
			sSql=sSql.append(htRef.get("H_17_20")==null?"NULL,":"N'"+htRef.get("H_17_20")+"',");
			
			sSql=sSql.append(htRef.get("H_18_1")==null?"NULL,":"N'"+htRef.get("H_18_1")+"',");
			sSql=sSql.append(htRef.get("H_18_2")==null?"NULL,":"N'"+htRef.get("H_18_2")+"',");
			sSql=sSql.append(htRef.get("H_18_3")==null?"NULL,":"N'"+htRef.get("H_18_3")+"',");
			sSql=sSql.append(htRef.get("H_18_4")==null?"NULL,":"N'"+htRef.get("H_18_4")+"',");
			sSql=sSql.append(htRef.get("H_18_5")==null?"NULL,":"N'"+htRef.get("H_18_5")+"',");
			sSql=sSql.append(htRef.get("H_18_6")==null?"NULL,":"N'"+htRef.get("H_18_6")+"',");
			sSql=sSql.append(htRef.get("H_18_7")==null?"NULL,":"N'"+htRef.get("H_18_7")+"',");
			sSql=sSql.append(htRef.get("H_18_8")==null?"NULL,":"N'"+htRef.get("H_18_8")+"',");
			sSql=sSql.append(htRef.get("H_18_9")==null?"NULL,":"N'"+htRef.get("H_18_9")+"',");
			sSql=sSql.append(htRef.get("H_18_10")==null?"NULL,":"N'"+htRef.get("H_18_10")+"',");
			sSql=sSql.append(htRef.get("H_18_11")==null?"NULL,":"N'"+htRef.get("H_18_11")+"',");
			sSql=sSql.append(htRef.get("H_18_12")==null?"NULL,":"N'"+htRef.get("H_18_12")+"',");
			sSql=sSql.append(htRef.get("H_18_13")==null?"NULL,":"N'"+htRef.get("H_18_13")+"',");
			sSql=sSql.append(htRef.get("H_18_14")==null?"NULL,":"N'"+htRef.get("H_18_14")+"',");
			sSql=sSql.append(htRef.get("H_18_15")==null?"NULL,":"N'"+htRef.get("H_18_15")+"',");
			sSql=sSql.append(htRef.get("H_18_16")==null?"NULL,":"N'"+htRef.get("H_18_16")+"',");
			sSql=sSql.append(htRef.get("H_18_17")==null?"NULL,":"N'"+htRef.get("H_18_17")+"',");
			sSql=sSql.append(htRef.get("H_18_19")==null?"NULL,":"N'"+htRef.get("H_18_19")+"',");
			sSql=sSql.append(htRef.get("H_18_20")==null?"NULL,":"N'"+htRef.get("H_18_20")+"',");

			sSql=sSql.append(htRef.get("H_19_1")==null?"NULL,":"N'"+htRef.get("H_19_1")+"',");
			sSql=sSql.append(htRef.get("H_19_2")==null?"NULL,":"N'"+htRef.get("H_19_2")+"',");
			sSql=sSql.append(htRef.get("H_19_3")==null?"NULL,":"N'"+htRef.get("H_19_3")+"',");
			sSql=sSql.append(htRef.get("H_19_4")==null?"NULL,":"N'"+htRef.get("H_19_4")+"',");
			sSql=sSql.append(htRef.get("H_19_5")==null?"NULL,":"N'"+htRef.get("H_19_5")+"',");
			sSql=sSql.append(htRef.get("H_19_6")==null?"NULL,":"N'"+htRef.get("H_19_6")+"',");
			sSql=sSql.append(htRef.get("H_19_7")==null?"NULL,":"N'"+htRef.get("H_19_7")+"',");
			sSql=sSql.append(htRef.get("H_19_8")==null?"NULL,":"N'"+htRef.get("H_19_8")+"',");
			sSql=sSql.append(htRef.get("H_19_9")==null?"NULL,":"N'"+htRef.get("H_19_9")+"',");
			sSql=sSql.append(htRef.get("H_19_10")==null?"NULL,":"N'"+htRef.get("H_19_10")+"',");
			sSql=sSql.append(htRef.get("H_19_11")==null?"NULL,":"N'"+htRef.get("H_19_11")+"',");
			sSql=sSql.append(htRef.get("H_19_12")==null?"NULL,":"N'"+htRef.get("H_19_12")+"',");
			sSql=sSql.append(htRef.get("H_19_13")==null?"NULL,":"N'"+htRef.get("H_19_13")+"',");
			sSql=sSql.append(htRef.get("H_19_14")==null?"NULL,":"N'"+htRef.get("H_19_14")+"',");
			sSql=sSql.append(htRef.get("H_19_15")==null?"NULL,":"N'"+htRef.get("H_19_15")+"',");
			sSql=sSql.append(htRef.get("H_19_16")==null?"NULL,":"N'"+htRef.get("H_19_16")+"',");
			sSql=sSql.append(htRef.get("H_19_17")==null?"NULL,":"N'"+htRef.get("H_19_17")+"',");
			sSql=sSql.append(htRef.get("H_19_18")==null?"NULL,":"N'"+htRef.get("H_19_18")+"',");
			sSql=sSql.append(htRef.get("H_19_20")==null?"NULL,":"N'"+htRef.get("H_19_20")+"',");

			sSql=sSql.append(htRef.get("H_20_1")==null?"NULL,":"N'"+htRef.get("H_20_1")+"',");
			sSql=sSql.append(htRef.get("H_20_2")==null?"NULL,":"N'"+htRef.get("H_20_2")+"',");
			sSql=sSql.append(htRef.get("H_20_3")==null?"NULL,":"N'"+htRef.get("H_20_3")+"',");
			sSql=sSql.append(htRef.get("H_20_4")==null?"NULL,":"N'"+htRef.get("H_20_4")+"',");
			sSql=sSql.append(htRef.get("H_20_5")==null?"NULL,":"N'"+htRef.get("H_20_5")+"',");
			sSql=sSql.append(htRef.get("H_20_6")==null?"NULL,":"N'"+htRef.get("H_20_6")+"',");
			sSql=sSql.append(htRef.get("H_20_7")==null?"NULL,":"N'"+htRef.get("H_20_7")+"',");
			sSql=sSql.append(htRef.get("H_20_8")==null?"NULL,":"N'"+htRef.get("H_20_8")+"',");
			sSql=sSql.append(htRef.get("H_20_9")==null?"NULL,":"N'"+htRef.get("H_20_9")+"',");
			sSql=sSql.append(htRef.get("H_20_10")==null?"NULL,":"N'"+htRef.get("H_20_10")+"',");
			sSql=sSql.append(htRef.get("H_20_11")==null?"NULL,":"N'"+htRef.get("H_20_11")+"',");
			sSql=sSql.append(htRef.get("H_20_12")==null?"NULL,":"N'"+htRef.get("H_20_12")+"',");
			sSql=sSql.append(htRef.get("H_20_13")==null?"NULL,":"N'"+htRef.get("H_20_13")+"',");
			sSql=sSql.append(htRef.get("H_20_14")==null?"NULL,":"N'"+htRef.get("H_20_14")+"',");
			sSql=sSql.append(htRef.get("H_20_15")==null?"NULL,":"N'"+htRef.get("H_20_15")+"',");
			sSql=sSql.append(htRef.get("H_20_16")==null?"NULL,":"N'"+htRef.get("H_20_16")+"',");
			sSql=sSql.append(htRef.get("H_20_17")==null?"NULL,":"N'"+htRef.get("H_20_17")+"',");
			sSql=sSql.append(htRef.get("H_20_18")==null?"NULL,":"N'"+htRef.get("H_20_18")+"',");
			sSql=sSql.append(htRef.get("H_20_19")==null?"NULL,":"N'"+htRef.get("H_20_19")+"',");
			
			sSql=sSql.append(htRef.get("EnDirect")==null?"NULL,":"N'"+htRef.get("EnDirect")+"',");
			sSql=sSql.append(htRef.get("Corrupted")==null?"NULL,":"N'"+htRef.get("Corrupted")+"',");
			sSql=sSql.append(htRef.get("montant_enjeu_total")==null?"NULL,":"N'"+htRef.get("montant_enjeu_total")+"',");
			
			sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
			sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
			sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"',");
			sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"'");
			
			logger.info("pr_PmuInfoCentreKafka_XML_Live_Exacta_V2_InsertData  " + sSql);
			oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Live_Exacta_V2_InsertData", sSql.toString());
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
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\55_RAPP_PROB_CO_EVOL";
		new ParseLiveExacta55().run(filePath);
	}
}
