//package com.datalabchina.bll1;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
//
//import com.common.DateUtils;
//import com.common.FileDispose;
//import com.common.PageHelper;
//import com.common.Utils;
//import com.common.db.ZTStd;
//import com.datalabchina.controler.Controler;
//
//public class BookOdds implements Runnable {
//	
////		SimpleDateFormat oSDFTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		public static long sendno = 0;
//		public static long sendedno = 0;
//		public static List<String> toSaveList = new ArrayList<String>();
//	
////		public SendOddsChangeNotifyToMQ() {
////		}
//	//	
//////		public SendOddsChangeNotifyToMQ(String sJMSInfo, String sRaceStartTime) {
//////			JMSInfo = sJMSInfo;
//////			raceStartTime = sRaceStartTime;
//////		}
//	//
//	
//		public static void sendMQ(String line, String sRaceStartTime) {
//			synchronized (toSaveList) {
//				sendno++;
//				toSaveList.add(line + "##" + sRaceStartTime);
//				logger.info("toSaveList.size() = " + toSaveList.size());
//				if (isRunning("BookOdds_Thread") == false) {
//					logger.error("BookOdds Thread not find. start one");
//					new Thread(new BookOdds(), "BookOdds_Thread").start();
//				}
//				
//			}		
//		}
//	
//		private static boolean isRunning(String name) {
//			ThreadGroup sys;
//			Thread[] all;
//			String sThreadName = null;
//			Boolean isRunning = false;
//	
//			sys = Thread.currentThread().getThreadGroup();
//			all = new Thread[sys.activeCount()];
//			sys.enumerate(all);
//			for (int i = 0; i < all.length; i++) {
//				sThreadName = all[i].getName();
////				logger.info("Thread Name=" + sThreadName);
//				if (sThreadName.equals(name)) {
//					isRunning = true;
//					break;
//				}
//			}
//			
//			sys = null;
//			all = null;
//			sThreadName = null;
//			return isRunning;
//		}
//		
////		public static String getLongStr() {
////			Date currentTime = new Date();
////			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////			String dateString = formatter.format(currentTime);
////			return dateString;
////		}
//	
//	
//	private static Logger logger = Logger.getLogger(BookOdds.class);
//	
//	private static boolean debug = false;
//	
//	PageHelper page = PageHelper.getPageHelper();
//	
//	ZTStd ztd = new ZTStd();
//	
//	private String file;
//	
//	static String Timestamp = null;
//	
//	public BookOdds(){
//	}
//	
//	public void run() {
//		try {
//			
//			while (true) {
//				String line = "";
//				synchronized (toSaveList) {
//					int s = toSaveList.size();
//					if (s > 0) {
//						try {
//							line = toSaveList.get(0);
//							toSaveList.remove(0);
//						} catch (Exception e) {
//						}
//					}
//				}
//				
//				if (line.equals("")) {
//					Thread.sleep(1000);
//				} else {
//					save(line);								
//				}
//			}
//
//			
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//	}
//	
////	public void local(String begin,String end) {
////		try {
////			
////			Date dBegin = DateUtils.toShortDate1(begin);
////			Date dEnd = DateUtils.toShortDate1(end);
////			while (dBegin.compareTo(dEnd) <= 0) {
////				String date = DateUtils.getShortStr(dBegin);
////				logger.info(date);
////				local(date);
////				dBegin = DateUtils.add(dBegin, Calendar.DATE, 1);
////			}
////			
////		} catch (Exception e) {
////			logger.error("", e);
////		}		
////	}	
////	
////	public void local(String date) {
////		try {
////			
////			String year = date.substring(0, 4);
////			String month = date.substring(5, 7);
////			String day = date.substring(8, 10);
////			
////			String path = Controler.saveFilePath + File.separator +
////				"BookOdds" + File.separator +
////				year + File.separator + month + File.separator +day + File.separator;
////
////			logger.info("parsing  " + path);
////			List<String> files = FileDispose.getFiles(path);
////			
////			for (String file : files) {
////				logger.info("parsing  " + file);
////				fullData(file, null);
////			}				
////						
////		} catch (Exception e) {
////			logger.error("", e);
////		}		
////	}	
//	
//	public void save(String line) {
//		try {
//			if (line.startsWith("{\"Header\":{\"Type\":32")) return;
//			
//			if (line.startsWith("{\"Header\":{\"Type\":1")) {
//				save1(line);
//				return;
//			}
//			
//			if (line.startsWith("{\"Header\":{\"Type\":3")) {
//				save3(line);
//				return;
//			}				
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//	}
//		
//	public void save1(String line) {
//		try {
//				
//			if (line.startsWith("{\"Header\":{\"Type\":32")) return;
//			
//			if (line.startsWith("{\"Header\":{\"Type\":1")) {
//				return;
//			}
//			
//			if (line.startsWith("{\"Header\":{\"Type\":3")) {
//				return;
//			}				
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//	}		
//	
//	public void save3(String line) {
//		
//		try {
//			
///*
//			
//			
//			
//*/			
//			
//			//{"Header":{"Type":1,"MsgId":1,"MsgGuid":"531dad8b-1604-411f-83f8-d53a7179c54c","ServerTimestamp":1542557319},"Body":{"Competition":{"Id":2911,"Name":"Parx Racing","Type":1,"Competitions":[{"Id":4654,"Name":"Race 3","Type":2,"Events":[{"FixtureId":4162048,"Livescore":null,"Markets":null,"OutrightFixture":{"Sport":{"Id":687888,"Name":"Horse Racing"},"Location":{"Id":4,"Name":"United States"},"StartDate":"2018-11-18T17:19:00","LastUpdate":"2018-11-18T16:07:57.9071","Status":1,"Participants":[{"Id":51774843,"Name":"Layla's Voyage","Position":"1","IsActive":1,"ExtraData":[{"Name":"TrainerName","Value":"Francis Meares"},{"Name":"Jockey","Value":"Silvestre Gonzalez"},{"Name":"JockeyWeight","Value":"54.88"},{"Name":"Dam","Value":"Black Tie Voyage"},{"Name":"DamSire","Value":"Macho Uno"},{"Name":"Sire","Value":"Indy Wind"}]},{"Id":52141702,"Name":"Island Getaway","Position":"2","IsActive":1,"ExtraData":[{"Name":"TrainerName","Value":"John Servis"},{"Name":"Jockey","Value":"Frankie Pennington"},{"Name":"JockeyWeight","Value":"53.98"},{"Name":"Dam","Value":"Island Bound"},{"Name":"DamSire","Value":"Speightstown"},{"Name":"Sire","Value":"Animal Kingdom"}]},{"Id":51717434,"Name":"Month Of Sundays","Position":"3","IsActive":1,"ExtraData":[{"Name":"TrainerName","Value":"Cynthia Reese"},{"Name":"Jockey","Value":"Luis Rodriguez Castro"},{"Name":"JockeyWeight","Value":"55.34"},{"Name":"Dam","Value":"Untamed Melody"},{"Name":"DamSire","Value":"Sultry Song"},{"Name":"Sire","Value":"Any Given Saturday"}]},{"Id":52076533,"Name":"Hanna Due","Position":"4","IsActive":1,"ExtraData":[{"Name":"TrainerName","Value":"Marten Woodhouse"},{"Name":"Jockey","Value":"Anthony Nunez"},{"Name":"JockeyWeight","Value":"54.88"},{"Name":"Dam","Value":"Hana J"},{"Name":"DamSire","Value":"Peaks And Valleys"},{"Name":"Sire","Value":"E Dubai"}]},{"Id":52118447,"Name":"Ocean Court","Position":"6","IsActive":1,"ExtraData":[{"Name":"TrainerName","Value":"Louis Linder, Jr."},{"Name":"Jockey","Value":"Abel Mariano"},{"Name":"JockeyWeight","Value":"54.88"},{"Name":"Dam","Value":"Sea Grouch"},{"Name":"DamSire","Value":"Sea Hero"},{"Name":"Sire","Value":"Court Vision"},{"Name":"Color","Value":"Mare"}]},{"Id":52068461,"Name":"Elijah's Party","Position":"7","IsActive":1,"ExtraData":[{"Name":"TrainerName","Value":"Penny Pearce"},{"Name":"Jockey","Value":"Erick Lopez"},{"Name":"JockeyWeight","Value":"54.88"},{"Name":"Dam","Value":"Miss Accord"},{"Name":"DamSire","Value":"A. P Jet"},{"Name":"Sire","Value":"Desert Party"}]}]}}]}]}}}
//			
////			file = fileName;
////			
////			if (strBody == null) {
////				strBody = FileDispose.readFile(fileName);
////			}
//			
//			Timestamp = Utils.extractMatchValue(strBody, "<Header>.*?<Timestamp>(.*?)</Timestamp>");
//			if (debug) System.err.println("Timestamp = " + Timestamp);
//			
//			//String ExtractTime = DateUtils.getLongStr();
//			
//			String ExtractTime = fileName.substring(fileName.lastIndexOf(File.separator)+1);
//			ExtractTime = ExtractTime.replaceAll("\\.xml", "");
//			String date = ExtractTime.substring(0,4) + "-" + ExtractTime.substring(4,6) + "-" + ExtractTime.substring(6,8) 
//					+ " " + ExtractTime.substring(9,11) + ":" + ExtractTime.substring(11,13) + ":" + ExtractTime.substring(13,15);
//			ExtractTime = date;
//
//			long s = 0;
//			List<String> Events = Utils.extractMatchValues(strBody, "<Event>(.*?)</Event>");
//			for (String event : Events) {
//				
//				if (debug) System.err.println("============================================");
//	
//				
//				String SportName = Utils.extractMatchValue(event, "<SportID Name=\"(.*?)\"");          		//SportName varchar(30),
//				if (debug) System.err.println("SportName = " + SportName);
//				
//				String EventID = Utils.extractMatchValue(event, "<EventID>(.*?)<");          				//EventID bigint,
//				if (debug) {
//					System.err.println("EventID = " + EventID);
//				}
//				
//				
//				String RaceDate = Utils.extractMatchValue(event, "<StartDate>(.*?)T");         			 	//RaceDate smalldatetime,
//				if (debug) System.err.println("RaceDate = " + RaceDate);
//				
//				String RaceTime = Utils.extractMatchValue(event, "<StartDate>.*?T(.*?)<");          			//RaceTime varchar(5) = NULL,
//				RaceTime = RaceTime.replaceAll("T", " ").replaceAll(":00.000", "");
//				if (debug) System.err.println("RaceTime = " + RaceTime);
//				
//				String LeagueName = Utils.extractMatchValue(event, "<LeagueID Name=\"(.*?)\"");          	//LeagueName varchar(30) = NULL,
//				if (debug) System.err.println("LeagueName = " + LeagueName);
//				
//				String LocationName = Utils.extractMatchValue(event, "<LocationID Name=\"(.*?)\"");          //LocationName varchar(30) = NULL,
//				if (debug) System.err.println("LocationName = " + LocationName);
//				
//				String RaceNo = Utils.extractMatchValue(event, "<Race .*?Number=\"(.*?)\"");          	//RaceNo tinyint,
//				if (debug) System.err.println("RaceNo = " + RaceNo);
//				
//				if (debug) System.err.println("-------------------------------------------------");
//				
//				HashMap<String, String> hmHorseName = new HashMap<String, String>();
//				List<String> Participants = Utils.extractMatchValues(event, "<Participant (.*?)/>");				
//				for (String participant : Participants) {
//
//					String Name = Utils.extractMatchValue(participant, "Name=\"(.*?)\"");
//					if (debug) System.err.println("Name = " + Name);
//
//					String Number = Utils.extractMatchValue(participant, "Number=\"(.*?)\"");
//					if (debug) System.err.println("Number = " + Number);
//					
//					hmHorseName.put(Number, Name);
//				}
//				
//				List<String> Outcomes = Utils.extractMatchValues(event, "<Outcome (.*?)</Outcome>");				
//				for (String Outcome : Outcomes) {
//					
//					
//					String OutcomeName = Utils.extractMatchValue(Outcome, "name=\"(.*?)\"");          	  //Bookmakerid int,
//					String OutcomeId = Utils.extractMatchValue(Outcome, "id=\"(.*?)\"");          	  		//Bookmakerid int,
//					if (debug) System.err.println("OutcomeName = " + OutcomeName);
//					if (debug) System.err.println("OutcomeId = " + OutcomeId);
//				
//					List<String> Bookmakers = Utils.extractMatchValues(Outcome, "<Bookmaker (.*?)</Bookmaker>");				
//					for (String book : Bookmakers) {
//						String Bookmakerid = Utils.extractMatchValue(book, "id=\"(.*?)\"");          	  //Bookmakerid int,
//						if (debug) {
//							System.err.println("Bookmakerid = " + Bookmakerid);
//						}
//						
//						String BookmakerName = Utils.extractMatchValue(book, " name=\"(.*?)\"");          //BookmakerName varchar(50) = NULL,
//						if (debug) System.err.println("BookmakerName = " + BookmakerName);
//						
//						String IsResulting = Utils.extractMatchValue(book, "isResulting=\"(.*?)\"");      //IsResulting bit = NULL,
//						if (debug) System.err.println("IsResulting = " + IsResulting);
//						if (IsResulting == null || IsResulting.equals("false")) {
//							IsResulting = "0";
//						} else {
//							IsResulting = "1";
//						}
//						
//						List<String> Odds = Utils.extractMatchValues(book, "<(Odds .*?)/>");				
//						for (String odd : Odds) {
//					
//							String Clothno = Utils.extractMatchValue(odd, " ProgramNumber=\"(.*?)\"");          //Clothno tinyint,
//							if (debug) {
//								System.err.println("Clothno = " + Clothno);
//							}
//							if (Clothno == null) continue;
//							
//							try {
//								Integer.parseInt(Clothno);
//							} catch (Exception e) {
//								continue;
//							}
//							
//							String OddsID = Utils.extractMatchValue(odd, "Odds id=\"(.*?)\"");
//							if (debug) System.err.println("OddsID = " + OddsID);
//							
//							String HorseName = hmHorseName.get(Clothno);          								//HorseName varchar(50) = NULL,
//							if (debug) System.err.println("HorseName = " + HorseName);
//													
//							String StartPrice = Utils.extractMatchValue(odd, " startPrice=\"(.*?)\"");          //StartPrice decimal(8, 2) = NULL,
//							if (debug) System.err.println("StartPrice = " + StartPrice);
//							
//							String CurrentPrice = Utils.extractMatchValue(odd, " currentPrice=\"(.*?)\"");       //CurrentPrice decimal(8, 2) = NULL,
//							if (debug) System.err.println("CurrentPrice = " + CurrentPrice);
//							
//							String EventStatus = Utils.extractMatchValue(odd, " Status=\"(.*?)\"");          	//EventStatus varchar(10) = NULL,
//							if (debug) System.err.println("EventStatus = " + EventStatus);
//							
//							String Line = Utils.extractMatchValue(odd, " line=\"(.*?)\"");          			//Line varchar(20) = NULL,
//							if (debug) System.err.println("Line = " + Line);
//							
//							String LastUpdateTime = Utils.extractMatchValue(odd, " LastUpdate=\"(.*?)\"");      //LastUpdateTime datetime,
//							if (debug) System.err.println("LastUpdateTime = " + LastUpdateTime);
//	
//							StringBuffer sbSql = new StringBuffer();  			// pr_lsports_Bookie_liveOdds_InsertData
//							appendStringParameter(sbSql,SportName);				// varchar(30),
//							appendParameter(sbSql,EventID);						// bigint,
//							appendStringParameter(sbSql,RaceDate);				// smalldatetime,
//							appendStringParameter(sbSql,RaceTime);				// varchar(5) = NULL,
//							appendStringParameter(sbSql,LeagueName);			// varchar(30) = NULL,
//							appendStringParameter(sbSql,LocationName);			// varchar(30) = NULL,
//							appendParameter(sbSql,RaceNo);						// tinyint,
//							appendParameter(sbSql,Clothno);						// tinyint,
//							appendStringParameter(sbSql,HorseName);				// varchar(50) = NULL,
//							appendParameter(sbSql,Bookmakerid);					// int,
//							appendStringParameter(sbSql,BookmakerName);			// varchar(50) = NULL,
//							appendParameter(sbSql,StartPrice);					// decimal(8, 2) = NULL,
//							appendParameter(sbSql,CurrentPrice);				// decimal(8, 2) = NULL,
//							appendStringParameter(sbSql,EventStatus);			// varchar(10) = NULL,
//							appendStringParameter(sbSql,Line);					// varchar(20) = NULL,
//							appendParameter(sbSql,IsResulting);					// bit = NULL,
//							appendStringParameter(sbSql,LastUpdateTime);		// datetime,
//							appendStringParameter(sbSql,ExtractTime);			// datetime = NULL
//							
//							appendStringParameter(sbSql,OutcomeName);			// varchar(50) = NULL,
//							appendParameter(sbSql,OutcomeId);					// Id int,
//							appendParameter(sbSql,OddsID);						// OddsID
//							String sql = sbSql.toString();
//							sql = sql.substring(1);
//											
//							s++;
//							if (!ztd.ExecStoredProcedures("pr_lsports_Bookie_liveOdds_InsertData",sql)) {
//								logger.error("file = " + file); 
//							}
//							
//						}
//						
//					} //Bookmakers
//				
//				} //Outcomes
//				
//			}
//			
//			logger.info("insert to db " + s + " rows");
//		
//		} catch (Exception e) {
//			logger.error("fullData. file = " + file, e);
//		}		
//	}	
//	
//	private void appendParameter(StringBuffer sb, String parameter) {
//		if (debug) {
////			System.err.println(" ========== " + parameter);			
//		}
//		
//		if (parameter != null && parameter.trim().equals("")) {
//			parameter = null;
//		}
//			
//		sb.append("," + parameter);			
//	}
//	
//	private void appendStringParameter(StringBuffer sb, String parameter) {
//		if (debug) {
////			System.err.println(" ========== " + parameter);			
//		}
//
//		if (parameter == null) {
//			sb.append("," + parameter);			
//		} else {
//			parameter = parameter.replaceAll("'", "''");
//			sb.append(",'" + parameter + "'");
//		}
//	}	
//	
//	public static void main(String[] args) {
//		PropertyConfigurator.configure("log4j.properties");	
//		debug = true;		
//		String fileName = "F:\\ZA\\20171106_145948.xml";
//		new BookOdds().fullData(fileName, null);
//	}
//}
