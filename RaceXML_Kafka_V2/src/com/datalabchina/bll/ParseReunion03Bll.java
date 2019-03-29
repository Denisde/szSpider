package com.datalabchina.bll;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.datalabchina.controler.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;


public class ParseReunion03Bll {
	private  static Logger logger = Logger.getLogger(ParseRaceInformation04Bll.class.getName());
	private  static CommonDB oCommonDB =new CommonDB();
	private  static CommonMethod oCommonMethod = new CommonMethod();	

	public static void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\03_REUNION\\77727935.xml";
		String  body = FileDispose.readFile(fileName);
		/*code_statut_infos,libelle_statut_infos,type_document,date_herue_generation,date_jour_reunion,id_nav_reunion,code_hippo,type_reunion
		 * specicialite_reunion,categorie_reunion,lib_hippo_reunion,num_reunion,num_externe,num_externe_reunion,lib_reunion,audience_gpe_reunion
		 * progvalide_reunion,heure_reunion,nbcourse_reunion,pays_site_reunion,devise_reunion,org_reunion,ind_evenement,gnt_reunion
		 * paris_exception,url_hippodrome,meteo,ind_reu_csi,ExtractTime
		 * */
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
				
					String  code_hippo= oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String  type_reunion= oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>");
					String  date_reunion= oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
					String  specialite_reunion= oCommonMethod.getValueByPatter(oneReunion, "<specialite_reunion translate=\"yes\"\\s?>(.*?)</specialite_reunion>");
					String  categorie_reunion= oCommonMethod.getValueByPatter(oneReunion, "<categorie_reunion>(.*?)</categorie_reunion>");
					String  lib_hippo_reunion= oCommonMethod.getValueByPatter(oneReunion, "<lib_hippo_reunion>(.*?)</lib_hippo_reunion>");
					String  num_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					String num_externe_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					String  lib_reunion= oCommonMethod.getValueByPatter(oneReunion, "<lib_reunion>(.*?)</lib_reunion>");
					String  audience_gpe_reunion= oCommonMethod.getValueByPatter(oneReunion, "<audience_gpe_reunion>(.*?)</audience_gpe_reunion>");
					String  progvalide_reunion= oCommonMethod.getValueByPatter(oneReunion, "<progvalide_reunion>(.*?)</progvalide_reunion>");
					String  ind_reu_csi= oCommonMethod.getValueByPatter(oneReunion, "<ind_reu_csi>(.*?)</ind_reu_csi>");
					String  heure_reunion= oCommonMethod.getValueByPatter(oneReunion, "<heure_reunion>(.*?)</heure_reunion>");
					String  nbcourse_reunion= oCommonMethod.getValueByPatter(oneReunion, "<nbcourse_reunion>(.*?)</nbcourse_reunion>");
					String  nbcourse_nploc= oCommonMethod.getValueByPatter(oneReunion, "<nbcourse_nploc>(.*?)</nbcourse_nploc>");
					String  pays_site_reunion= oCommonMethod.getValueByPatter(oneReunion, "<pays_site_reunion>(.*?)</pays_site_reunion>");
					String  devise_reunion= oCommonMethod.getValueByPatter(oneReunion, "<devise_reunion>(.*?)</devise_reunion>");
					String  org_reunion= oCommonMethod.getValueByPatter(oneReunion, "<org_reunion>(.*?)</org_reunion>");
				
					String gnt_reunion = oCommonMethod.getValueByPatter(oneReunion, "<gnt_reunion>(.*?)</gnt_reunion>");
					String  paris_exception= oCommonMethod.getValueByPatter(oneReunion, "<paris_exception>(.*?)</paris_exception>");
					String  url_hippodrome= oCommonMethod.getValueByPatter(oneReunion, "<url_hippodrome>(.*?)</url_hippodrome>");
					String  ind_evenement= oCommonMethod.getValueByPatter(oneReunion, "<ind_evenement>(.*?)</ind_evenement>");
					String  meteo_carte= oCommonMethod.getValueByPatter(oneReunion, "<meteo_carte>(.*?)</meteo_carte>");
					String  meteo_texte= oCommonMethod.getValueByPatter(oneReunion, "<meteo_texte>(.*?)</meteo_texte>");
					
					String id_nav_reunion = oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					
					Matcher courses = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courses.find()){
						String course = courses.group(1);
						String code_evenement = oCommonMethod.getValueByPatter(course, "<code_evenement>(.*?)</code_evenement>");
						
						String heure_depart_course = oCommonMethod.getValueByPatter(course, "<heure_depart_course>(.*?)</heure_depart_course>");
						String num_course_pmu = oCommonMethod.getValueByPatter(course, "<num_course_pmu>(.*?)</num_course_pmu>");
						
						String libcourt_prix_course = oCommonMethod.getValueByPatter(course, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
						
						String nbdeclare_course = oCommonMethod.getValueByPatter(course, "<nbdeclare_course>(.*?)</nbdeclare_course>");
						
						String id_nav_course = oCommonMethod.getValueByPatter(course, "<id_nav_course>(.*?)</id_nav_course>");
						
						String discipline = oCommonMethod.getValueByPatter(course, "<discipline translate=\"yes\"\\s?>(.*?)</discipline>");
						
						String dist_course = oCommonMethod.getValueByPatter(course, "<dist_course>(.*?)</dist_course>");
						
						String statut_course  = oCommonMethod.getValueByPatter(course, "<statut_course translate=\"yes\"\\s?>(.*?)</statut_course>");
						
						String ind_pmu_course = oCommonMethod.getValueByPatter(course, "<ind_pmu_course>(.*?)</ind_pmu_course>");
						
						String ind_spot_quinte = oCommonMethod.getValueByPatter(course, "<ind_spot_quinte>(.*?)</ind_spot_quinte>");
						
						String ind_spot_2sur4 = oCommonMethod.getValueByPatter(course, "<ind_spot_2sur4>(.*?)</ind_spot_2sur4>");
						
						String ind_spot_multi = oCommonMethod.getValueByPatter(course, "<ind_spot_multi>(.*?)</ind_spot_multi>");
						
						String code_pays = oCommonMethod.getValueByPatter(course, "<code_pays>(.*?)</code_pays>");
						
						String ind_quadrio = oCommonMethod.getValueByPatter(course, "<ind_quadrio>(.*?)</ind_quadrio>");
						
						String nom_pays = oCommonMethod.getValueByPatter(course, "<nom_pays>(.*?)</nom_pays>");
						
						String lib_hippo_reel = oCommonMethod.getValueByPatter(course, "<lib_hippo_reel>(.*?)</lib_hippo_reel>");
						
						String enquete_reclamation_statut = oCommonMethod.getValueByPatter(course, "<enquete_reclamation_statut>(.*?)</enquete_reclamation_statut>");
						
						String enquete_reclamation_libelle  = oCommonMethod.getValueByPatter(course, "<enquete_reclamation_libelle translate=\"yes\"\\s?>(.*?)</enquete_reclamation_libelle>");
						
						audience_gpe_reunion=audience_gpe_reunion==null?null:audience_gpe_reunion.replace(",", "，");
//						String sSql ="";
						StringBuffer sSql =new StringBuffer(); 
						
					     sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos.replaceAll("'", "''")+"',");
					     sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos.replaceAll("'", "''")+"',");
					     sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
					     sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document.replaceAll("'", "''")+"',");
					     sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
					     sSql=sSql.append("NULL,");//reunions
					     sSql=sSql.append("NULL,");//reunion
					     sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo.replaceAll("'", "''")+"',");
					     sSql=sSql.append(type_reunion==null?"NULL,":"N'"+type_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(date_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_reunion)+"',");
					     sSql=sSql.append(specialite_reunion==null?"NULL,":"N'"+specialite_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(categorie_reunion==null?"NULL,":"N'"+categorie_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(lib_hippo_reunion==null?"NULL,":"N'"+lib_hippo_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(lib_reunion==null?"NULL,":"N'"+lib_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(audience_gpe_reunion==null?"NULL,":"N'"+audience_gpe_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(progvalide_reunion==null?"NULL,":"N'"+progvalide_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_reu_csi==null?"NULL,":"N'"+ind_reu_csi.replaceAll("'", "''")+"',");
					     
					     sSql=sSql.append(heure_reunion==null?"NULL,":"N'"+heure_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nbcourse_reunion==null?"NULL,":"N'"+nbcourse_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nbcourse_nploc==null?"NULL,":"N'"+nbcourse_nploc.replaceAll("'", "''")+"',");
					     sSql=sSql.append(pays_site_reunion==null?"NULL,":"N'"+pays_site_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(devise_reunion==null?"NULL,":"N'"+devise_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(org_reunion==null?"NULL,":"N'"+org_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(gnt_reunion==null?"NULL,":"N'"+gnt_reunion.replaceAll("'", "''")+"',");
					     
					     sSql=sSql.append(paris_exception==null?"NULL,":"N'"+paris_exception.replaceAll("'", "''")+"',");
					     sSql=sSql.append(url_hippodrome==null?"NULL,":"N'"+url_hippodrome.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_evenement==null?"NULL,":"N'"+ind_evenement.replaceAll("'", "''")+"',");
					     sSql=sSql.append(meteo_carte==null?"NULL,":"N'"+meteo_carte.replaceAll("'", "''")+"',");
					     sSql=sSql.append(meteo_texte==null?"NULL,":"N'"+meteo_texte.replaceAll("'", "''")+"',");
					     sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion.replaceAll("'", "''")+"',");
					     sSql=sSql.append(code_evenement==null?"NULL,":"N'"+code_evenement.replaceAll("'", "''")+"',");
					     sSql=sSql.append(heure_depart_course==null?"NULL,":"N'"+heure_depart_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu.replaceAll("'", "''")+"',");
					     sSql=sSql.append(libcourt_prix_course==null?"NULL,":"N'"+libcourt_prix_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nbdeclare_course==null?"NULL,":"N'"+nbdeclare_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(discipline==null?"NULL,":"N'"+discipline.replaceAll("'", "''")+"',");
					     sSql=sSql.append(dist_course==null?"NULL,":"N'"+dist_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(statut_course==null?"NULL,":"N'"+statut_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_pmu_course==null?"NULL,":"N'"+ind_pmu_course.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_spot_quinte==null?"NULL,":"N'"+ind_spot_quinte.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_spot_2sur4==null?"NULL,":"N'"+ind_spot_2sur4.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_spot_multi==null?"NULL,":"N'"+ind_spot_multi.replaceAll("'", "''")+"',");
					     sSql=sSql.append(code_pays==null?"NULL,":"N'"+code_pays.replaceAll("'", "''")+"',");
					     sSql=sSql.append(ind_quadrio==null?"NULL,":"N'"+ind_quadrio.replaceAll("'", "''")+"',");
					     sSql=sSql.append(nom_pays==null?"NULL,":"N'"+nom_pays.replaceAll("'", "''")+"',");
					     sSql=sSql.append(lib_hippo_reel==null?"NULL,":"N'"+lib_hippo_reel.replaceAll("'", "''")+"',");
					     sSql=sSql.append(enquete_reclamation_statut==null?"NULL,":"N'"+enquete_reclamation_statut.replaceAll("'", "''")+"',");
					     sSql=sSql.append(enquete_reclamation_libelle==null?"NULL,":"N'"+enquete_reclamation_libelle.replaceAll("'", "''")+"',");
							
							sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
							sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
					     logger.info("pr_PmuInfoCentreKafka_XML_Reunion_InsertData  " + sSql);
					     oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_Reunion_InsertData", sSql.toString());
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
	
}
