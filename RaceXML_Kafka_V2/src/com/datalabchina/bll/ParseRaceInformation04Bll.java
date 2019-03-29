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


public class ParseRaceInformation04Bll {
	private  static Logger logger = Logger.getLogger(ParseRaceInformation04Bll.class.getName());
	private  static CommonDB oCommonDB =new CommonDB();
	private  static CommonMethod oCommonMethod = new CommonMethod();	


	public static void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\04_COURSE\\77727884.xml";
		String  body = FileDispose.readFile(fileName);
		logger.info("Parse FileName ************************" +fileName+"***************************************");
		try {
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find()){
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
				String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos>(.*?)</libelle_statut_infos>");
				String date_heure_generation = oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
				String date_jour_reunion = oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>");
				String type_document = oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>");
				
				Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(oneJours, "<reunion>(.*?)</reunion>");
				while(reunionMatcher.find()){
					String oneReunion = reunionMatcher.group(1);
					String  date_reunion= oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
					String  code_hippo= oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String id_nav_reunion = oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					String  type_reunion= oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>");
					String  lib_reunion= oCommonMethod.getValueByPatter(oneReunion, "<lib_reunion>(.*?)</lib_reunion>");
					String num_externe_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					String  heure_reunion= oCommonMethod.getValueByPatter(oneReunion, "<heure_reunion>(.*?)</heure_reunion>");
					String  num_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					Matcher courses = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courses.find()){
						String course = courses.group(1);
						
						String id_nav_course = oCommonMethod.getValueByPatter(course, "<id_nav_course>(.*?)</id_nav_course>");
						String num_course_pmu = oCommonMethod.getValueByPatter(course, "<num_course_pmu>(.*?)</num_course_pmu>");
						String libcourt_prix_course = oCommonMethod.getValueByPatter(course, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
						String liblong_prix_course = oCommonMethod.getValueByPatter(course, "<liblong_prix_course>(.*?)</liblong_prix_course>");
						String lib_spon_course = oCommonMethod.getValueByPatter(course, "<lib_spon_course>(.*?)</lib_spon_course>");
						String epreuve_course = oCommonMethod.getValueByPatter(course, "<epreuve_course>(.*?)</epreuve_course>");
						String code_pays = oCommonMethod.getValueByPatter(course, "<code_pays>(.*?)</code_pays>");
						String nom_pays = oCommonMethod.getValueByPatter(course, "<nom_pays>(.*?)</nom_pays>");
						String lib_hippo_reel = oCommonMethod.getValueByPatter(course, "<lib_hippo_reel>(.*?)</lib_hippo_reel>");
						String peloton_course = oCommonMethod.getValueByPatter(course, "<peloton_course>(.*?)</peloton_course>");
						String heure_depart_course = oCommonMethod.getValueByPatter(course, "<heure_depart_course>(.*?)</heure_depart_course>");
						String valnom_prix_course = oCommonMethod.getValueByPatter(course, "<valnom_prix_course>(.*?)</valnom_prix_course>");
						String distance_course = oCommonMethod.getValueByPatter(course, "<distance_course>(.*?)</distance_course>");
						String unite_dist_course = oCommonMethod.getValueByPatter(course, "<unite_dist_course>(.*?)</unite_dist_course>");
						String lib_parcours_course = oCommonMethod.getValueByPatter(course, "<lib_parcours_course translate=\"yes\"\\s?>(.*?)</lib_parcours_course>");
						String typ_dep_course  = oCommonMethod.getValueByPatter(course, "<typ_dep_course translate=\"yes\"\\s?>(.*?)</typ_dep_course>");
						
						String corde_course = oCommonMethod.getValueByPatter(course, "<corde_course>(.*?)</corde_course>");
						String lib_corde_course  = oCommonMethod.getValueByPatter(course, "<lib_corde_course translate=\"yes\"\\s?>(.*?)</lib_corde_course>");
						String piste_course = oCommonMethod.getValueByPatter(course, "<piste_course>(.*?)</piste_course>");
						String lib_piste_course = oCommonMethod.getValueByPatter(course, "<lib_piste_course translate=\"yes\"\\s?>(.*?)</lib_piste_course>");
						String num_externe_course = oCommonMethod.getValueByPatter(course, "<num_externe_course>(.*?)</num_externe_course>");
						String discipline_course = oCommonMethod.getValueByPatter(course, "<discipline_course>(.*?)</discipline_course>");
						String categ_course  = oCommonMethod.getValueByPatter(course, "<categ_course translate=\"mandatory\"\\s?>(.*?)</categ_course>");
						String type_categ_course = oCommonMethod.getValueByPatter(course, "<type_categ_course>(.*?)</type_categ_course>");
						String ref_1age_course = oCommonMethod.getValueByPatter(course, "<ref_1age_course>(.*?)</ref_1age_course>");
						String ref_2age_course = oCommonMethod.getValueByPatter(course, "<ref_2age_course>(.*?)</ref_2age_course>");
						String ref_3age_course = oCommonMethod.getValueByPatter(course, "<ref_3age_course>(.*?)</ref_3age_course>");
						
						String type_course = oCommonMethod.getValueByPatter(course, "<type_course>(.*?)</type_course>");
						String gnt_course = oCommonMethod.getValueByPatter(course, "<gnt_course>(.*?)</gnt_course>");
						String debt_oper_course = oCommonMethod.getValueByPatter(course, "<debt_oper_course>(.*?)</debt_oper_course>");
						String ind_course_speciale = oCommonMethod.getValueByPatter(course, "<ind_course_speciale>(.*?)</ind_course_speciale>");
						String ind_gpe_pdv_nat = oCommonMethod.getValueByPatter(course, "<ind_gpe_pdv_nat>(.*?)</ind_gpe_pdv_nat>");
						String ind_gpe_pdv_reg = oCommonMethod.getValueByPatter(course, "<ind_gpe_pdv_reg>(.*?)</ind_gpe_pdv_reg>");
						String race_cond_course = oCommonMethod.getValueByPatter(course, "<race_cond_course>(.*?)</race_cond_course>");
						String age_cond_course = oCommonMethod.getValueByPatter(course, "<age_cond_course>(.*?)</age_cond_course>");
						String sexe_cond_course = oCommonMethod.getValueByPatter(course, "<sexe_cond_course>(.*?)</sexe_cond_course>");
						String monte_cond_course = oCommonMethod.getValueByPatter(course, "<monte_cond_course>(.*?)</monte_cond_course>");
						String conditions_txt_course = oCommonMethod.getValueByPatter(course, "<conditions_txt_course translate=\"yes\"\\s?>(.*?)</conditions_txt_course>");
						String montant_total_allocation = oCommonMethod.getValueByPatter(course, "<montant_total_allocation>(.*?)</montant_total_allocation>");
						String allocation_premier_partant = oCommonMethod.getValueByPatter(course, "<allocation_premier_partant>(.*?)</allocation_premier_partant>");
						String allocation_deuxieme_partant = oCommonMethod.getValueByPatter(course, "<allocation_deuxieme_partant>(.*?)</allocation_deuxieme_partant>");
						String allocation_troisieme_partant = oCommonMethod.getValueByPatter(course, "<allocation_troisieme_partant>(.*?)</allocation_troisieme_partant>");
						String allocation_quatrieme_partant = oCommonMethod.getValueByPatter(course, "<allocation_quatrieme_partant>(.*?)</allocation_quatrieme_partant>");
						String allocation_cinquieme_partant = oCommonMethod.getValueByPatter(course, "<allocation_cinquieme_partant>(.*?)</allocation_cinquieme_partant>");
						String allocation_sixieme_partant = oCommonMethod.getValueByPatter(course, "<allocation_sixieme_partant>(.*?)</allocation_sixieme_partant>");
						String allocation_septieme_partant = oCommonMethod.getValueByPatter(course, "<allocation_septieme_partant>(.*?)</allocation_septieme_partant>");
//						String sSql ="";
						StringBuffer sSql =new StringBuffer(); 
					     sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos.replaceAll("'", "''")+"',");
					     sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos.replaceAll("'", "''")+"',");
					     sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
					     sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
					     sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document.replaceAll("'", "''")+"',");
					     sSql=sSql.append(date_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_reunion)+"',");
					     sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo.replaceAll("'", "''")+"',");
					     sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(type_reunion==null?"NULL,":"N'"+type_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(lib_reunion==null?"NULL,":"N'"+lib_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(heure_reunion==null?"NULL,":"N'"+heure_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu.replaceAll("'", "''")+"',");
					     sSql=sSql.append(libcourt_prix_course==null?"NULL,":"N'"+libcourt_prix_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(liblong_prix_course==null?"NULL,":"N'"+liblong_prix_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(lib_spon_course==null?"NULL,":"N'"+lib_spon_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(epreuve_course==null?"NULL,":"N'"+epreuve_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(code_pays==null?"NULL,":"N'"+code_pays.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nom_pays==null?"NULL,":"N'"+nom_pays.replaceAll("'", "''")+"',");
					     sSql=sSql.append(lib_hippo_reel==null?"NULL,":"N'"+lib_hippo_reel.replaceAll("'", "''")+"',");
					     sSql=sSql.append(peloton_course==null?"NULL,":"N'"+peloton_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(heure_depart_course==null?"NULL,":"N'"+heure_depart_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(valnom_prix_course==null?"NULL,":"N'"+valnom_prix_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(distance_course==null?"NULL,":"N'"+distance_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(unite_dist_course==null?"NULL,":"N'"+unite_dist_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(lib_parcours_course==null?"NULL,":"N'"+lib_parcours_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(typ_dep_course==null?"NULL,":"N'"+typ_dep_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(corde_course==null?"NULL,":"N'"+corde_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(lib_corde_course==null?"NULL,":"N'"+lib_corde_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(piste_course==null?"NULL,":"N'"+piste_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(lib_piste_course==null?"NULL,":"N'"+lib_piste_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_externe_course==null?"NULL,":"N'"+num_externe_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(discipline_course==null?"NULL,":"N'"+discipline_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(categ_course==null?"NULL,":"N'"+categ_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(type_categ_course==null?"NULL,":"N'"+type_categ_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ref_1age_course==null?"NULL,":"N'"+ref_1age_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ref_2age_course==null?"NULL,":"N'"+ref_2age_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ref_3age_course==null?"NULL,":"N'"+ref_3age_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(type_course==null?"NULL,":"N'"+type_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(gnt_course==null?"NULL,":"N'"+gnt_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(debt_oper_course==null?"NULL,":"N'"+debt_oper_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_course_speciale==null?"NULL,":"N'"+ind_course_speciale.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_gpe_pdv_nat==null?"NULL,":"N'"+ind_gpe_pdv_nat.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_gpe_pdv_reg==null?"NULL,":"N'"+ind_gpe_pdv_reg.replaceAll("'", "''")+"',");
					     sSql=sSql.append(race_cond_course==null?"NULL,":"N'"+race_cond_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(age_cond_course==null?"NULL,":"N'"+age_cond_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(sexe_cond_course==null?"NULL,":"N'"+sexe_cond_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(monte_cond_course==null?"NULL,":"N'"+monte_cond_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(conditions_txt_course==null?"NULL,":"N'"+conditions_txt_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(montant_total_allocation==null?"NULL,":"N'"+montant_total_allocation.replaceAll("'", "''")+"',");
					     sSql=sSql.append(allocation_premier_partant==null?"NULL,":"N'"+allocation_premier_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(allocation_deuxieme_partant==null?"NULL,":"N'"+allocation_deuxieme_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(allocation_troisieme_partant==null?"NULL,":"N'"+allocation_troisieme_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(allocation_quatrieme_partant==null?"NULL,":"N'"+allocation_quatrieme_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(allocation_cinquieme_partant==null?"NULL,":"N'"+allocation_cinquieme_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(allocation_sixieme_partant==null?"NULL,":"N'"+allocation_sixieme_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append(allocation_septieme_partant==null?"NULL,":"N'"+allocation_septieme_partant.replaceAll("'", "''")+"',");
					     sSql=sSql.append("N'"+oCommonMethod.getCurrentTime()+"',");
					     sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
					     
					     logger.info("pr_PmuInfoCentreKafka_XML_RaceInformation_InsertData  " + sSql);
					     oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_RaceInformation_InsertData", sSql.toString());
					     sSql=null;
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
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\04_COURSE";
		new ParseRaceInformation04Bll().run(filePath);
	}

}
