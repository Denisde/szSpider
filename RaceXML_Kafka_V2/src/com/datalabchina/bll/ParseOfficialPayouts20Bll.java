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


public class ParseOfficialPayouts20Bll {
	private  static Logger logger = Logger.getLogger(ParseRaceInformation04Bll.class.getName());
	private  static CommonDB oCommonDB =new CommonDB();
	private  static CommonMethod oCommonMethod = new CommonMethod();	


	public static void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\20_RAPP_DEF_RAP/77729493.xml";
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
				
				
				String date_jour_reunion = oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>");
//				System.out.println(date_jour_reunion );
				
				String type_document = oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>");
//				System.out.println(type_document );
				
				Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(oneJours, "<reunion>(.*?)</reunion>");
				while(reunionMatcher.find()){
					String oneReunion = reunionMatcher.group(1);
					String  code_hippo= oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String  num_externe_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					String  id_nav_reunion= oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					String  type_reunion= oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>");
					String  date_reunion= oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
				
					String  num_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					
					Matcher courses = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courses.find()){
						String course = courses.group(1);
						
						String libcourt_prix_course = oCommonMethod.getValueByPatter(course, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
						String id_nav_course = oCommonMethod.getValueByPatter(course, "<id_nav_course>(.*?)</id_nav_course>");
						String num_course_pmu = oCommonMethod.getValueByPatter(course, "<num_course_pmu>(.*?)</num_course_pmu>");
						String ind_quadrio = oCommonMethod.getValueByPatter(course, "<ind_quadrio>(.*?)</ind_quadrio>");
						
						Matcher paris_course = oCommonMethod.getMatcherStrGroup(course, "<pari_course>(.*?)</pari_course>");
						while(paris_course.find()){
							String pari_course = paris_course.group(1);
							String code_pari_generique = oCommonMethod.getValueByPatter(pari_course, "<code_pari_generique>(.*?)</code_pari_generique>");
							String numero_gagnant = oCommonMethod.getValueByPatter(pari_course, "<numero_gagnant>(.*?)</numero_gagnant>");
							String mise_base = oCommonMethod.getValueByPatter(pari_course, "<mise_base>(.*?)</mise_base>");
							String audience_pari_course = oCommonMethod.getValueByPatter(pari_course, "<audience_pari_course>(.*?)</audience_pari_course>");
							String tirelire = oCommonMethod.getValueByPatter(pari_course, "<tirelire>(.*?)</tirelire>");
							String date_prochaine_tirelire = oCommonMethod.getValueByPatter(pari_course, "<date_prochaine_tirelire>(.*?)</date_prochaine_tirelire>");
							String montant_prochaine_tirelire = oCommonMethod.getValueByPatter(pari_course, "<montant_prochaine_tirelire>(.*?)</montant_prochaine_tirelire>");
							
							
							Matcher combinaisons = oCommonMethod.getMatcherStrGroup(course, "<combinaison>(.*?)</combinaison>");
							while(combinaisons.find()){
								String combinaison = combinaisons.group(1);
								String gagnant = oCommonMethod.getValueByPatter(combinaison, "<gagnant>(.*?)</gagnant>");
								String gagnant_mb = oCommonMethod.getValueByPatter(combinaison, "<gagnant_mb>(.*?)</gagnant_mb>");
								String place = oCommonMethod.getValueByPatter(combinaison, "<place>(.*?)</place>");
								String place_mb = oCommonMethod.getValueByPatter(combinaison, "<place_mb>(.*?)</place_mb>");
								String type_reserve_rap_def = oCommonMethod.getValueByPatter(combinaison, "<type_reserve_rap_def>(.*?)</type_reserve_rap_def>");
								String combinaison_rap_def = oCommonMethod.getValueByPatter(combinaison, "<combinaison_rap_def>(.*?)</combinaison_rap_def>");
								String sum_mises_gagn = oCommonMethod.getValueByPatter(combinaison, "<sum_mises_gagn>(.*?)</sum_mises_gagn>");
								String sum_mises_place = oCommonMethod.getValueByPatter(combinaison, "<sum_mises_place>(.*?)</sum_mises_place>");
								String sum_mises_gagn_type_res_rap_def = oCommonMethod.getValueByPatter(combinaison, "<sum_mises_gagn_type_res_rap_def>(.*?)</sum_mises_gagn_type_res_rap_def>");
								String sum_mises_place_type_res_rap_def = oCommonMethod.getValueByPatter(combinaison, "<sum_mises_place_type_res_rap_def>(.*?)</sum_mises_place_type_res_rap_def>");
								
//								audience_pari_course=audience_pari_course==null?null:audience_pari_course.replace(".", "").replace(",", ".");
//								gagnant=gagnant==null?null:gagnant.replace(".", "").replace(",", ".");
//								gagnant_mb=gagnant_mb==null?null:gagnant_mb.replace(".", "").replace(",", ".");
//								place=place==null?null:place.replace(".", "").replace(",", ".");
//								place_mb=place_mb==null?null:place_mb.replace(".", "").replace(",", ".");
//								sum_mises_gagn=sum_mises_gagn==null?null:sum_mises_gagn.replace(".", "").replace(",", ".");
//								sum_mises_place=sum_mises_place==null?null:sum_mises_place.replace(".", "").replace(",", ".");
//								sum_mises_gagn_type_res_rap_def=sum_mises_gagn_type_res_rap_def==null?null:sum_mises_gagn_type_res_rap_def.replace(".", "").replace(",", ".");
//								sum_mises_place_type_res_rap_def=sum_mises_place_type_res_rap_def==null?null:sum_mises_place_type_res_rap_def.replace(".", "").replace(",", ".");
//								String sSql ="";
								StringBuffer sSql =new StringBuffer(); 
							     sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos.replaceAll("'", "''")+"',");
							     sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos.replaceAll("'", "''")+"',");
							     sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
							     sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
							     sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document.replaceAll("'", "''")+"',");
							     sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo.replaceAll("'", "''")+"',");
							     sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion.replaceAll("'", "''")+"',");
							     sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion.replaceAll("'", "''")+"',");
							     sSql=sSql.append(type_reunion==null?"NULL,":"N'"+type_reunion.replaceAll("'", "''")+"',");
							     sSql=sSql.append(date_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_reunion)+"',");
							     sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion.replaceAll("'", "''")+"',");
							     sSql=sSql.append(libcourt_prix_course==null?"NULL,":"N'"+libcourt_prix_course.replaceAll("'", "''")+"',");
							     sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course.replaceAll("'", "''")+"',");
							     sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu.replaceAll("'", "''")+"',");
							     sSql=sSql.append(ind_quadrio==null?"NULL,":"N'"+ind_quadrio.replaceAll("'", "''")+"',");
							     sSql=sSql.append(code_pari_generique==null?"NULL,":"N'"+code_pari_generique.replaceAll("'", "''")+"',");
							     sSql=sSql.append(numero_gagnant==null?"NULL,":"N'"+numero_gagnant.replaceAll("'", "''")+"',");
							     sSql=sSql.append(mise_base==null?"NULL,":"N'"+mise_base.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(audience_pari_course==null?"NULL,":"N'"+audience_pari_course.replaceAll("'", "''")+"',");
							     sSql=sSql.append(tirelire==null?"NULL,":"N'"+tirelire.replaceAll("'", "''")+"',");
							     sSql=sSql.append(date_prochaine_tirelire==null?"NULL,":"N'"+date_prochaine_tirelire.replaceAll("'", "''")+"',");
							     sSql=sSql.append(montant_prochaine_tirelire==null?"NULL,":"N'"+montant_prochaine_tirelire.replaceAll("'", "''")+"',");
							     sSql=sSql.append(gagnant==null?"NULL,":"N'"+gagnant.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(gagnant_mb==null?"NULL,":"N'"+gagnant_mb.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(place==null?"NULL,":"N'"+place.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(place_mb==null?"NULL,":"N'"+place_mb.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(type_reserve_rap_def==null?"NULL,":"N'"+type_reserve_rap_def.replaceAll("'", "''")+"',");
							     sSql=sSql.append(combinaison_rap_def==null?"NULL,":"N'"+combinaison_rap_def.replaceAll("'", "''")+"',");
							     sSql=sSql.append(sum_mises_gagn==null?"NULL,":"N'"+sum_mises_gagn.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(sum_mises_place==null?"NULL,":"N'"+sum_mises_place.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(sum_mises_gagn_type_res_rap_def==null?"NULL,":"N'"+sum_mises_gagn_type_res_rap_def.replace(".", "").replace(",", ".")+"',");
							     sSql=sSql.append(sum_mises_place_type_res_rap_def==null?"NULL,":"N'"+sum_mises_place_type_res_rap_def.replace(".", "").replace(",", ".")+"',");
									
								sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
								sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
							     logger.info("pr_PmuInfoCentreKafka_XML_OfficialPayouts_InsertData  " + sSql);	
							     oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_OfficialPayouts_InsertData", sSql.toString());
							     sSql=null;
							}
							combinaisons=null;
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
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\20_RAPP_DEF_RAP";
		new ParseOfficialPayouts20Bll().run(filePath);
	}
	
}
