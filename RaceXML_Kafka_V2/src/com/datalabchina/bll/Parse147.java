package com.datalabchina.bll;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.controler.Controller;
import com.common.FileDispose;

public class Parse147 {
	private static Logger logger = Logger.getLogger(Parse147.class.getName());
	private static CommonDB oCommonDB =new CommonDB();
	private static CommonMethod oCommonMethod = new CommonMethod();	
	// <property name="FRDB">drivers=net.sourceforge.jtds.jdbc.Driver;url=jdbc:jtds:sqlserver://192.168.28.126:1433/FRDB;instance=inst12;user=spider;password=83862909</property>
//	private static String connectionString ="jdbc:sqlserver://192.168.120.216:1433;databaseName=FRDB;user=spider;password=83862909";
//	private static String connectionString = Controller.connectionString;
//	static boolean bPerMeetingCommit = false;
//	static CachedRowSet HorsecachedRS = null;
	public static void parseFile(String fileName)
	{
		String  body = FileDispose.readFile(fileName);
		if(body==null||body.length()<1)return;
		logger.info("Parse FileName ************************" +fileName+"***************************************");
		try {

//			 HorsecachedRS = new CachedRowSetImpl();
//	         HorsecachedRS.setUrl(connectionString);

//	         HorsecachedRS.setCommand("select * from  where 1=2");
//	         HorsecachedRS.execute();        
			
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
					String num_reunion = oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					String code_hippo = oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					
					Matcher courseMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courseMatcher.find())
					{
						String onecourse = courseMatcher.group(1);
						String id_nav_course = oCommonMethod.getValueByPatter(onecourse, "<id_nav_course>(.*?)</id_nav_course>");
						String num_course_pmu = oCommonMethod.getValueByPatter(onecourse, "<num_course_pmu>(.*?)</num_course_pmu>");
						String ind_quadrio = oCommonMethod.getValueByPatter(onecourse, "<ind_quadrio>(.*?)</ind_quadrio>");
						System.out.println(ind_quadrio);
						
						Matcher combinaisons_les_plus_jouees = oCommonMethod.getMatcherStrGroup(onecourse, "<combinaisons_les_plus_jouees>(.*?)</combinaisons_les_plus_jouees>");
						while(combinaisons_les_plus_jouees.find()){
							String onecombinaisons_les_plus_jouees = combinaisons_les_plus_jouees.group(1);
							Matcher type_combinaison = oCommonMethod.getMatcherStrGroup(onecombinaisons_les_plus_jouees, "<type_combinaison>(.*?)</type_combinaison>");
							while(type_combinaison.find()){
								String onetype_combinaison = type_combinaison.group(1);
								String code_pari = oCommonMethod.getValueByPatter(onetype_combinaison, "<code_pari>(.*?)</code_pari>");
								String audience_pari_course = oCommonMethod.getValueByPatter(onetype_combinaison, "<audience_pari_course>(.*?)</audience_pari_course>");
								String montant_enjeu_total = oCommonMethod.getValueByPatter(onetype_combinaison, "<montant_enjeu_total>(.*?)</montant_enjeu_total>");
								String heure_recolte = oCommonMethod.getValueByPatter(onetype_combinaison, "<heure_recolte>(.*?)</heure_recolte>");
								
								Matcher combinaisons = oCommonMethod.getMatcherStrGroup(onetype_combinaison, "<combinaisons>(.*?)</combinaisons>");
								while(combinaisons.find()){
									String onecombinaisons = combinaisons.group(1);
									Matcher combinaison_les_plus_joues = oCommonMethod.getMatcherStrGroup(onecombinaisons, "<combinaison_les_plus_joues>(.*?)</combinaison_les_plus_joues>");
									while(combinaison_les_plus_joues.find()){
										String onecombinaison_les_plus_joues = combinaison_les_plus_joues.group(1);
//										<comb_plus_joues>03-05-07</comb_plus_joues>
//										<ordre_comb_plus_jouees>1</ordre_comb_plus_jouees>
//										<pari_comb_plus_jouees>TR</pari_comb_plus_jouees>
//										<montant_enjeu_comb>39,95</montant_enjeu_comb>
										String comb_plus_joues = oCommonMethod.getValueByPatter(onecombinaison_les_plus_joues, "<comb_plus_joues>(.*?)</comb_plus_joues>");
										String ordre_comb_plus_jouees = oCommonMethod.getValueByPatter(onecombinaison_les_plus_joues, "<ordre_comb_plus_jouees>(.*?)</ordre_comb_plus_jouees>");
										String pari_comb_plus_jouees = oCommonMethod.getValueByPatter(onecombinaison_les_plus_joues, "<pari_comb_plus_jouees>(.*?)</pari_comb_plus_jouees>");
										String montant_enjeu_comb = oCommonMethod.getValueByPatter(onecombinaison_les_plus_joues, "<montant_enjeu_comb>(.*?)</montant_enjeu_comb>");
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
										sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
										sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu+"',");
										sSql=sSql.append(ind_quadrio==null?"NULL,":"N'"+ind_quadrio+"',");
										sSql=sSql.append(code_pari==null?"NULL,":"N'"+code_pari+"',");
										sSql=sSql.append(audience_pari_course==null?"NULL,":"N'"+audience_pari_course+"',");
										sSql=sSql.append(montant_enjeu_total==null?"NULL,":"N'"+montant_enjeu_total.replaceAll("\\.", "").replaceAll(",", "\\.")+"',");
										sSql=sSql.append(heure_recolte==null?"NULL,":"N'"+heure_recolte+"',");
										sSql=sSql.append(comb_plus_joues==null?"NULL,":"N'"+comb_plus_joues+"',");
										sSql=sSql.append(ordre_comb_plus_jouees==null?"NULL,":"N'"+ordre_comb_plus_jouees+"',");
										sSql=sSql.append(pari_comb_plus_jouees==null?"NULL,":"N'"+pari_comb_plus_jouees+"',");
										sSql=sSql.append(montant_enjeu_comb==null?"NULL,":"N'"+montant_enjeu_comb.replaceAll("\\.", "").replaceAll(",", "\\.")+"',");
										sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
										sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
																	
										logger.info("pr_PmuInfoCentreKafka_XML_PlayedHorses_Trio_InsertData  " + sSql);
										oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_PlayedHorses_Trio_InsertData", sSql.toString());
										sSql=null;
									}
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
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\147";
		new Parse147().run(filePath);
	}
}
