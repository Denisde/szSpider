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


public class ParseLiveOdds51Bll {
	private static Logger logger = Logger.getLogger(ParseLiveOdds51Bll.class.getName());
//	private CommonDB oCommonDB =new CommonDB();
	private static CommonMethod oCommonMethod = new CommonMethod();	
//	private static String connectionString ="jdbc:sqlserver://192.168.120.216:1433;databaseName=FRDB;user=spider;password=83862909";
	private static String connectionString = Controller.connectionString;
	
	static boolean bPerMeetingCommit = false;
	static CachedRowSet HorsecachedRS = null;
	public static void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\51_RAPP_PROB_SG_EVOL/77727687.xml";
		String  body = FileDispose.readFile(fileName);
		try {
			
			 HorsecachedRS = new CachedRowSetImpl();
	         HorsecachedRS.setUrl(connectionString);
	         
	         HorsecachedRS.setCommand("select * from PmuInfoCentreKafka_XML_LiveOdds where 1=2");
	         HorsecachedRS.execute();      
			
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
				
					String  id_nav_reunion= oCommonMethod.getValueByPatter(oneReunion, "<id_nav_reunion>(.*?)</id_nav_reunion>");
					String  num_externe_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_externe_reunion>(.*?)</num_externe_reunion>");
					String  code_hippo= oCommonMethod.getValueByPatter(oneReunion, "<code_hippo>(.*?)</code_hippo>");
					String  type_reunion= oCommonMethod.getValueByPatter(oneReunion, "<type_reunion>(.*?)</type_reunion>");
					String  date_reunion= oCommonMethod.getValueByPatter(oneReunion, "<date_reunion>(.*?)</date_reunion>");
					String  num_reunion= oCommonMethod.getValueByPatter(oneReunion, "<num_reunion>(.*?)</num_reunion>");
					
					Matcher courses = oCommonMethod.getMatcherStrGroup(oneReunion, "<course>(.*?)</course>");
					while(courses.find()){
						String course = courses.group(1);
						String id_nav_course = oCommonMethod.getValueByPatter(course, "<id_nav_course>(.*?)</id_nav_course>");
						String num_course_pmu = oCommonMethod.getValueByPatter(course, "<num_course_pmu>(.*?)</num_course_pmu>");
						String libcourt_prix_course = oCommonMethod.getValueByPatter(course, "<libcourt_prix_course>(.*?)</libcourt_prix_course>");
						String ind_quadrio = oCommonMethod.getValueByPatter(course, "<ind_quadrio>(.*?)</ind_quadrio>");
						String heure_rap_ref = oCommonMethod.getValueByPatter(course, "<heure_rap_ref>(.*?)</heure_rap_ref>");
						String heure_rap_evol = oCommonMethod.getValueByPatter(course, "<heure_rap_evol>(.*?)</heure_rap_evol>");
						String audience_pari_course = oCommonMethod.getValueByPatter(course, "<audience_pari_course>(.*?)</audience_pari_course>");
						
						String montant_enjeu_total = oCommonMethod.getValueByPatter(course, "<montant_enjeu_total>(.*?)</montant_enjeu_total>");
						
						Matcher rapps_probs_part = oCommonMethod.getMatcherStrGroup(course, "<rapp_prob_part>(.*?)</rapp_prob_part>");
						while(rapps_probs_part.find()){
							String rapp_prob_part = rapps_probs_part.group(1);
							String nom_cheval = oCommonMethod.getValueByPatter(rapp_prob_part, "<nom_cheval>(.*?)</nom_cheval>");
							String num_partant = oCommonMethod.getValueByPatter(rapp_prob_part, "<num_partant>(.*?)</num_partant>");
							
							String ecurie_part = oCommonMethod.getValueByPatter(rapp_prob_part, "<ecurie_part>(.*?)</ecurie_part>");
							String rapp_ref = oCommonMethod.getValueByPatter(rapp_prob_part, "<rapp_ref>(.*?)</rapp_ref>");
							String rapp_evol = oCommonMethod.getValueByPatter(rapp_prob_part, "<rapp_evol>(.*?)</rapp_evol>");
							String favori = oCommonMethod.getValueByPatter(rapp_prob_part, "<favori>(.*?)</favori>");
							String signes = oCommonMethod.getValueByPatter(rapp_prob_part, "<signes>(.*?)</signes>");
									
//							montant_enjeu_total=montant_enjeu_total==null?null:montant_enjeu_total.replace(".", "").replace(",", "");
							rapp_ref=rapp_ref==null?null:rapp_ref.replace(".", "").replace(",", ".");
							rapp_evol=rapp_evol==null?null:rapp_evol.replace(".", "").replace(",", ".");
							signes=signes==null?null:signes.replace(".", "").replace(",", ".");
							
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
//							  RacecachedRS.updateString("id_nav_reunion", id_nav_reunion);
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
							  HorsecachedRS.updateString("ind_quadrio", ind_quadrio);
							  HorsecachedRS.updateString("heure_rap_ref", heure_rap_ref);
							  HorsecachedRS.updateString("heure_rap_evol", heure_rap_evol);
							  HorsecachedRS.updateString("audience_pari_course", audience_pari_course);
							  HorsecachedRS.updateString("montant_enjeu_total", montant_enjeu_total);
							  HorsecachedRS.updateString("nom_cheval", nom_cheval);
							  if(num_partant==null){
								  HorsecachedRS.updateNull("num_partant");
							  }else{
								  HorsecachedRS.updateInt("num_partant",Integer.parseInt(num_partant));
							  }
							  HorsecachedRS.updateString("ecurie_part", ecurie_part);
							  HorsecachedRS.updateString("rapp_ref", rapp_ref);
							  HorsecachedRS.updateString("rapp_evol", rapp_evol);
							  HorsecachedRS.updateString("favori", favori);
							  if(signes==null){
								  HorsecachedRS.updateNull("signes");
							  }else{
								  HorsecachedRS.updateDouble("signes",Double.parseDouble(signes));
							  }
							  HorsecachedRS.updateDate("ExtractTime", new java.sql.Date(new Date().getTime()));
							  HorsecachedRS.updateString("fileName", fileName);
							  
							  HorsecachedRS.insertRow();
							  HorsecachedRS.moveToCurrentRow();	
						}
						rapps_probs_part=null;
					}
					courses=null;
				}
			}
			if (bPerMeetingCommit == false) {
				InsertBulk("PmuInfoCentreKafka_XML_LiveOdds",HorsecachedRS);							
			}
		}
		catch (Exception e) 
		{
			logger.error("error fileName:"+fileName+"\n\n",e);
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
		String filePath ="D:\\Denis\\PMU XPRO APP\\XML\\51_RAPP_PROB_SG_EVOL";
		new ParseLiveOdds51Bll().run(filePath);
	}
}
