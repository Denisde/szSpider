package com.datalabchina.bll;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.datalabchina.controler.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;


public class ParseWinningNumber37Bll {
	private  Logger logger = Logger.getLogger(ParseWinningNumber37Bll.class.getName());
	private  CommonDB oCommonDB =new CommonDB();
	private  CommonMethod oCommonMethod = new CommonMethod();	


	public void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\37_NUMERO_PLUS_GAGNANT\\77736896.xml";
		String  body = FileDispose.readFile(fileName);
		try {
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find()){
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
//				System.out.println(code_statut_infos );
				
				String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos>(.*?)</libelle_statut_infos>");
//				System.out.println(libelle_statut_infos );

				String date_heure_generation = oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
//				System.out.println(date_heure_generation );
				
				String type_document = oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>");
//				System.out.println(type_document );
				
				String date_jour_reunion = oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>");
//				System.out.println(date_jour_reunion );
				
				Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(oneJours, "<reunion>(.*?)</reunion>");
				while(reunionMatcher.find()){
					String oneReunion = reunionMatcher.group(1);
					String  code_hippo= oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String  id_nav_reunion= oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					String  num_externe_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					String  type_reunion= oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>");
					String  date_reunion= oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
					String  num_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					
					Matcher courses = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courses.find()){
						String course = courses.group(1);
						
						String libcourt_prix_course = oCommonMethod.getValueByPatter(course, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
						String num_course_pmu = oCommonMethod.getValueByPatter(course, "<num_course_pmu>(.*?)</num_course_pmu>");
						String id_nav_course = oCommonMethod.getValueByPatter(course, "<id_nav_course>(.*?)</id_nav_course>");
						
						Matcher paris_course = oCommonMethod.getMatcherStrGroup(course, "<pari_course>(.*?)</pari_course>");
						while(paris_course.find()){
							String pari_course = paris_course.group(1);
							String code_pari_generique = oCommonMethod.getValueByPatter(pari_course, "<code_pari_generique>(.*?)</code_pari_generique>");
							String numero_gagnant = oCommonMethod.getValueByPatter(pari_course, "<numero_gagnant>(.*?)</numero_gagnant>");
							
//							String sSql ="";
							StringBuffer sSql =new StringBuffer(); 
						     sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos+"',");
						     sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos+"',");
						     sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
						     sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document+"',");
						     sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
						     sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo+"',");
						     sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
						     sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion+"',");
						     sSql=sSql.append(type_reunion==null?"NULL,":"N'"+type_reunion+"',");
						     sSql=sSql.append(date_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_reunion)+"',");
						     sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion+"',");
						     sSql=sSql.append(libcourt_prix_course==null?"NULL,":"N'"+libcourt_prix_course+"',");
						     sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu+"',");
						     sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
						     sSql=sSql.append(code_pari_generique==null?"NULL,":"N'"+code_pari_generique+"',");
						     sSql=sSql.append(numero_gagnant==null?"NULL,":"N'"+numero_gagnant+"',");
								
								sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
								sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
						     logger.info("pr_PmuInfoCentreKafka_XML_WinningNumber_InsertData  " + sSql);	
						     oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_WinningNumber_InsertData", sSql.toString());
						     sSql=null;
						}
						paris_course=null;
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
