package com.datalabchina.bll;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.datalabchina.controler.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;


public class ParseRaceReport23Bll {
	private  Logger logger = Logger.getLogger(ParseRaceReport23Bll.class.getName());
	private  CommonDB oCommonDB =new CommonDB();
	private  CommonMethod oCommonMethod = new CommonMethod();	


	public  void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\23_COMPTE_RENDU\\77729706.xml";
		String  body = FileDispose.readFile(fileName);
		try {
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find()){
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
				
				String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos>(.*?)</libelle_statut_infos>");
				
				String date_heure_generation = oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
				
				String type_document = oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>");
				
				String date_jour_reunion = oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>");
				
				
				Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(oneJours, "<reunion>(.*?)</reunion>");
				while(reunionMatcher.find()){
					String oneReunion = reunionMatcher.group(1);
					String  id_nav_reunion= oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					String  num_externe_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					String  code_hippo= oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String  num_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					
					Matcher courses = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courses.find()){
						String course = courses.group(1);
						String id_nav_course = oCommonMethod.getValueByPatter(course, "<id_nav_course>(.*?)</id_nav_course>");
						String num_course_pmu = oCommonMethod.getValueByPatter(course, "<num_course_pmu>(.*?)</num_course_pmu>");
						String etat_arrivee = oCommonMethod.getValueByPatter(course, "<etat_arrivee translate=\"yes\"\\s?>(.*?)</etat_arrivee>");
						String temps_course = oCommonMethod.getValueByPatter(course, "<temps_course>(.*?)</temps_course>");
						
						Matcher partants = oCommonMethod.getMatcherStrGroup(course, "<partant>(.*?)</partant>");
						while(partants.find()){
							String partant = partants.group(1);
							String num_partant = oCommonMethod.getValueByPatter(partant, "<num_partant>(.*?)</num_partant>");
							String nom_cheval = oCommonMethod.getValueByPatter(partant, "<nom_cheval>(.*?)</nom_cheval>");
							String suffixe_cheval = oCommonMethod.getValueByPatter(partant, "<suffixe_cheval>(.*?)</suffixe_cheval>");
							String id_nav_partant = oCommonMethod.getValueByPatter(partant, "<id_nav_partant>(.*?)</id_nav_partant>");
							String num_place_arrivee = oCommonMethod.getValueByPatter(partant, "<num_place_arrivee translate=\"yes\"\\s?>(.*?)</num_place_arrivee>");
							String texte_place_arrivee = oCommonMethod.getValueByPatter(partant, "<texte_place_arrivee translate=\"yes\"\\s?>(.*?)</texte_place_arrivee>");
						
							StringBuffer sSql =new StringBuffer(); 
//							String sSql ="";
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
						     sSql=sSql.append(etat_arrivee==null?"NULL,":"N'"+etat_arrivee+"',");
						     sSql=sSql.append(temps_course==null?"NULL,":"N'"+temps_course+"',");
						     sSql=sSql.append(num_partant==null?"NULL,":"N'"+num_partant+"',");
						     sSql=sSql.append(nom_cheval==null?"NULL,":"N'"+nom_cheval+"',");
						     sSql=sSql.append(suffixe_cheval==null?"NULL,":"N'"+suffixe_cheval+"',");
						     sSql=sSql.append(id_nav_partant==null?"NULL,":"N'"+id_nav_partant+"',");
						     sSql=sSql.append(num_place_arrivee==null?"NULL,":"N'"+num_place_arrivee+"',");
						     sSql=sSql.append(texte_place_arrivee==null?"NULL,":"N'"+texte_place_arrivee+"',");
								
								sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
								sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
						     logger.info("pr_PmuInfoCentreKafka_XML_RaceReport_InsertData  " + sSql);	
						     oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_RaceReport_InsertData", sSql.toString());
						     sSql=null;
						}
						partants=null;
					}
					courses=null;
				}
			}
		}
		catch (Exception e) 
		{
			logger.error("error fileName:"+fileName+"\n\n",e);
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
}
