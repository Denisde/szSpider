package com.datalabchina.bll;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.datalabchina.controler.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;


public class ParseRaceBetType16Bll {
	private  Logger logger = Logger.getLogger(ParseRaceBetType16Bll.class.getName());
	private  CommonDB oCommonDB =new CommonDB();
	private CommonMethod oCommonMethod = new CommonMethod();	

	

	public void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\16_PARIS_PROPOSES\\77727888.xml";
		String  body = FileDispose.readFile(fileName);
		try {
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find()){
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
				
				String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos translate=\"yes\"\\s?>(.*?)</libelle_statut_infos>");
				
				
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
						String num_course_pmu = oCommonMethod.getValueByPatter(course, "<num_course_pmu>(.*?)</num_course_pmu>");
						String libcourt_prix_course = oCommonMethod.getValueByPatter(course, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
						String id_nav_course = oCommonMethod.getValueByPatter(course, "<id_nav_course>(.*?)</id_nav_course>");
						
						Matcher paris_course = oCommonMethod.getMatcherStrGroup(course, "<pari_course>(.*?)</pari_course>");
						while(paris_course.find()){
							String pari_course = paris_course.group(1);
							String lib_audience_pari = oCommonMethod.getValueByPatter(pari_course, "<lib_audience_pari translate=\"yes\"\\s?>(.*?)</lib_audience_pari>");
							String picto_pari_course = oCommonMethod.getValueByPatter(pari_course, "<picto_pari_course>(.*?)</picto_pari_course>");
							String code_pari = oCommonMethod.getValueByPatter(pari_course, "<code_pari>(.*?)</code_pari>");
							String libcourt_pari_course = oCommonMethod.getValueByPatter(pari_course, "<libcourt_pari_course>(.*?)</libcourt_pari_course>");
							String liblong_pari_course = oCommonMethod.getValueByPatter(pari_course, "<liblong_pari_course>(.*?)</liblong_pari_course>");
							String pays1_offre_pari_course = oCommonMethod.getValueByPatter(pari_course, "<pays1_offre_pari_course>(.*?)</pays1_offre_pari_course>");
							String pays2_offre_pari_course = oCommonMethod.getValueByPatter(pari_course, "<pays2_offre_pari_course>(.*?)</pays2_offre_pari_course>");
							String pays3_offre_pari_course = oCommonMethod.getValueByPatter(pari_course, "<pays3_offre_pari_course>(.*?)</pays3_offre_pari_course>");
							String pays4_offre_pari_course = oCommonMethod.getValueByPatter(pari_course, "<pays4_offre_pari_course>(.*?)</pays4_offre_pari_course>");
							String pays5_offre_pari_course = oCommonMethod.getValueByPatter(pari_course, "<pays5_offre_pari_course>(.*?)</pays5_offre_pari_course>");
							String audience_pari_course = oCommonMethod.getValueByPatter(pari_course, "<audience_pari_course>(.*?)</audience_pari_course>");
							String devise_pari_course = oCommonMethod.getValueByPatter(pari_course, "<devise_pari_course>(.*?)</devise_pari_course>");
							String montant_pari_course = oCommonMethod.getValueByPatter(pari_course, "<montant_pari_course>(.*?)</montant_pari_course>");
							String code_pari_generique = oCommonMethod.getValueByPatter(pari_course, "<code_pari_generique>(.*?)</code_pari_generique>");
							String spot = oCommonMethod.getValueByPatter(pari_course, "<spot>(.*?)</spot>");
							String channel = oCommonMethod.getValueByPatter(pari_course, "<channel>(.*?)</channel>");
							String booster = oCommonMethod.getValueByPatter(pari_course, "<booster>(.*?)</booster>");
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
						     sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu+"',");
						     sSql=sSql.append(libcourt_prix_course==null?"NULL,":"N'"+libcourt_prix_course+"',");
						     sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
						     sSql=sSql.append(lib_audience_pari==null?"NULL,":"N'"+lib_audience_pari+"',");
						     sSql=sSql.append(picto_pari_course==null?"NULL,":"N'"+picto_pari_course+"',");
						     sSql=sSql.append(code_pari==null?"NULL,":"N'"+code_pari+"',");
						     sSql=sSql.append(libcourt_pari_course==null?"NULL,":"N'"+libcourt_pari_course+"',");
						     sSql=sSql.append(liblong_pari_course==null?"NULL,":"N'"+liblong_pari_course+"',");
						     sSql=sSql.append(pays1_offre_pari_course==null?"NULL,":"N'"+pays1_offre_pari_course+"',");
						     sSql=sSql.append(pays2_offre_pari_course==null?"NULL,":"N'"+pays2_offre_pari_course+"',");
						     sSql=sSql.append(pays3_offre_pari_course==null?"NULL,":"N'"+pays3_offre_pari_course+"',");
						     sSql=sSql.append(pays4_offre_pari_course==null?"NULL,":"N'"+pays4_offre_pari_course+"',");
						     sSql=sSql.append(pays5_offre_pari_course==null?"NULL,":"N'"+pays5_offre_pari_course+"',");
						     sSql=sSql.append(audience_pari_course==null?"NULL,":"N'"+audience_pari_course+"',");
						     sSql=sSql.append(devise_pari_course==null?"NULL,":"N'"+devise_pari_course+"',");
						     sSql=sSql.append(montant_pari_course==null?"NULL,":"N'"+montant_pari_course+"',");
						     sSql=sSql.append(code_pari_generique==null?"NULL,":"N'"+code_pari_generique+"',");
						     sSql=sSql.append(spot==null?"NULL,":"N'"+spot+"',");
						     sSql=sSql.append(channel==null?"NULL,":"N'"+channel+"',");
						     sSql=sSql.append(booster==null?"NULL,":"N'"+booster+"',");
								
								sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
								sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
						     logger.info("pr_PmuInfoCentreKafka_XML_RaceBetType_InsertData  " + sSql);
						     oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_RaceBetType_InsertData", sSql.toString());
						     sSql=null;
						}
					}
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
