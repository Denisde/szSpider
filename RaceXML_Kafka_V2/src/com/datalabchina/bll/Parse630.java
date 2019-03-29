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

public class Parse630 {
	private static Logger logger = Logger.getLogger(Parse147.class.getName());
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
				while(reunionMatcher.find()){
					String oneReunion =reunionMatcher.group(1); 
					String id_nav_reunion = oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					String num_externe_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					String code_hippo = oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String type_reunion = oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>");
					String date_reunion = oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
					String num_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					
					Matcher courseMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courseMatcher.find())
					{
						String onecourse = courseMatcher.group(1);
						String id_nav_course = oCommonMethod.getValueByPatter(onecourse, "<id_nav_course>(.*?)</id_nav_course>");
						String num_course_pmu = oCommonMethod.getValueByPatter(onecourse, "<num_course_pmu>(.*?)</num_course_pmu>");
						String libcourt_prix_course = oCommonMethod.getValueByPatter(onecourse, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
						
						Matcher citations_epc = oCommonMethod.getMatcherStrGroup(onecourse, "<citations_epc>(.*?)</citations_epc>");
						while(citations_epc.find()){
							String onecitations_epc = citations_epc.group(1);
							String code_pari = oCommonMethod.getValueByPatter(onecitations_epc, "<code_pari>(.*?)</code_pari>");
							String audience_pari_course = oCommonMethod.getValueByPatter(onecitations_epc, "<audience_pari_course>(.*?)</audience_pari_course>");
							String devise = oCommonMethod.getValueByPatter(onecitations_epc, "<devise>(.*?)</devise>");
							String montant_enjeu_total = oCommonMethod.getValueByPatter(onecitations_epc, "<montant_enjeu_total>(.*?)</montant_enjeu_total>");
									
							Matcher partant = oCommonMethod.getMatcherStrGroup(onecourse, "<partant>(.*?)</partant>");
							while(partant.find()){
								String onepartant = partant.group(1);
								String id_nav_partant = oCommonMethod.getValueByPatter(onepartant, "<id_nav_partant>(.*?)</id_nav_partant>");
								String nom_cheval = oCommonMethod.getValueByPatter(onepartant, "<nom_cheval>(.*?)</nom_cheval>");
								String num_partant = oCommonMethod.getValueByPatter(onepartant, "<num_partant>(.*?)</num_partant>");
								String statut_part = oCommonMethod.getValueByPatter(onepartant, "<statut_part>(.*?)</statut_part>");
								
								Matcher citation = oCommonMethod.getMatcherStrGroup(onepartant, "<citation>(.*?)</citation>");
								while(citation.find()){
									String onecitation = citation.group(1);
									String risque = oCommonMethod.getValueByPatter(onecitation, "<risque>(.*?)</risque>");
									String montant_enjeux = oCommonMethod.getValueByPatter(onecitation, "<montant_enjeux>(.*?)</montant_enjeux>");
									String montant_pourcent = oCommonMethod.getValueByPatter(onecitation, "<montant_pourcent>(.*?)</montant_pourcent>");
								
									StringBuffer sSql =new StringBuffer(); 
									sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos+"',");
									sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos+"',");
									sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
									sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document+"',");
									sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
									sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
									sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion+"',");
									sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo+"',");
									sSql=sSql.append(type_reunion==null?"NULL,":"N'"+type_reunion+"',");
									sSql=sSql.append(date_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_reunion)+"',");
									sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion+"',");
									sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
									sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu+"',");
									sSql=sSql.append(libcourt_prix_course==null?"NULL,":"N'"+libcourt_prix_course+"',");
									sSql=sSql.append(code_pari==null?"NULL,":"N'"+code_pari+"',");
									sSql=sSql.append(audience_pari_course==null?"NULL,":"N'"+audience_pari_course+"',");
									sSql=sSql.append(devise==null?"NULL,":"N'"+devise+"',");
									sSql=sSql.append(montant_enjeu_total==null?"NULL,":"N'"+montant_enjeu_total.replaceAll("\\.", "").replaceAll(",", "\\.")+"',");
									sSql=sSql.append(id_nav_partant==null?"NULL,":"N'"+id_nav_partant+"',");
									sSql=sSql.append(nom_cheval==null?"NULL,":"N'"+nom_cheval+"',");
									sSql=sSql.append(num_partant==null?"NULL,":"N'"+num_partant+"',");
									sSql=sSql.append(statut_part==null?"NULL,":"N'"+statut_part+"',");
									sSql=sSql.append(risque==null?"NULL,":"N'"+risque+"',");
									sSql=sSql.append(montant_enjeux==null?"NULL,":"N'"+montant_enjeux.replaceAll("\\.", "").replaceAll(",", "\\.")+"',");
									sSql=sSql.append(montant_pourcent==null?"NULL,":"N'"+montant_pourcent.replaceAll("\\.", "").replaceAll(",", "\\.")+"',");
									
									sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
									sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
																
									logger.info("pr_PmuInfoCentreKafka_XML_CITATIONS_MINI_MULTI_InsertData  " + sSql);
									oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_CITATIONS_MINI_MULTI_InsertData", sSql.toString());
									sSql=null;
									
									}
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
	
	public static String getDate(String str){
		String s = null;
		try {
			if(str.contains(" ")){
				String arr[] = str.split(" ");
				if(arr[0].contains("-")){
					s = arr[0].split("-")[2]+"-"+arr[0].split("-")[1]+"-"+arr[0].split("-")[0]+" "+arr[1]+""+".000";
				} 
			}else{
				s = str.split("-")[2]+"-"+str.split("-")[1]+"-"+str.split("-")[0];
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return s;
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		String filePath ="D:\\Denis\\PMU XPRO APP\\20180801\\630";
		new Parse630().run(filePath);
	}
}

