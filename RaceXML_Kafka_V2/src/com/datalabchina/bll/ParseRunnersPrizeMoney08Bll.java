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


public class ParseRunnersPrizeMoney08Bll {
	private  Logger logger = Logger.getLogger(ParseRunnersPrizeMoney08Bll.class.getName());
	private  CommonDB oCommonDB =new CommonDB();
	private  CommonMethod oCommonMethod = new CommonMethod();	


	public  void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\08_PART_CARRIERE_GAINS\\77727887.xml";
		String  body = FileDispose.readFile(fileName);

		logger.info("Parse FileName ************************" +fileName+"***************************************");
		try {
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find()){
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
				
				String date_heure_generation = oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
				
				String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos>(.*?)</libelle_statut_infos>");
				
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
						
						
						Matcher partants = oCommonMethod.getMatcherStrGroup(course, "<partant>(.*?)</partant>");
						while(partants.find()){
							String partant = partants.group(1);
							String id_nav_partant = oCommonMethod.getValueByPatter(partant, "<id_nav_partant>(.*?)</id_nav_partant>");
							String num_partant = oCommonMethod.getValueByPatter(partant, "<num_partant>(.*?)</num_partant>");
							String nom_cheval = oCommonMethod.getValueByPatter(partant, "<nom_cheval>(.*?)</nom_cheval>");
							String gains_carr_partant = oCommonMethod.getValueByPatter(partant, "<gains_carr_partant>(.*?)</gains_carr_partant>");
							String musique_partant = oCommonMethod.getValueByPatter(partant, "<musique_partant>(.*?)</musique_partant>");
							String gains_vict_partant = oCommonMethod.getValueByPatter(partant, "<gains_vict_partant>(.*?)</gains_vict_partant>");
							String gains_pla_partant = oCommonMethod.getValueByPatter(partant, "<gains_pla_partant>(.*?)</gains_pla_partant>");
							String nb_courses_partant = oCommonMethod.getValueByPatter(partant, "<nb_courses_partant>(.*?)</nb_courses_partant>");
							String nb_vict_partant = oCommonMethod.getValueByPatter(partant, "<nb_vict_partant>(.*?)</nb_vict_partant>");
							String nb_places_partant = oCommonMethod.getValueByPatter(partant, "<nb_places_partant>(.*?)</nb_places_partant>");
							String inedit_partant = oCommonMethod.getValueByPatter(partant, "<inedit_partant>(.*?)</inedit_partant>");
							String record_partant = oCommonMethod.getValueByPatter(partant, "<record_partant>(.*?)</record_partant>");
							String discipline_record_partant = oCommonMethod.getValueByPatter(partant, "<discipline_record_partant>(.*?)</discipline_record_partant>");
							String intention_deferrer = oCommonMethod.getValueByPatter(partant, "<intention_deferrer>(.*?)</intention_deferrer>");
							String valeur = oCommonMethod.getValueByPatter(partant, "<valeur>(.*?)</valeur>");
							String gains_annee = oCommonMethod.getValueByPatter(partant, "<gains_annee>(.*?)</gains_annee>");
							//,
							String gains_annee_prec = oCommonMethod.getValueByPatter(partant, "<gains_annee_prec>(.*?)</gains_annee_prec>");
							String nb_courses_anne = oCommonMethod.getValueByPatter(partant, "<nb_courses_anne>(.*?)</nb_courses_anne>");
							String nb_premier_annee = oCommonMethod.getValueByPatter(partant, "<nb_premier_annee>(.*?)</nb_premier_annee>");
							String nb_deuxieme_anne = oCommonMethod.getValueByPatter(partant, "<nb_deuxieme_anne>(.*?)</nb_deuxieme_anne>");
							String nb_troisieme_annee = oCommonMethod.getValueByPatter(partant, "<nb_troisieme_annee>(.*?)</nb_troisieme_annee>");
							String nb_courses_annee_prec = oCommonMethod.getValueByPatter(partant, "<nb_courses_annee_prec>(.*?)</nb_courses_annee_prec>");
							String nb_premier_annee_prec = oCommonMethod.getValueByPatter(partant, "<nb_premier_annee_prec>(.*?)</nb_premier_annee_prec>");
							String nb_deuxieme_annee_prec = oCommonMethod.getValueByPatter(partant, "<nb_deuxieme_annee_prec>(.*?)</nb_deuxieme_annee_prec>");
							String nb_troisieme_annee_prec = oCommonMethod.getValueByPatter(partant, "<nb_troisieme_annee_prec>(.*?)</nb_troisieme_annee_prec>");
							String nb_courses_total = oCommonMethod.getValueByPatter(partant, "<nb_courses_total>(.*?)</nb_courses_total>");
							String nb_premier_total = oCommonMethod.getValueByPatter(partant, "<nb_premier_total>(.*?)</nb_premier_total>");
							String nb_deuxieme_total = oCommonMethod.getValueByPatter(partant, "<nb_deuxieme_total>(.*?)</nb_deuxieme_total>");
							String nb_troisieme_total = oCommonMethod.getValueByPatter(partant, "<nb_troisieme_total>(.*?)</nb_troisieme_total>");
							
							Matcher courses_records = oCommonMethod.getMatcherStrGroup(partant, "<course_record>(.*?)</course_record>");
							while(courses_records.find())
							{
								String course_record = courses_records.group(1);
								
								String temps_record = oCommonMethod.getValueByPatter(course_record, "<temps_record>(.*?)</temps_record>");
								String distance_record = oCommonMethod.getValueByPatter(course_record, "<distance_record>(.*?)</distance_record>");
								String discipline_record = oCommonMethod.getValueByPatter(course_record, "<discipline_record>(.*?)</discipline_record>");
								String type_depart_record = oCommonMethod.getValueByPatter(course_record, "<type_depart_record>(.*?)</type_depart_record>");
								String hippodrome_record = oCommonMethod.getValueByPatter(course_record, "<hippodrome_record>(.*?)</hippodrome_record>");
								
//								gains_carr_partant=gains_carr_partant==null?null:gains_carr_partant.replace(".", "").replace(",", "，");
//								gains_vict_partant=gains_vict_partant==null?null:gains_vict_partant.replace(".", "").replace(",", "，");
//								gains_pla_partant=gains_pla_partant==null?null:gains_pla_partant.replace(".", "").replace(",", "，");
//								gains_annee=gains_annee==null?null:gains_annee.replace(".", "").replace(",", "，");
//								gains_annee_prec=(gains_annee_prec==null?null:gains_annee_prec.replace(".", "").replace(",", "."));
//								valeur=valeur==null?null:valeur.replace(".", "").replaceAll(",", ".");
//								
//								String sSql ="";
								StringBuffer sSql =new StringBuffer(); 
							     sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos+"',");
							     sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
							     sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos+"',");
							     sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document+"',");
							     sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
							     sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
							     sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion+"',");
							     sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo+"',");
							     sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion+"',");
							     sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
							     sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu+"',");
							     sSql=sSql.append(id_nav_partant==null?"NULL,":"N'"+id_nav_partant+"',");
							     sSql=sSql.append(num_partant==null?"NULL,":"N'"+num_partant+"',");
							     sSql=sSql.append(nom_cheval==null?"NULL,":"N'"+nom_cheval+"',");
							     sSql=sSql.append(gains_carr_partant==null?"NULL,":"N'"+gains_carr_partant.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(musique_partant==null?"NULL,":"N'"+musique_partant+"',");
							     sSql=sSql.append(gains_vict_partant==null?"NULL,":"N'"+gains_vict_partant.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(gains_pla_partant==null?"NULL,":"N'"+gains_pla_partant.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(nb_courses_partant==null?"NULL,":"N'"+nb_courses_partant+"',");
							     sSql=sSql.append(nb_vict_partant==null?"NULL,":"N'"+nb_vict_partant+"',");
							     sSql=sSql.append(nb_places_partant==null?"NULL,":"N'"+nb_places_partant+"',");
							     sSql=sSql.append(inedit_partant==null?"NULL,":"N'"+inedit_partant+"',");
							     sSql=sSql.append(record_partant==null?"NULL,":"N'"+record_partant+"',");
							     sSql=sSql.append(discipline_record_partant==null?"NULL,":"N'"+discipline_record_partant+"',");
							     sSql=sSql.append(intention_deferrer==null?"NULL,":"N'"+intention_deferrer+"',");
							     sSql=sSql.append(valeur==null?"NULL,":"N'"+valeur.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(gains_annee==null?"NULL,":"N'"+gains_annee.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(gains_annee_prec==null?"NULL,":"N'"+gains_annee_prec.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(nb_courses_anne==null?"NULL,":"N'"+nb_courses_anne+"',");
							     sSql=sSql.append(nb_premier_annee==null?"NULL,":"N'"+nb_premier_annee+"',");
							     sSql=sSql.append(nb_deuxieme_anne==null?"NULL,":"N'"+nb_deuxieme_anne+"',");
							     sSql=sSql.append(nb_troisieme_annee==null?"NULL,":"N'"+nb_troisieme_annee+"',");
							     sSql=sSql.append(nb_courses_annee_prec==null?"NULL,":"N'"+nb_courses_annee_prec+"',");
							     sSql=sSql.append(nb_premier_annee_prec==null?"NULL,":"N'"+nb_premier_annee_prec+"',");
							     sSql=sSql.append(nb_deuxieme_annee_prec==null?"NULL,":"N'"+nb_deuxieme_annee_prec+"',");
							     sSql=sSql.append(nb_troisieme_annee_prec==null?"NULL,":"N'"+nb_troisieme_annee_prec+"',");
							     sSql=sSql.append(nb_courses_total==null?"NULL,":"N'"+nb_courses_total+"',");
							     sSql=sSql.append(nb_premier_total==null?"NULL,":"N'"+nb_premier_total+"',");
							     sSql=sSql.append(nb_deuxieme_total==null?"NULL,":"N'"+nb_deuxieme_total+"',");
							     sSql=sSql.append(nb_troisieme_total==null?"NULL,":"N'"+nb_troisieme_total+"',");
							     sSql=sSql.append(temps_record==null?"NULL,":"N'"+temps_record+"',");
							     sSql=sSql.append(distance_record==null?"NULL,":"N'"+distance_record+"',");
							     sSql=sSql.append(discipline_record==null?"NULL,":"N'"+discipline_record+"',");
							     sSql=sSql.append(type_depart_record==null?"NULL,":"N'"+type_depart_record+"',");
							     sSql=sSql.append(hippodrome_record==null?"NULL,":"N'"+hippodrome_record+"',");
									
									sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
									sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
							     logger.info("pr_PmuInfoCentreKafka_XML_RunnersPrizeMoney_InsertData  " + sSql);
							     oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_RunnersPrizeMoney_InsertData", sSql.toString());
							     sSql=null;
							}
							courses_records=null;
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
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\08_PART_CARRIERE_GAINS";
		new ParseRunnersPrizeMoney08Bll().run(filePath);
	}
}
