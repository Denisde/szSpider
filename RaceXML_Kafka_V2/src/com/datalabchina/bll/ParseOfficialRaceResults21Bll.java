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


public class ParseOfficialRaceResults21Bll {
	private  static Logger logger = Logger.getLogger(ParseRaceInformation04Bll.class.getName());
	private  static CommonDB oCommonDB =new CommonDB();
	private  static CommonMethod oCommonMethod = new CommonMethod();	


	public static void parseFile(String fileName){

//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\21_ARRIVEE/77728524.xml";
		String  body = FileDispose.readFile(fileName);
		try {
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find()){
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
//				System.out.println(code_statut_infos );
				
				String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos translate=\"yes\"\\s?>(.*?)</libelle_statut_infos>");
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
					String  id_nav_reunion= oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					String  num_externe_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					String  code_hippo= oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String  num_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					
					Matcher courses = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courses.find()){
						String course = courses.group(1);
						String id_nav_course = oCommonMethod.getValueByPatter(course, "<id_nav_course>(.*?)</id_nav_course>");
						String non_partant = null;
						String non_partant0 = oCommonMethod.getValueByPatter(course, "<non_partant>(.*?)</non_partant>");
						if(non_partant0!=null&&non_partant0.length()>1){
							String num_non_partant0 =oCommonMethod.getValueByPatter(non_partant0, "<num_non_partant>(.*?)</num_non_partant>");
							String nom_non_partant0 =oCommonMethod.getValueByPatter(non_partant0, "<nom_non_partant>(.*?)</nom_non_partant>");
							String nom_jockey0 =oCommonMethod.getValueByPatter(non_partant0, "<nom_jockey>(.*?)</nom_jockey>");
							String nom_entraineur0 =oCommonMethod.getValueByPatter(non_partant0, "<nom_entraineur>(.*?)</nom_entraineur>");
							String nom_proprietaire0 =oCommonMethod.getValueByPatter(non_partant0, "<nom_proprietaire>(.*?)</nom_proprietaire>");
							 non_partant = "num_non_partant :"+num_non_partant0+"nom_non_partant :"
							+nom_non_partant0+"nom_jockey :"+nom_jockey0+"nom_entraineur :"+nom_entraineur0
							+"nom_proprietaire :"+nom_proprietaire0;
						}
						
						String num_course_pmu = oCommonMethod.getValueByPatter(course, "<num_course_pmu>(.*?)</num_course_pmu>");
						String temps_course = oCommonMethod.getValueByPatter(course, "<temps_course>(.*?)</temps_course>");
						String etat_arrivee = oCommonMethod.getValueByPatter(course, "<etat_arrivee>(.*?)</etat_arrivee>");
						String lib_tous_couru = oCommonMethod.getValueByPatter(course, "<lib_tous_couru>(.*?)</lib_tous_couru>");
						
						Matcher partants = oCommonMethod.getMatcherStrGroup(course, "<partant>(.*?)</partant>");
						while(partants.find())
						{
							String partant = partants.group(1);
							String id_nav_partant = oCommonMethod.getValueByPatter(partant, "<id_nav_partant>(.*?)</id_nav_partant>");
							String num_partant = oCommonMethod.getValueByPatter(partant, "<num_partant>(.*?)</num_partant>");
							String nom_cheval = oCommonMethod.getValueByPatter(partant, "<nom_cheval>(.*?)</nom_cheval>");
							String suffixe_cheval = oCommonMethod.getValueByPatter(partant, "<suffixe_cheval>(.*?)</suffixe_cheval>");
							String pds_calc_hand_partant = oCommonMethod.getValueByPatter(partant, "<pds_calc_hand_partant>(.*?)</pds_calc_hand_partant>");
							String pds_cond_monte_partant = oCommonMethod.getValueByPatter(partant, "<pds_cond_monte_partant>(.*?)</pds_cond_monte_partant>");
							String nom_jockey = oCommonMethod.getValueByPatter(partant, "<nom_jockey>(.*?)</nom_jockey>");
							String nom_entraineur = oCommonMethod.getValueByPatter(partant, "<nom_entraineur>(.*?)</nom_entraineur>");
							String nom_proprietaire = oCommonMethod.getValueByPatter(partant, "<nom_proprietaire>(.*?)</nom_proprietaire>");
							String num_place_arrivee = oCommonMethod.getValueByPatter(partant, "<num_place_arrivee>(.*?)</num_place_arrivee>");
							String dist_partant = oCommonMethod.getValueByPatter(partant, "<dist_partant>(.*?)</dist_partant>");
							String texte_place_arrivee = oCommonMethod.getValueByPatter(partant, "<texte_place_arrivee>(.*?)</texte_place_arrivee>");
							String reduction_km = oCommonMethod.getValueByPatter(partant, "<reduction_km>(.*?)</reduction_km>");
							String temps_part = oCommonMethod.getValueByPatter(partant, "<temps_part translate=\"mandatory\"\\s?>(.*?)</temps_part>");
							
//							pds_calc_hand_partant=pds_calc_hand_partant==null?null:pds_calc_hand_partant.replace(".", "").replace(",", ".");
//							dist_partant=dist_partant==null?null:dist_partant.replace(".", "").replace(",", ".");
//							String sSql ="";
							StringBuffer sSql =new StringBuffer(); 
						     sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos.replaceAll("'", "''")+"',");
						     sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos.replaceAll("'", "''")+"',");
						     sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
						     sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document.replaceAll("'", "''")+"',");
						     sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
						     sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion.replaceAll("'", "''")+"',");
						     sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion.replaceAll("'", "''")+"',");
						     sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo.replaceAll("'", "''")+"',");
						     sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion.replaceAll("'", "''")+"',");
						     sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course.replaceAll("'", "''")+"',");
						     sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu.replaceAll("'", "''")+"',");
						     sSql=sSql.append(temps_course==null?"NULL,":"N'"+temps_course.replaceAll("'", "''")+"',");
						     sSql=sSql.append(etat_arrivee==null?"NULL,":"N'"+etat_arrivee.replaceAll("'", "''")+"',");
						     sSql=sSql.append(lib_tous_couru==null?"NULL,":"N'"+lib_tous_couru.replaceAll("'", "''")+"',");
						     sSql=sSql.append(id_nav_partant==null?"NULL,":"N'"+id_nav_partant.replaceAll("'", "''")+"',");
						     sSql=sSql.append(num_partant==null?"NULL,":"N'"+num_partant.replaceAll("'", "''")+"',");
						     sSql=sSql.append(nom_cheval==null?"NULL,":"N'"+nom_cheval.replaceAll("'", "''")+"',");
						     sSql=sSql.append(suffixe_cheval==null?"NULL,":"N'"+suffixe_cheval.replaceAll("'", "''")+"',");
						     sSql=sSql.append(pds_calc_hand_partant==null?"NULL,":"N'"+pds_calc_hand_partant.replace(".", "").replace(",", ".")+"',");
						     sSql=sSql.append(pds_cond_monte_partant==null?"NULL,":"N'"+pds_cond_monte_partant.replaceAll("'", "''")+"',");
						     sSql=sSql.append(nom_jockey==null?"NULL,":"N'"+nom_jockey.replaceAll("'", "''")+"',");
						     sSql=sSql.append(nom_entraineur==null?"NULL,":"N'"+nom_entraineur.replaceAll("'", "''")+"',");
						     sSql=sSql.append(nom_proprietaire==null?"NULL,":"N'"+nom_proprietaire.replaceAll("'", "''")+"',");
						     sSql=sSql.append(num_place_arrivee==null?"NULL,":"N'"+num_place_arrivee.replaceAll("'", "''")+"',");
						     sSql=sSql.append(dist_partant==null?"NULL,":"N'"+dist_partant.replace(".", "").replace(",", ".")+"',");
						     sSql=sSql.append(texte_place_arrivee==null?"NULL,":"N'"+texte_place_arrivee.replaceAll("'", "''")+"',");
						     sSql=sSql.append(reduction_km==null?"NULL,":"N'"+reduction_km.replaceAll("'", "''")+"',");
						     sSql=sSql.append(temps_part==null?"NULL,":"N'"+temps_part.replaceAll("'", "''")+"',");
								
							sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
							sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"',");

							sSql=sSql.append(non_partant==null?"NULL":"N'"+non_partant+"'");
							
						     logger.info("pr_PmuInfoCentreKafka_XML_OfficialRaceResults_InsertData  " + sSql);	
						     oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_OfficialRaceResults_InsertData", sSql.toString());
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
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\21_ARRIVEE";
		new ParseOfficialRaceResults21Bll().run(filePath);
	}
}
