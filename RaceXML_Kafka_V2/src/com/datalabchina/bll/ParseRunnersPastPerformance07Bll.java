package com.datalabchina.bll;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.datalabchina.controler.Controller;
import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.FileDispose;


public class ParseRunnersPastPerformance07Bll {
	private  Logger logger = Logger.getLogger(ParseRunnersPastPerformance07Bll.class.getName());
	private  CommonDB oCommonDB =new CommonDB();
	private  CommonMethod oCommonMethod = new CommonMethod();	
	
	

	public void parseFile(String fileName){
//		fileName ="C:\\Users\\zhida.li\\AppData\\Roaming\\Skype\\My Skype Received Files\\XML\\20160815\\XML\\07_PART_DERNIERES_COURSE\\77730183.xml";
		String  body = FileDispose.readFile(fileName);
		try {
			Matcher JoursMatcher = oCommonMethod.getMatcherStrGroup(body, "<jour>(.*?)</jour>");
			while(JoursMatcher.find()){
				String oneJours = JoursMatcher.group(1);
				String code_statut_infos = oCommonMethod.getValueByPatter(oneJours, "<code_statut_infos>(.*?)</code_statut_infos>");
				
				String lib_stat_infos = oCommonMethod.getValueByPatter(oneJours, "<lib_stat_infos>(.*?)</lib_stat_infos>");
				
				String date_heure_generation = oCommonMethod.getValueByPatter(oneJours, "<date_heure_generation>(.*?)</date_heure_generation>");
				
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
							String nom_cheval = oCommonMethod.getValueByPatter(partant, "<nom_cheval>(.*?)</nom_cheval>");
							String num_partant = oCommonMethod.getValueByPatter(partant, "<num_partant>(.*?)</num_partant>");
							
							Matcher cperfs = oCommonMethod.getMatcherStrGroup(partant, "<cperf>(.*?)</cperf>");
							while(cperfs.find()){
								String cperf = cperfs.group(1);
								
								String date = oCommonMethod.getValueByPatter(cperf, "<date>(.*?)</date>");
								if(date==null)continue;
								String cipo = oCommonMethod.getValueByPatter(cperf, "<cipo>(.*?)</cipo>");
								String lipo = oCommonMethod.getValueByPatter(cperf, "<lipo>(.*?)</lipo>");
								String perfet = oCommonMethod.getValueByPatter(cperf, "<perfet>(.*?)</perfet>");
								String dsc = oCommonMethod.getValueByPatter(cperf, "<dsc translate=\"mandatory\"\\s?>(.*?)</dsc>");
								String nomprix = oCommonMethod.getValueByPatter(cperf, "<nomprix>(.*?)</nomprix>");
								String pntr = oCommonMethod.getValueByPatter(cperf, "<pntr>(.*?)</pntr>");
								String ter = oCommonMethod.getValueByPatter(cperf, "<ter translate=\"mandatory\"\\s?>(.*?)</ter>");
								String ctg = oCommonMethod.getValueByPatter(cperf, "<ctg translate=\"mandatory\"\\s?>(.*?)</ctg>");
								String taloc = oCommonMethod.getValueByPatter(cperf, "<taloc>(.*?)</taloc>");
								String gains = oCommonMethod.getValueByPatter(cperf, "<gains>(.*?)</gains>");
								String dstcper = oCommonMethod.getValueByPatter(cperf, "<dstcper>(.*?)</dstcper>");
								String nbpar = oCommonMethod.getValueByPatter(cperf, "<nbpar>(.*?)</nbpar>");
								String tmppre = oCommonMethod.getValueByPatter(cperf, "<tmppre>(.*?)</tmppre>");
								String tpdpt = oCommonMethod.getValueByPatter(cperf, "<tpdpt>(.*?)</tpdpt>");
								String pst = oCommonMethod.getValueByPatter(cperf, "<pst>(.*?)</pst>");
								String npmu = oCommonMethod.getValueByPatter(cperf, "<npmu>(.*?)</npmu>");
								String plarr = oCommonMethod.getValueByPatter(cperf, "<plarr>(.*?)</plarr>");
							
								Matcher arprfs = oCommonMethod.getMatcherStrGroup(cperf, "<arprf>(.*?)</arprf>");
								while(arprfs.find()){
									String arprf = arprfs.group(1);
									String pl = oCommonMethod.getValueByPatter(arprf, "<pl>(.*?)</pl>");
									String plav = oCommonMethod.getValueByPatter(arprf, "<plav>(.*?)</plav>");
									String nom = oCommonMethod.getValueByPatter(arprf, "<nom>(.*?)</nom>");
									if(nom==null)continue;
									String suf = oCommonMethod.getValueByPatter(arprf, "<suf>(.*?)</suf>");
									String plc = oCommonMethod.getValueByPatter(arprf, "<plc>(.*?)</plc>");
									String dpre = oCommonMethod.getValueByPatter(arprf, "<dpre translate=\"mandatory\"\\s?>(.*?)</dpre>");
									String gs = oCommonMethod.getValueByPatter(arprf, "<gs>(.*?)</gs>");
									String trcl = oCommonMethod.getValueByPatter(arprf, "<trcl>(.*?)</trcl>");
									String vrcl = oCommonMethod.getValueByPatter(arprf, "<vrcl>(.*?)</vrcl>");
									String pds = oCommonMethod.getValueByPatter(arprf, "<pds>(.*?)</pds>");
									String dch = oCommonMethod.getValueByPatter(arprf, "<dch>(.*?)</dch>");
									String rkm = oCommonMethod.getValueByPatter(arprf, "<rkm>(.*?)</rkm>");
									String top = oCommonMethod.getValueByPatter(arprf, "<top>(.*?)</top>");
									String pr = oCommonMethod.getValueByPatter(arprf, "<pr>(.*?)</pr>");
									String jk = oCommonMethod.getValueByPatter(arprf, "<jk>(.*?)</jk>");
									String ent = oCommonMethod.getValueByPatter(arprf, "<ent>(.*?)</ent>");
									String nump = oCommonMethod.getValueByPatter(arprf, "<nump>(.*?)</nump>");
									String oeil = oCommonMethod.getValueByPatter(arprf, "<oeil>(.*?)</oeil>");
									
//									String sSql ="";
									StringBuffer sSql =new StringBuffer(); 
								     sSql=sSql.append(code_statut_infos==null?"NULL,":"N'"+code_statut_infos+"',");
								     sSql=sSql.append(lib_stat_infos==null?"NULL,":"N'"+lib_stat_infos+"',");
								     sSql=sSql.append(date_heure_generation==null?"NULL,":"N'"+oCommonMethod.getTime(date_heure_generation)+"',");
								     sSql=sSql.append(type_document==null?"NULL,":"N'"+type_document+"',");
								     sSql=sSql.append(date_jour_reunion==null?"NULL,":"N'"+oCommonMethod.getTime(date_jour_reunion)+"',");
								     sSql=sSql.append(id_nav_reunion==null?"NULL,":"N'"+id_nav_reunion+"',");
								     sSql=sSql.append(num_externe_reunion==null?"NULL,":"N'"+num_externe_reunion+"',");
								     sSql=sSql.append(code_hippo==null?"NULL,":"N'"+code_hippo+"',");
								     sSql=sSql.append(num_reunion==null?"NULL,":"N'"+num_reunion+"',");
								     sSql=sSql.append(id_nav_course==null?"NULL,":"N'"+id_nav_course+"',");
								     sSql=sSql.append(num_course_pmu==null?"NULL,":"N'"+num_course_pmu+"',");
								     sSql=sSql.append(id_nav_partant==null?"NULL,":"N'"+id_nav_partant+"',");
								     sSql=sSql.append(nom_cheval==null?"NULL,":"N'"+nom_cheval+"',");
								     sSql=sSql.append(num_partant==null?"NULL,":"N'"+num_partant+"',");
								     sSql=sSql.append(date==null?"NULL,":"N'"+oCommonMethod.getTime(date)+"',");
								     sSql=sSql.append(cipo==null?"NULL,":"N'"+cipo+"',");
								     sSql=sSql.append(lipo==null?"NULL,":"N'"+lipo+"',");
								     sSql=sSql.append(perfet==null?"NULL,":"N'"+perfet+"',");
								     sSql=sSql.append(dsc==null?"NULL,":"N'"+dsc+"',");
								     sSql=sSql.append(nomprix==null?"NULL,":"N'"+nomprix+"',");
								     sSql=sSql.append(pntr==null?"NULL,":"N'"+pntr+"',");
								     sSql=sSql.append(ter==null?"NULL,":"N'"+ter+"',");
								     sSql=sSql.append(ctg==null?"NULL,":"N'"+ctg+"',");
								     sSql=sSql.append(taloc==null?"NULL,":"N'"+taloc+"',");
								     sSql=sSql.append(gains==null?"NULL,":"N'"+gains+"',");
								     sSql=sSql.append(dstcper==null?"NULL,":"N'"+dstcper+"',");
								     sSql=sSql.append(nbpar==null?"NULL,":"N'"+nbpar+"',");
								     sSql=sSql.append(tmppre==null?"NULL,":"N'"+tmppre+"',");
								     sSql=sSql.append(tpdpt==null?"NULL,":"N'"+tpdpt+"',");
								     sSql=sSql.append(pst==null?"NULL,":"N'"+pst+"',");
								     sSql=sSql.append(npmu==null?"NULL,":"N'"+npmu+"',");
								     sSql=sSql.append(plarr==null?"NULL,":"N'"+plarr+"',");
								     sSql=sSql.append(pl==null?"NULL,":"N'"+pl+"',");
								     sSql=sSql.append(plav==null?"NULL,":"N'"+plav+"',");
								     sSql=sSql.append(nom==null?"NULL,":"N'"+nom+"',");
								     sSql=sSql.append(suf==null?"NULL,":"N'"+suf+"',");
								     sSql=sSql.append(plc==null?"NULL,":"N'"+plc+"',");
								     sSql=sSql.append(dpre==null?"NULL,":"N'"+dpre+"',");
								     sSql=sSql.append(gs==null?"NULL,":"N'"+gs+"',");
								     sSql=sSql.append(trcl==null?"NULL,":"N'"+trcl+"',");
								     sSql=sSql.append(vrcl==null?"NULL,":"N'"+vrcl+"',");
								     sSql=sSql.append(pds==null?"NULL,":"N'"+pds+"',");
								     sSql=sSql.append(dch==null?"NULL,":"N'"+dch+"',");
								     sSql=sSql.append(rkm==null?"NULL,":"N'"+rkm+"',");
								     sSql=sSql.append(top==null?"NULL,":"N'"+top+"',");
								     sSql=sSql.append(pr==null?"NULL,":"N'"+pr+"',");
								     sSql=sSql.append(jk==null?"NULL,":"N'"+jk+"',");
								     sSql=sSql.append(ent==null?"NULL,":"N'"+ent+"',");
								     sSql=sSql.append(nump==null?"NULL,":"N'"+nump+"',");
								     sSql=sSql.append(oeil==null?"NULL,":"N'"+oeil+"',");
										
									sSql=sSql.append("'"+oCommonMethod.getCurrentTime()+"',");
									sSql=sSql.append("N'"+fileName.replace(Controller.sSaveFilePath,"")+"'");
										
								     logger.info("pr_PmuInfoCentreKafka_XML_RunnersPastPerformance_InsertData  " + sSql);
								     oCommonDB.execStoredProcedures("pr_PmuInfoCentreKafka_XML_RunnersPastPerformance_InsertData", sSql.toString());
								     sSql=null;
								}
								arprfs=null;
							}
							cperfs=null;
							
						}
						partants=null;
						
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
