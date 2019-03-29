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

public class ParseGSPQ103 {
	private static Logger logger = Logger.getLogger(ParseGSPQ103.class.getName());
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
						Matcher JournalsMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<journal>(.*?)</journal>");
						while(JournalsMatcher.find()){
							String oneJournal= JournalsMatcher.group(1);
							String societe  = oCommonMethod.getValueByPatter(oneJournal, "<societe>(.*?)</societe>");
							String journaliste  = oCommonMethod.getValueByPatter(oneJournal, "<journaliste>(.*?)</journaliste>");
							String rg_journaliste  = oCommonMethod.getValueByPatter(oneJournal, "<rg_journaliste>(.*?)</rg_journaliste>");
							String nb_pts  = oCommonMethod.getValueByPatter(oneJournal, "<nb_pts>(.*?)</nb_pts>");
							String tierce_nb  = oCommonMethod.getValueByPatter(oneJournal, "<tierce_nb>(.*?)</tierce_nb>");
							String tierce_ecart  = oCommonMethod.getValueByPatter(oneJournal, "<tierce_ecart>(.*?)</tierce_ecart>");
							String quarte_nb  = oCommonMethod.getValueByPatter(oneJournal, "<quarte_nb>(.*?)</quarte_nb>");
							String quarte_ecart  = oCommonMethod.getValueByPatter(oneJournal, "<quarte_ecart>(.*?)</quarte_ecart>");
							String quinte_nb  = oCommonMethod.getValueByPatter(oneJournal, "<quinte_nb>(.*?)</quinte_nb>");
							String quinte_ecart  = oCommonMethod.getValueByPatter(oneJournal, "<quinte_ecart>(.*?)</quinte_ecart>");
							
							Matcher selectionsMatcher = oCommonMethod.getMatcherStrGroup(oneJournal, "<selection>(.*?)</selection>");
							while(selectionsMatcher.find()){
								String oneSelection = selectionsMatcher.group(1);
								String num_partant  = oCommonMethod.getValueByPatter(oneSelection, "<num_partant>(.*?)</num_partant>");
								String nom_cheval  = oCommonMethod.getValueByPatter(oneSelection, "<nom_cheval>(.*?)</nom_cheval>");
								String rg_partant  = oCommonMethod.getValueByPatter(oneSelection, "<rg_partant>(.*?)</rg_partant>");
								
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
								sSql=sSql.append(societe==null?"NULL,":"N'"+societe+"',");
								sSql=sSql.append(journaliste==null?"NULL,":"N'"+journaliste+"',");
								sSql=sSql.append(rg_journaliste==null?"NULL,":"N'"+rg_journaliste+"',");
								sSql=sSql.append(nb_pts==null?"NULL,":"N'"+nb_pts+"',");
								sSql=sSql.append(tierce_nb==null?"NULL,":"N'"+tierce_nb+"',");
								sSql=sSql.append(tierce_ecart==null?"NULL,":"N'"+tierce_ecart+"',");
								sSql=sSql.append(quarte_nb==null?"NULL,":"N'"+quarte_nb+"',");
								sSql=sSql.append(quarte_ecart==null?"NULL,":"N'"+quarte_ecart+"',");
								sSql=sSql.append(quinte_nb==null?"NULL,":"N'"+quinte_nb+"',");
								sSql=sSql.append(quinte_ecart==null?"NULL,":"N'"+quinte_ecart+"',");
								sSql=sSql.append(num_partant==null?"NULL,":"N'"+num_partant+"',");
								sSql=sSql.append(nom_cheval==null?"NULL,":"N'"+nom_cheval+"',");
								sSql=sSql.append(rg_partant==null?"NULL,":"N'"+rg_partant+"',");
								
								
								sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
								sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
								logger.info("pr_PmuInfoCentreKafka_XML_Geny_PressSelectionQuinte_InsertData  " + sSql);
								oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Geny_PressSelectionQuinte_InsertData", sSql.toString());
								 sSql=null;
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
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\103_GNY_SELECTION_PRESS_Q5";
		new ParseGSPQ103().run(filePath);
	}
}
