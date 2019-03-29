package com.datalabchina.bll;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.datalabchina.controler.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;

public class ParseGPR102 {
	private static Logger logger = Logger.getLogger(ParseGPR102.class.getName());
	private static CommonDB oCommonDB =new CommonDB();
	private static CommonMethod oCommonMethod = new CommonMethod();	
	
	public static void parseFile(String fileName)
	{
		String  body = FileDispose.readFile(fileName);
		if(body==null||body.length()<1)return;
		logger.info("Parse FileName ************************" +fileName+"***************************************");
		try {
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
				
				while(reunionMatcher.find())
				{
					String oneReunion =reunionMatcher.group(1); 
					String id_nav_reunion = oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					String num_externe_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					String code_hippo = oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String num_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>"); 

					Matcher courseMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courseMatcher.find())
					{
						String onecourse = courseMatcher.group(1);
						String id_nav_course = oCommonMethod.getValueByPatter(onecourse, "<id_nav_course>(.*?)</id_nav_course>");
						String num_course_pmu = oCommonMethod.getValueByPatter(onecourse, "<num_course_pmu>(.*?)</num_course_pmu>");
						
						Matcher syntheseMatcher = oCommonMethod.getMatcherStrGroup(onecourse, "<synthese>(.*?)</synthese>");
						while(syntheseMatcher.find())
						{
							String oneSynthese = syntheseMatcher.group(1);
							String rang2= oCommonMethod.getValueByPatter(oneSynthese, "<rang>(.*?)</rang>");
							String num_partant2= oCommonMethod.getValueByPatter(oneSynthese, "<num_partant>(.*?)</num_partant>");
							String nb_fois_premier= oCommonMethod.getValueByPatter(oneSynthese, "<nb_fois_premier>(.*?)</nb_fois_premier>");
							String nb_fois_second= oCommonMethod.getValueByPatter(oneSynthese, "<nb_fois_second>(.*?)</nb_fois_second>");
							StringBuffer sSql0 =new StringBuffer(); 
							sSql0=sSql0.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos+"',");
							sSql0=sSql0.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos+"',");
							sSql0=sSql0.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
							sSql0=sSql0.append(type_document==null?"NULL,":"N'"+type_document+"',");
							sSql0=sSql0.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
							sSql0=sSql0.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
							
							sSql0=sSql0.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion+"',");
							sSql0=sSql0.append(code_hippo==null?"NULL,":"N'"+code_hippo+"',");
							sSql0=sSql0.append(num_reunion==null?"NULL,":"N'"+num_reunion+"',");
							sSql0=sSql0.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
							sSql0=sSql0.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu+"',");
							sSql0=sSql0.append(rang2==null?"NULL,":"N'"+rang2+"',");
							sSql0=sSql0.append(num_partant2==null?"NULL,":"N'"+num_partant2+"',");
							sSql0=sSql0.append(nb_fois_premier==null?"NULL,":"N'"+nb_fois_premier+"',");
							sSql0=sSql0.append(nb_fois_second==null?"NULL,":"N'"+nb_fois_second+"',");
							
							sSql0=sSql0.append("'"+oCommonMethod.getCurrentTime()+"',");
							sSql0=sSql0.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
							logger.info("pr_PmuInfoCentreKafka_XML_Geny_MeetingPress_Syntheses_InsertData  " + sSql0);
							oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Geny_MeetingPress_Syntheses_InsertData", sSql0.toString());
							 sSql0=null;
						}
						Matcher journalMatcher = oCommonMethod.getMatcherStrGroup(onecourse, "<journal>(.*?)</journal>");
						while(journalMatcher.find()){
							String onejournal = journalMatcher.group(1);
							String societe = oCommonMethod.getValueByPatter(onejournal, "<societe>(.*?)</societe>");
							Matcher journalisteMatcher = oCommonMethod.getMatcherStrGroup(onejournal, "<journaliste>(.*?)</journaliste>");
							while(journalisteMatcher.find())
							{
								 String onejournaliste = journalisteMatcher.group(1);
								 String nom_journaliste =  oCommonMethod.getValueByPatter(onejournaliste, "<nom_journaliste>(.*?)</nom_journaliste>");
								 Matcher pronosticMatcher = oCommonMethod.getMatcherStrGroup(onejournaliste, "<pronostic>(.*?)</pronostic>");
									while(pronosticMatcher.find()){
										 String onepronostic = pronosticMatcher.group(1);
										 String rang =  oCommonMethod.getValueByPatter(onepronostic, "<rang>(.*?)</rang>");
										 String num_partant =  oCommonMethod.getValueByPatter(onepronostic, "<num_partant>(.*?)</num_partant>");
										 String nom_cheval =  oCommonMethod.getValueByPatter(onepronostic, "<nom_cheval>(.*?)</nom_cheval>");
										 
											StringBuffer sSql =new StringBuffer(); 
											sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos+"',");
											sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos+"',");
											sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
											sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document+"',");
											sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
											sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
											
											sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion+"',");
											sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo+"',");
											sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion+"',");
											sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
											sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu+"',");
											sSql=sSql.append(societe==null?"NULL,":"N'"+societe+"',");
											sSql=sSql.append(nom_journaliste==null?"NULL,":"N'"+nom_journaliste+"',");
											sSql=sSql.append(rang==null?"NULL,":"N'"+rang+"',");
											sSql=sSql.append(num_partant==null?"NULL,":"N'"+num_partant+"',");
											sSql=sSql.append(nom_cheval==null?"NULL,":"N'"+nom_cheval+"',");
											
											sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
											sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
											logger.info("pr_PmuInfoCentreKafka_XML_Geny_MeetingPress_Journals_InsertData  " + sSql);
											oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Geny_MeetingPress_Journals_InsertData", sSql.toString());
											 sSql=null;
									}
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
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\102_GNY_PRESS_REUNION";
		new ParseGPR102().run(filePath);
	}
}
