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



public class ParseRj02 {
	private static Logger logger = Logger.getLogger(ParseRj02.class.getName());
	private static CommonDB oCommonDB =new CommonDB();
	private static CommonMethod oCommonMethod = new CommonMethod();	
	
	public static void parseFile(String fileName)
	{
//		fileName ="D:\\Denis\\PMU XPRO APP\\XML\\02_REUNION_JOUR\\77727933.xml";
		/*code_statut_infos,libelle_statut_infos,type_document,date_herue_generation,date_jour_reunion,id_nav_reunion,code_hippo,type_reunion
		 * specicialite_reunion,categorie_reunion,lib_hippo_reunion,num_reunion,num_externe,num_externe_reunion,lib_reunion,audience_gpe_reunion
		 * progvalide_reunion,heure_reunion,nbcourse_reunion,pays_site_reunion,devise_reunion,org_reunion,ind_evenement,gnt_reunion
		 * paris_exception,url_hippodrome,meteo,ind_reu_csi,ExtractTime
		 * */
		String  body = FileDispose.readFile(fileName);
		if(body==null||body.length()<1)return;
		logger.info("Parse FileName ************************" +fileName+"***************************************");
		try {
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find()){
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
//				System.out.println(code_statut_infos );
				String libelle_statut_infos =  oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos>(.*?)</libelle_statut_infos>");
//				System.out.println(libelle_statut_infos );
				String type_document = oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>");
//				System.out.println(type_document );
				String date_herue_generation =  oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
				if(date_herue_generation!=null&&date_herue_generation.length()>1){
					//N'15-08-2016 12:21:56',N'17-08-2016'
					date_herue_generation = date_herue_generation.split(" ")[0].split("-")[2]+date_herue_generation.split(" ")[0].split("-")[1]+date_herue_generation.split(" ")[0].split("-")[0]+" "+date_herue_generation.split(" ")[1];
				}
//				System.out.println(date_herue_generation );
				String date_jour_reunion =  oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>");
				if(date_jour_reunion!=null&&date_jour_reunion.length()>1){
					//N'15-08-2016 12:21:56',N'17-08-2016'
					date_jour_reunion = date_jour_reunion.split("-")[2]+date_jour_reunion.split("-")[1]+date_jour_reunion.split("-")[0];
				}
//				System.out.println(date_jour_reunion );
				Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(body, "<reunion>(.*?)</reunion>");
				while(reunionMatcher.find()){
					String oneReunion = reunionMatcher.group(1);
					String id_nav_reunion =  oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
//					System.out.println(id_nav_reunion);
					String  code_hippo=  oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
//					System.out.println(code_hippo);
					String  type_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>");
//					System.out.println(type_reunion);
					String  date_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
					if(date_reunion!=null&&date_reunion.length()>1){
						date_reunion = date_reunion.split("-")[2]+date_reunion.split("-")[1]+date_reunion.split("-")[0];
					}
//					System.out.println(date_reunion); //17-08-2016
					String  specialite_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<specialite_reunion>(.*?)</specialite_reunion>");
//					System.out.println(specialite_reunion);
					String  categorie_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<categorie_reunion>(.*?)</categorie_reunion>");
//					System.out.println(categorie_reunion);
					String  lib_hippo_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<lib_hippo_reunion>(.*?)</lib_hippo_reunion>");
//					System.out.println(lib_hippo_reunion);
					String  num_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
//					System.out.println(num_reunion);
					String num_externe_reunion =  oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
//					System.out.println(num_externe_reunion);
					String  lib_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<lib_reunion>(.*?)</lib_reunion>");
//					System.out.println(lib_reunion);
					String  audience_gpe_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<audience_gpe_reunion>(.*?)</audience_gpe_reunion>");
//					System.out.println(audience_gpe_reunion);
					String  progvalide_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<progvalide_reunion>(.*?)</progvalide_reunion>");
//					System.out.println(progvalide_reunion);
					String  heure_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<heure_reunion>(.*?)</heure_reunion>");
//					System.out.println(heure_reunion);
					String  nbcourse_reunion= oCommonMethod.getValueByPatter(oneReunion, "<nbcourse_reunion>(.*?)</nbcourse_reunion>");
//					System.out.println(nbcourse_reunion);
					String  pays_site_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<pays_site_reunion>(.*?)</pays_site_reunion>");
//					System.out.println(pays_site_reunion);
					String  devise_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<devise_reunion>(.*?)</devise_reunion>");
//					System.out.println(devise_reunion);
					String  org_reunion=  oCommonMethod.getValueByPatter(oneReunion, "<org_reunion>(.*?)</org_reunion>");
//					System.out.println(org_reunion);
					String  ind_evenement=  oCommonMethod.getValueByPatter(oneReunion, "<ind_evenement>(.*?)</ind_evenement>");
//					System.out.println(ind_evenement);
					String gnt_reunion =  oCommonMethod.getValueByPatter(oneReunion, "<gnt_reunion>(.*?)</gnt_reunion>");
//					System.out.println(gnt_reunion);
					String  paris_exception=  oCommonMethod.getValueByPatter(oneReunion, "<paris_exception>(.*?)</paris_exception>");
//					System.out.println(paris_exception);
					String  url_hippodrome=  oCommonMethod.getValueByPatter(oneReunion, "<url_hippodrome>(.*?)</url_hippodrome>");
//					System.out.println(url_hippodrome);
					String  meteo=  oCommonMethod.getValueByPatter(oneReunion, "<meteo>(.*?)</meteo>");
//					System.out.println(meteo);
					String  ind_reu_csi=  oCommonMethod.getValueByPatter(oneReunion, "<ind_reu_csi>(.*?)</ind_reu_csi>");
//					System.out.println(ind_reu_csi);
//					String sSql ="";
					StringBuffer sSql =new StringBuffer(); 
					sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos+"',");
					sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos+"',");
					sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document+"',");
					sSql=sSql.append(date_herue_generation==null?"NULL,":"N'"+date_herue_generation+"',");
					sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+date_jour_reunion+"',");
					sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
					sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo+"',");
					sSql=sSql.append(type_reunion==null?"NULL,":"N'"+type_reunion+"',");
					sSql=sSql.append(date_reunion==null?"NULL,":"N'"+date_reunion+"',");
					sSql=sSql.append(specialite_reunion==null?"NULL,":"N'"+specialite_reunion+"',");
					sSql=sSql.append(categorie_reunion==null?"NULL,":"N'"+categorie_reunion+"',");
					sSql=sSql.append(lib_hippo_reunion==null?"NULL,":"N'"+lib_hippo_reunion+"',");
					sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion+"',");
					sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion+"',");
					sSql=sSql.append(lib_reunion==null?"NULL,":"N'"+lib_reunion+"',");
					sSql=sSql.append(audience_gpe_reunion==null?"NULL,":"N'"+audience_gpe_reunion+"',");
					sSql=sSql.append(progvalide_reunion==null?"NULL,":"N'"+progvalide_reunion+"',");
					sSql=sSql.append(heure_reunion==null?"NULL,":"N'"+heure_reunion+"',");
					sSql=sSql.append(nbcourse_reunion==null?"NULL,":"N'"+nbcourse_reunion+"',");
					sSql=sSql.append(pays_site_reunion==null?"NULL,":"N'"+pays_site_reunion+"',");
					sSql=sSql.append(devise_reunion==null?"NULL,":"N'"+devise_reunion+"',");
					sSql=sSql.append(org_reunion==null?"NULL,":"N'"+org_reunion+"',");
					sSql=sSql.append(ind_evenement==null?"NULL,":"N'"+ind_evenement+"',");
					sSql=sSql.append(gnt_reunion==null?"NULL,":"N'"+gnt_reunion+"',");
					sSql=sSql.append(paris_exception==null?"NULL,":"N'"+paris_exception+"',");
					sSql=sSql.append(url_hippodrome==null?"NULL,":"N'"+url_hippodrome+"',");
					sSql=sSql.append(meteo==null?"NULL,":"N'"+meteo+"',");
					sSql=sSql.append(ind_reu_csi==null?"NULL,":"N'"+ind_reu_csi+"',");
					
					sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
					sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
					logger.info("pr_PmuInfoCentreKafka_XML_DailyMeeting_InsertData  " + sSql);
					oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_DailyMeeting_InsertData", sSql.toString());
					 sSql=null;
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
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\02_REUNION_JOUR";
		new ParseRj02().run(filePath);
	}
}
