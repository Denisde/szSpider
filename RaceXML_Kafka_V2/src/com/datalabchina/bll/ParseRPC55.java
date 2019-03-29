package com.datalabchina.bll;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import javax.sql.rowset.CachedRowSet;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.datalabchina.controler.Controller;
//import com.datalabchina.Controller;
//import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import com.sun.rowset.CachedRowSetImpl;



public class ParseRPC55 {
	private static Logger logger = Logger.getLogger(ParseRPC55.class.getName());
//	private static CommonDB oCommonDB =new CommonDB();
	private static CommonMethod oCommonMethod = new CommonMethod();	
//	private static String connectionString ="jdbc:sqlserver://192.168.120.216:1433;databaseName=FRDB;user=spider;password=83862909";
	private static String connectionString = Controller.connectionString;
	static boolean bPerMeetingCommit = false;
	static CachedRowSet HorsecachedRS = null;
	
	public static void parseFile(String fileName)
	{
		String  body = FileDispose.readFile(fileName);
		if(body==null||body.length()<1)return;
		logger.info("Parse FileName ************************" +fileName+"***************************************");
		try {

			 HorsecachedRS = new CachedRowSetImpl();
	         HorsecachedRS.setUrl(connectionString);
	         
	         HorsecachedRS.setCommand("select * from PmuInfoCentreKafka_XML_LiveOdds_Exacta where 1=2");
	         HorsecachedRS.execute();        
			
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find())
			{
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
				String libelle_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<libelle_statut_infos>(.*?)</libelle_statut_infos>");
				String date_heure_generation = oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
				String type_document = oCommonMethod.getValueByPatter(oneJours, "<type_document>(.*?)</type_document>");
				String date_jour_reunion = oCommonMethod.getValueByPatter(oneJours, "<date_jour_reunion>(.*?)</date_jour_reunion>");
				Matcher reunionMatcher = oCommonMethod.getMatcherStrGroup(oneJours, "<reunion>(.*?)</reunion>");
				
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
						String heure_rap_ref = oCommonMethod.getValueByPatter(onecourse, "<heure_rap_ref>(.*?)</heure_rap_ref>");
						String heure_rap_evol = oCommonMethod.getValueByPatter(onecourse, "<heure_rap_evol>(.*?)</heure_rap_evol>");
						
						Matcher rapps_probsMatcher = oCommonMethod.getMatcherStrGroup(oneReunion, "<rapps_probs>(.*?)</rapps_probs>");
						while(rapps_probsMatcher.find()){
							String onerapps_probs = rapps_probsMatcher.group(1);
							String audience_pari_course = oCommonMethod.getValueByPatter(onerapps_probs, "<audience_pari_course>(.*?)</audience_pari_course>");
							Matcher rapps_probs_partMatcher = oCommonMethod.getMatcherStrGroup(onerapps_probs, "<rapps_probs_part>(.*?)</rapps_probs_part>");
							while(rapps_probs_partMatcher.find()){
								 String onerapps_probs_part =rapps_probs_partMatcher.group(1); 
								 String num_part = oCommonMethod.getValueByPatter(onerapps_probs_part, "<num_part>(.*?)</num_part>");
								 String nom_cheval = oCommonMethod.getValueByPatter(onerapps_probs_part, "<nom_cheval>(.*?)</nom_cheval>");
								 String ecurie_part = oCommonMethod.getValueByPatter(onerapps_probs_part, "<ecurie_part>(.*?)</ecurie_part>");
								 Matcher combinaisonMatcher = oCommonMethod.getMatcherStrGroup(onerapps_probs_part, "<combinaison>(.*?)</combinaison>");
									while(combinaisonMatcher.find())
									{
										String oneCombinaison = combinaisonMatcher.group(1);
										String num_part_1 = oCommonMethod.getValueByPatter(oneCombinaison, "<num_part_1>(.*?)</num_part_1>");
										String statut_part_1 = oCommonMethod.getValueByPatter(oneCombinaison, "<statut_part_1>(.*?)</statut_part_1>");
										String num_part_2 = oCommonMethod.getValueByPatter(oneCombinaison, "<num_part_2>(.*?)</num_part_2>");
										String statut_part_2 = oCommonMethod.getValueByPatter(oneCombinaison, "<statut_part_2>(.*?)</statut_part_2>");
										String rapp_ref = oCommonMethod.getValueByPatter(oneCombinaison, "<rapp_ref>(.*?)</rapp_ref>");
										String rapp_evol = oCommonMethod.getValueByPatter(oneCombinaison, "<rapp_evol>(.*?)</rapp_evol>");
										
//										String sSql ="";
//										StringBuffer sSql =new StringBuffer(); 
//										sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos+"',");
//										sSql=sSql.append(libelle_statut_infos==null?"NULL,":"N'"+libelle_statut_infos+"',");
//										sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
//										sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document+"',");
//										sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
//										sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
//										sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion+"',");
//										sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo+"',");
//										sSql=sSql.append(type_reunion==null?"NULL,":"N'"+type_reunion+"',");
//										sSql=sSql.append(date_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_reunion)+"',");
//										sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion+"',");
//										sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
//										sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu+"',");
//										sSql=sSql.append(libcourt_prix_course==null?"NULL,":"N'"+libcourt_prix_course+"',");
//										sSql=sSql.append(heure_rap_ref==null?"NULL,":"N'"+heure_rap_ref+"',");
//										sSql=sSql.append(heure_rap_evol==null?"NULL,":"N'"+heure_rap_evol+"',");
//										sSql=sSql.append(audience_pari_course==null?"NULL,":"N'"+audience_pari_course+"',");
//										sSql=sSql.append(num_part==null?"NULL,":"N'"+num_part+"',");
//										sSql=sSql.append(nom_cheval==null?"NULL,":"N'"+nom_cheval+"',");
//										sSql=sSql.append(ecurie_part==null?"NULL,":"N'"+ecurie_part+"',");
//										sSql=sSql.append(num_part_1==null?"NULL,":"N'"+num_part_1+"',");
//										sSql=sSql.append(statut_part_1==null?"NULL,":"N'"+statut_part_1+"',");
//										sSql=sSql.append(num_part_2==null?"NULL,":"N'"+num_part_2+"',");
//										sSql=sSql.append(statut_part_2==null?"NULL,":"N'"+statut_part_2+"',");
//										sSql=sSql.append(rapp_ref==null?"NULL,":"N'"+rapp_ref+"',");
//										sSql=sSql.append(rapp_evol==null?"NULL,":"N'"+rapp_evol+"',");
//										
//										sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
//										sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
//										logger.info("pr_PmuInfoCentreKafka_XML_LiveOdds_Exacta_InsertData  " + sSql);
//										oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_LiveOdds_Exacta_InsertData", sSql.toString());
//										sSql=null;
										HorsecachedRS.moveToInsertRow();     
										  
										HorsecachedRS.updateString("code_statut_infos", code_statut_infos);
										  HorsecachedRS.updateString("libelle_statut_infos", libelle_statut_infos);
										  
										  if(date_heure_generation==null){
											  HorsecachedRS.updateNull("date_heure_generation");
										  }else{
											  HorsecachedRS.updateTimestamp("date_heure_generation",java.sql.Timestamp.valueOf(getDate(date_heure_generation)));
										  }
										  
										  HorsecachedRS.updateString("type_document", type_document);
										  
										  if(date_jour_reunion==null){
											  HorsecachedRS.updateNull("date_jour_reunion");
										  }else{
											  HorsecachedRS.updateDate("date_jour_reunion",java.sql.Date.valueOf(getDate(date_jour_reunion)));
										  }
//										  RacecachedRS.updateString("id_nav_reunion", id_nav_reunion);
										  if(id_nav_reunion==null){
												 HorsecachedRS.updateNull("id_nav_reunion");
										  }else{
											 HorsecachedRS.updateInt("id_nav_reunion",Integer.parseInt(id_nav_reunion));
										  }	
										  
										  if(num_externe_reunion==null){
												 HorsecachedRS.updateNull("num_externe_reunion");
											 }else{
												 HorsecachedRS.updateInt("num_externe_reunion",Integer.parseInt(num_externe_reunion));
											 }
										  HorsecachedRS.updateString("code_hippo", code_hippo);
										  HorsecachedRS.updateString("type_reunion", type_reunion);
										  
										  if(date_reunion==null){
											  HorsecachedRS.updateNull("date_reunion");
										  }else{
											  HorsecachedRS.updateDate("date_reunion",java.sql.Date.valueOf(getDate(date_reunion)));
										  }
										  
										  if(num_reunion==null){
											  HorsecachedRS.updateNull("num_reunion");
										  }else{
											  HorsecachedRS.updateInt("num_reunion",Integer.parseInt(num_reunion));
										  }
										  
										  if(id_nav_course==null){
											  HorsecachedRS.updateNull("id_nav_course");
										  }else{
											  HorsecachedRS.updateInt("id_nav_course",Integer.parseInt(id_nav_course));
										  }
										  
										  if(num_course_pmu==null){
											  HorsecachedRS.updateNull("num_course_pmu");
										  }else{
											  HorsecachedRS.updateInt("num_course_pmu",Integer.parseInt(num_course_pmu));
										  }
										  HorsecachedRS.updateString("libcourt_prix_course", libcourt_prix_course);
										  HorsecachedRS.updateString("heure_rap_ref", heure_rap_ref);
										  HorsecachedRS.updateString("heure_rap_evol", heure_rap_evol);
										  HorsecachedRS.updateString("audience_pari_course", audience_pari_course);
										  if(num_part==null){
											  HorsecachedRS.updateNull("num_part");
										  }else{
											  HorsecachedRS.updateInt("num_part",Integer.parseInt(num_part));
										  }
										  HorsecachedRS.updateString("nom_cheval", nom_cheval);
										  HorsecachedRS.updateString("ecurie_part", ecurie_part);
										  if(num_part_1==null){
											  HorsecachedRS.updateNull("num_part_1");
										  }else{
											  HorsecachedRS.updateInt("num_part_1",Integer.parseInt(num_part_1));
										  }
										  HorsecachedRS.updateString("statut_part_1", statut_part_1);
										  if(num_part_2==null){
											  HorsecachedRS.updateNull("num_part_2");
										  }else{
											  HorsecachedRS.updateInt("num_part_2",Integer.parseInt(num_part_2));
										  }
										  HorsecachedRS.updateString("statut_part_2", statut_part_2);
										  if(rapp_ref==null){
											  HorsecachedRS.updateNull("rapp_ref");
										  }else{
											  HorsecachedRS.updateInt("rapp_ref",Integer.parseInt(rapp_ref));
										  }
										  if(rapp_evol==null){
											  HorsecachedRS.updateNull("rapp_evol");
										  }else{
											  HorsecachedRS.updateInt("rapp_evol",Integer.parseInt(rapp_evol));
										  }
										  
										  HorsecachedRS.updateDate("ExtractTime", new java.sql.Date(new Date().getTime()));
										  HorsecachedRS.updateString("fileName", fileName);
										  
										  HorsecachedRS.insertRow();
										  HorsecachedRS.moveToCurrentRow();	
										  
										  
								}
							}
						}
					}
				}
			}
			if (bPerMeetingCommit == false) {
				InsertBulk("PmuInfoCentreKafka_XML_LiveOdds_Exacta",HorsecachedRS);							
			}
		}
		catch (Exception e) 
		{
			logger.error("fileName = ****************"+fileName+"*******************************");
			logger.error("",e);
		}
	}
	
	public static void InsertBulk(String tableName, CachedRowSet cachedRS) {
		SQLServerBulkCopy bulkCopy = null;
		try {

			// Note: if you are not using try-with-resources statements (as
			// here),
			// you must remember to call close() on any Connection, Statement,
			// ResultSet, and SQLServerBulkCopy objects that you create.
			// Open a sourceConnection to the AdventureWorks database.
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			// Connection sourceConnection =
			// DriverManager.getConnection(connectionString);
			// sourceConnection =
			// DriverManager.getConnection("jdbc:sqlserver://192.168.120.216:1433;databaseName=FRDB;user=spider;password=83862909");

			bulkCopy = new SQLServerBulkCopy(connectionString);
			SQLServerBulkCopyOptions copyOptions = new SQLServerBulkCopyOptions();
			copyOptions.setBulkCopyTimeout(0);
			bulkCopy.setBulkCopyOptions(copyOptions);
			bulkCopy.setDestinationTableName(tableName);

			StopWatch sw = new StopWatch();
			sw.start();
			try {
				bulkCopy.writeToServer(cachedRS);
			} catch (Exception e) {
				logger.error("", e);
			}
			bulkCopy.close();

			sw.stop();
			logger.info("insert " + cachedRS.size() + " rows, used : "+ sw.getTime() / 1000 + " s");

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			try {
				if (cachedRS != null)
					cachedRS.close();
				if (bulkCopy != null)
					bulkCopy.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
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
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\55_RAPP_PROB_CO_EVOL";
		new ParseRPC55().run(filePath);
	}
}
