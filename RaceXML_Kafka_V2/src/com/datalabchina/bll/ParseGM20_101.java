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



public class ParseGM20_101 {
	private static Logger logger = Logger.getLogger(ParseGM20_101.class.getName());
	private static CommonDB oCommonDB =new CommonDB();
	private static CommonMethod oCommonMethod = new CommonMethod();	
	
	public static void parseFile(String fileName)
	{
		String body = FileDispose.readFile(fileName);
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
				
				while(reunionMatcher.find()){
					String oneReunion =reunionMatcher.group(1); 
					String id_nav_reunion = oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					String num_externe_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					String code_hippo = oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String num_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>"); 

					Matcher typeMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<type>(.*?)</type>");
					while(typeMatcher.find())
					{
						String onetype = typeMatcher.group(1);
						String libelle_type = oCommonMethod.getValueByPatter(onetype, "<libelle_type>(.*?)</libelle_type>");
						String commentaire = oCommonMethod.getValueByPatter(onetype, "<commentaire.*?>(.*?)</commentaire>");
						
						Matcher statMatcher = oCommonMethod.getMatcherStrGroup(onetype, "<stat>(.*?)</stat>");
						while(statMatcher.find()){
							String oneStat = statMatcher.group(1);
							String nom = oCommonMethod.getValueByPatter(oneStat, "<nom>(.*?)</nom>");
							String nb_courses_courues = oCommonMethod.getValueByPatter(oneStat, "<nb_courses_courues>(.*?)</nb_courses_courues>");
							String nb_victoires = oCommonMethod.getValueByPatter(oneStat, "<nb_victoires>(.*?)</nb_victoires>");
							String nb_places = oCommonMethod.getValueByPatter(oneStat, "<nb_places>(.*?)</nb_places>");
							String taux_reussite = oCommonMethod.getValueByPatter(oneStat, "<taux_reussite>(.*?)</taux_reussite>");
							String ecart = oCommonMethod.getValueByPatter(oneStat, "<ecart>(.*?)</ecart>");
							
//							String sSql ="";
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
							sSql=sSql.append(libelle_type==null?"NULL,":"N'"+libelle_type+"',");
							sSql=sSql.append(commentaire==null?"NULL,":"N'"+commentaire+"',");
							sSql=sSql.append(nom==null?"NULL,":"N'"+nom+"',");
							sSql=sSql.append(nb_courses_courues==null?"NULL,":"N'"+nb_courses_courues+"',");
							sSql=sSql.append(nb_victoires==null?"NULL,":"N'"+nb_victoires+"',");
							sSql=sSql.append(nb_places==null?"NULL,":"N'"+nb_places+"',");
							sSql=sSql.append(taux_reussite==null?"NULL,":"N'"+taux_reussite+"',");
							sSql=sSql.append(ecart==null?"NULL,":"N'"+ecart+"',");
							
							sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
							sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
							logger.info("pr_PmuInfoCentreKafka_XML_Geny_MeetingTop20_InsertData  " + sSql);
							oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Geny_MeetingTop20_InsertData", sSql.toString());
							sSql=null;
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
			logger.info("fileName = ****************"+fileName+"*******************************");
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
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\101_GNY_20_MEILLEURS";
		new ParseGM20_101().run(filePath);
	}
}
