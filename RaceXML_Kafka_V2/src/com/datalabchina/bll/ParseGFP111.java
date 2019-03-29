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

public class ParseGFP111 {
	private static Logger logger = Logger.getLogger(ParseGFP111.class.getName());
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
					String num_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>"); 
					String code_hippo = oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String num_externe_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");

					Matcher courseMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courseMatcher.find())
					{
							String onecourse = courseMatcher.group(1);
							String id_nav_course = oCommonMethod.getValueByPatter(onecourse, "<id_nav_course>(.*?)</id_nav_course>");
							String num_course_pmu = oCommonMethod.getValueByPatter(onecourse, "<num_course_pmu>(.*?)</num_course_pmu>");
							String discipline = oCommonMethod.getValueByPatter(onecourse, "<discipline>(.*?)</discipline>");
							Matcher partantMatcher = oCommonMethod.getMatcherStrGroup(onecourse, "<partant>(.*?)</partant>");
							while(partantMatcher.find())
							{
								String onepartant= partantMatcher.group(1);
								String num_partant = oCommonMethod.getValueByPatter(onepartant, "<num_partant>(.*?)</num_partant>");
								String nom_cheval = oCommonMethod.getValueByPatter(onepartant, "<nom_cheval>(.*?)</nom_cheval>");
								String nom_entraineur = oCommonMethod.getValueByPatter(onepartant, "<nom_entraineur>(.*?)</nom_entraineur>");
								String nom_monte = oCommonMethod.getValueByPatter(onepartant, "<nom_monte>(.*?)</nom_monte>");
								String nom_pere = oCommonMethod.getValueByPatter(onepartant, "<nom_pere>(.*?)</nom_pere>");
								String nom_mere = oCommonMethod.getValueByPatter(onepartant, "<nom_mere>(.*?)</nom_mere>");
								String dist_partant = oCommonMethod.getValueByPatter(onepartant, "<dist_partant>(.*?)</dist_partant>");
								String pds = oCommonMethod.getValueByPatter(onepartant, "<pds>(.*?)</pds>");
								String musique_partant = oCommonMethod.getValueByPatter(onepartant, "<musique_partant>(.*?)</musique_partant>");
								String commentaire  = oCommonMethod.getValueByPatter(onepartant, "<commentaire.*?>(.*?)</commentaire>");
								
//								String sSql ="";
								StringBuffer sSql =new StringBuffer(); 
								
								sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos+"',");
								sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos+"',");
								sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
								sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document+"',");
								sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
								sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
								
								sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion+"',");
								sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo+"',");
								sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion+"',");
								sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
								sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu+"',");
								sSql=sSql.append(discipline==null?"NULL,":"N'"+discipline+"',");
								sSql=sSql.append(num_partant==null?"NULL,":"N'"+num_partant+"',");
								sSql=sSql.append(nom_cheval==null?"NULL,":"N'"+nom_cheval+"',");
								sSql=sSql.append(nom_entraineur==null?"NULL,":"N'"+nom_entraineur+"',");
								sSql=sSql.append(nom_monte==null?"NULL,":"N'"+nom_monte+"',");
								sSql=sSql.append(nom_pere==null?"NULL,":"N'"+nom_pere+"',");
								sSql=sSql.append(nom_mere==null?"NULL,":"N'"+nom_mere+"',");
								sSql=sSql.append(dist_partant==null?"NULL,":"N'"+dist_partant+"',");
								sSql=sSql.append(musique_partant==null?"NULL,":"N'"+musique_partant+"',");
								sSql=sSql.append(commentaire==null?"NULL,":"N'"+commentaire+"',");
								sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
								sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"',");
								sSql=sSql.append(pds==null?"NULL":"N'"+pds+"'");
								logger.info("pr_PmuInfoCentreKafka_XML_Geny_FormComments_InsertData  " + sSql);
								oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Geny_FormComments_InsertData", sSql.toString());
								 sSql=null;
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
	
	public static void main(String[] args) 
	{
		PropertyConfigurator.configure("log4j.properties");
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\111_GNY_FORCES_PRESENCE";
		new ParseGFP111().run(filePath);
	}
}
