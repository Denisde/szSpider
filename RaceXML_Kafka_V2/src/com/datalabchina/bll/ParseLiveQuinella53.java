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
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import com.sun.rowset.CachedRowSetImpl;

public class ParseLiveQuinella53 {
	private static Logger logger = Logger.getLogger(ParseLiveQuinella53.class.getName());
	private static CommonDB oCommonDB =new CommonDB();
	private static CommonMethod oCommonMethod = new CommonMethod();	
//	static CachedRowSet RacecachedRS = null;
	static CachedRowSet HorsecachedRS = null;
	
//	private static String connectionString ="jdbc:sqlserver://192.168.120.216:1433;databaseName=FRDB;user=spider;password=83862909";
	private static String connectionString = Controller.connectionString;
	
	static boolean bPerMeetingCommit = false;
	
	public static void parseFile(String FileName)
	{
		String  body = FileDispose.readFile(FileName);
		if(body==null||body.length()<1)return;
		logger.info("Parse FileName ************************" +FileName+"***************************************");
		try {
//			RacecachedRS = new CachedRowSetImpl();
//			RacecachedRS.setUrl(connectionString);
//			RacecachedRS.setCommand("select * from PmuInfoCentreKafka_XML_LiveOdds_Quinella where 1=2");
//			RacecachedRS.execute();
//			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			
//			 HorsecachedRS = new CachedRowSetImpl();
//	         HorsecachedRS.setUrl(connectionString);
//	         
//	         HorsecachedRS.setCommand("select * from PmuInfoCentreKafka_XML_LiveOdds_Quinella where 1=2");
//	         HorsecachedRS.execute();        
			String RaceDate, TrackName, RaceNo, DayMeetingNo, TimeStamp, 
			EnDirect, CorruptedOdds, montant_enjeu_total, id_nav_reunion, id_nav_course, ExtractTime;
//			CommonDB oCommonDB =new CommonDB();
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find())
			{
				String oneJours = JoursMatcher.group(1);
				//trackName
				String code_statut_infos = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>"));
//				TrackName = code_statut_infos;
				
				String libelle_statut_infos = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos>(.*?)</libelle_statut_infos>"));
				String date_heure_generation = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>"));
				String type_document = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>"));
				String date_jour_reunion =CommonMethod.covertString( oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>"));
				Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(body, "<reunion>(.*?)</reunion>");
				
				while(reunionMatcher.find()){
					String oneReunion =reunionMatcher.group(1); 
					id_nav_reunion = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>"));
					
					//DayMeetingNo
					String num_externe_reunion = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>"));
					DayMeetingNo = num_externe_reunion;
					
					String code_hippo = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>"));
					TrackName = code_hippo;
					
					String type_reunion = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>"));
					//raceDate 
					String date_reunion = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>"));
					RaceDate = getDate(date_reunion);
					String num_reunion = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>")); 

					Matcher courseMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courseMatcher.find())
					{
						String onecourse = courseMatcher.group(1);
						Hashtable<String,String> htRef= new Hashtable<String,String>();
						htRef.put("EnDirect", "0");
						Hashtable<String,String> htEvol= new Hashtable<String,String>();
						htEvol.put("EnDirect", "1");

						id_nav_course = CommonMethod.covertString(oCommonMethod.getValueByPatter(onecourse, "<id_nav_course>(.*?)</id_nav_course>"));
						//raceNo
						String num_course_pmu = CommonMethod.covertString(oCommonMethod.getValueByPatter(onecourse, "<num_course_pmu>(.*?)</num_course_pmu>"));
						RaceNo = num_course_pmu;
						
						String libcourt_prix_course = CommonMethod.covertString(oCommonMethod.getValueByPatter(onecourse, "<libcourt_prix_course>(.*?)</libcourt_prix_course>"));
						
//						TrackName = libcourt_prix_course;
						//上一条数据更新时间 
						String heure_rap_ref = CommonMethod.covertString(oCommonMethod.getValueByPatter(onecourse, "<heure_rap_ref>(.*?)</heure_rap_ref>"));
						htRef.put("Time", RaceDate+" "+heure_rap_ref);
						//下一条数据更新时间 
						String heure_rap_evol = CommonMethod.covertString(oCommonMethod.getValueByPatter(onecourse, "<heure_rap_evol>(.*?)</heure_rap_evol>"));
						htEvol.put("Time",RaceDate+" "+heure_rap_evol);
						
						Matcher rapps_probsMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<rapps_probs>(.*?)</rapps_probs>");
						while(rapps_probsMatcher.find()){
							String onerapps_probs = rapps_probsMatcher.group(1);
							String audience_pari_course = CommonMethod.covertString(oCommonMethod.getValueByPatter(onerapps_probs, "<audience_pari_course>(.*?)</audience_pari_course>"));
							Matcher rapps_probs_partMatcher = oCommonMethod.getMatcherStrGroup(onerapps_probs, "<rapps_probs_part>(.*?)</rapps_probs_part>");
							while(rapps_probs_partMatcher.find()){
								 String onerapps_probs_part =rapps_probs_partMatcher.group(1); 
								 
								 String num_part = CommonMethod.covertString(oCommonMethod.getValueByPatter(onerapps_probs_part, "<num_part>(.*?)</num_part>"));
									//horseName
								 String nom_cheval = CommonMethod.covertString(oCommonMethod.getValueByPatter(onerapps_probs_part, "<nom_cheval>(.*?)</nom_cheval>"));
//								 System.out.println(nom_cheval);
								 String ecurie_part = CommonMethod.covertString(oCommonMethod.getValueByPatter(onerapps_probs_part, "<ecurie_part>(.*?)</ecurie_part>"));
								 Matcher combinaisonMatcher = oCommonMethod.getMatcherStrGroup(onerapps_probs_part, "<combinaison>(.*?)</combinaison>");
									while(combinaisonMatcher.find()){
										String oneCombinaison = combinaisonMatcher.group(1);
										String num_part_1 = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneCombinaison, "<num_part_1>(.*?)</num_part_1>"));
										String statut_part_1 = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneCombinaison, "<statut_part_1>(.*?)</statut_part_1>"));
										String num_part_2 = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneCombinaison, "<num_part_2>(.*?)</num_part_2>"));
										String statut_part_2 = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneCombinaison, "<statut_part_2>(.*?)</statut_part_2>"));
										String rapp_ref = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneCombinaison, "<rapp_ref>(.*?)</rapp_ref>"));
										// H_16=NP
										rapp_ref=rapp_ref==null?null:rapp_ref.replace(".", "").replace(",", ".");
										if(rapp_ref!=null&&!"NP".equals(rapp_ref))
											htRef.put("H_"+num_part_1+"_"+num_part_2,rapp_ref);
										if(ecurie_part!=null&&!"NP".equals(rapp_ref)&&rapp_ref!=null)
											htRef.put("H_"+ecurie_part,rapp_ref);

										String rapp_evol = CommonMethod.covertString(oCommonMethod.getValueByPatter(oneCombinaison, "<rapp_evol>(.*?)</rapp_evol>"));
										rapp_evol=rapp_evol==null?null:rapp_evol.replace(".", "").replace(",", ".");
										if(rapp_evol!=null&&!"NP".equals(rapp_evol))
											htEvol.put("H_"+num_part_1+"_"+num_part_2, rapp_evol);
										if(ecurie_part!=null&&rapp_evol!=null&&!"NP".equals(rapp_evol))
											htEvol.put("H_"+ecurie_part, rapp_evol);
										
									}
//									System.out.println(htRef);
//									System.out.println(htEvol);
									if(htRef.get("Time")!=null){
//										saveQuinellaToDB(RaceDate, TrackName, RaceNo, DayMeetingNo, htRef, id_nav_reunion, id_nav_course, FileName);
										saveQuinellaToDB(RaceDate, TrackName, RaceNo, DayMeetingNo, htEvol, id_nav_reunion, id_nav_course, FileName);
									}
							}
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
			logger.error("fileName = ****************"+FileName+"*******************************");
			logger.error("",e);
		}
	}
	
	private static void saveQuinellaToDB(String raceDate, String trackName,
			String raceNo, String dayMeetingNo,
			Hashtable<String, String> htRef, 
			String id_nav_reunion, String id_nav_course, String fileName) {
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
		
			sSql=sSql.append(htRef.get("H_11_12")==null?"NULL,":"N'"+htRef.get("H_11_12")+"',");
			sSql=sSql.append(htRef.get("H_11_13")==null?"NULL,":"N'"+htRef.get("H_11_13")+"',");
			sSql=sSql.append(htRef.get("H_11_14")==null?"NULL,":"N'"+htRef.get("H_11_14")+"',");
			sSql=sSql.append(htRef.get("H_11_15")==null?"NULL,":"N'"+htRef.get("H_11_15")+"',");
			sSql=sSql.append(htRef.get("H_11_16")==null?"NULL,":"N'"+htRef.get("H_11_16")+"',");
			sSql=sSql.append(htRef.get("H_11_17")==null?"NULL,":"N'"+htRef.get("H_11_17")+"',");
			sSql=sSql.append(htRef.get("H_11_18")==null?"NULL,":"N'"+htRef.get("H_11_18")+"',");
			sSql=sSql.append(htRef.get("H_11_19")==null?"NULL,":"N'"+htRef.get("H_11_19")+"',");
			sSql=sSql.append(htRef.get("H_11_20")==null?"NULL,":"N'"+htRef.get("H_11_20")+"',");
		
			sSql=sSql.append(htRef.get("H_12_13")==null?"NULL,":"N'"+htRef.get("H_12_13")+"',");
			sSql=sSql.append(htRef.get("H_12_14")==null?"NULL,":"N'"+htRef.get("H_12_14")+"',");
			sSql=sSql.append(htRef.get("H_12_15")==null?"NULL,":"N'"+htRef.get("H_12_15")+"',");
			sSql=sSql.append(htRef.get("H_12_16")==null?"NULL,":"N'"+htRef.get("H_12_16")+"',");
			sSql=sSql.append(htRef.get("H_12_17")==null?"NULL,":"N'"+htRef.get("H_12_17")+"',");
			sSql=sSql.append(htRef.get("H_12_18")==null?"NULL,":"N'"+htRef.get("H_12_18")+"',");
			sSql=sSql.append(htRef.get("H_12_19")==null?"NULL,":"N'"+htRef.get("H_12_19")+"',");
			sSql=sSql.append(htRef.get("H_12_20")==null?"NULL,":"N'"+htRef.get("H_12_20")+"',");
	
			sSql=sSql.append(htRef.get("H_13_14")==null?"NULL,":"N'"+htRef.get("H_13_14")+"',");
			sSql=sSql.append(htRef.get("H_13_15")==null?"NULL,":"N'"+htRef.get("H_13_15")+"',");
			sSql=sSql.append(htRef.get("H_13_16")==null?"NULL,":"N'"+htRef.get("H_13_16")+"',");
			sSql=sSql.append(htRef.get("H_13_17")==null?"NULL,":"N'"+htRef.get("H_13_17")+"',");
			sSql=sSql.append(htRef.get("H_13_18")==null?"NULL,":"N'"+htRef.get("H_13_18")+"',");
			sSql=sSql.append(htRef.get("H_13_19")==null?"NULL,":"N'"+htRef.get("H_13_19")+"',");
			sSql=sSql.append(htRef.get("H_13_20")==null?"NULL,":"N'"+htRef.get("H_13_20")+"',");

			sSql=sSql.append(htRef.get("H_14_15")==null?"NULL,":"N'"+htRef.get("H_14_15")+"',");
			sSql=sSql.append(htRef.get("H_14_16")==null?"NULL,":"N'"+htRef.get("H_14_16")+"',");
			sSql=sSql.append(htRef.get("H_14_17")==null?"NULL,":"N'"+htRef.get("H_14_17")+"',");
			sSql=sSql.append(htRef.get("H_14_18")==null?"NULL,":"N'"+htRef.get("H_14_18")+"',");
			sSql=sSql.append(htRef.get("H_14_19")==null?"NULL,":"N'"+htRef.get("H_14_19")+"',");
			sSql=sSql.append(htRef.get("H_14_20")==null?"NULL,":"N'"+htRef.get("H_14_20")+"',");
			
			sSql=sSql.append(htRef.get("H_15_16")==null?"NULL,":"N'"+htRef.get("H_15_16")+"',");
			sSql=sSql.append(htRef.get("H_15_17")==null?"NULL,":"N'"+htRef.get("H_15_17")+"',");
			sSql=sSql.append(htRef.get("H_15_18")==null?"NULL,":"N'"+htRef.get("H_15_18")+"',");
			sSql=sSql.append(htRef.get("H_15_19")==null?"NULL,":"N'"+htRef.get("H_15_19")+"',");
			sSql=sSql.append(htRef.get("H_15_20")==null?"NULL,":"N'"+htRef.get("H_15_20")+"',");

			sSql=sSql.append(htRef.get("H_16_17")==null?"NULL,":"N'"+htRef.get("H_16_17")+"',");
			sSql=sSql.append(htRef.get("H_16_18")==null?"NULL,":"N'"+htRef.get("H_16_18")+"',");
			sSql=sSql.append(htRef.get("H_16_19")==null?"NULL,":"N'"+htRef.get("H_16_19")+"',");
			sSql=sSql.append(htRef.get("H_16_20")==null?"NULL,":"N'"+htRef.get("H_16_20")+"',");

			sSql=sSql.append(htRef.get("H_17_18")==null?"NULL,":"N'"+htRef.get("H_17_18")+"',");
			sSql=sSql.append(htRef.get("H_17_19")==null?"NULL,":"N'"+htRef.get("H_17_19")+"',");
			sSql=sSql.append(htRef.get("H_17_20")==null?"NULL,":"N'"+htRef.get("H_17_20")+"',");
			
			sSql=sSql.append(htRef.get("H_18_19")==null?"NULL,":"N'"+htRef.get("H_18_19")+"',");
			sSql=sSql.append(htRef.get("H_18_20")==null?"NULL,":"N'"+htRef.get("H_18_20")+"',");

			sSql=sSql.append(htRef.get("H_19_20")==null?"NULL,":"N'"+htRef.get("H_19_20")+"',");
			sSql=sSql.append(htRef.get("EnDirect")==null?"NULL,":"N'"+htRef.get("EnDirect")+"',");
			sSql=sSql.append(htRef.get("Corrupted")==null?"NULL,":"N'"+htRef.get("Corrupted")+"',");
			sSql=sSql.append(htRef.get("montant_enjeu_total")==null?"NULL,":"N'"+htRef.get("montant_enjeu_total")+"',");
			
			sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
			sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
			sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"',");
			sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"'");
			
			logger.info("pr_PmuInfoCentreKafka_XML_Live_Quinella_V2_InsertData  " + sSql);
			oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Live_Quinella_V2_InsertData", sSql.toString());
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
	
}
