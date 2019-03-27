package com.datalabchina.bll1;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.common.DateUtils;
import com.common.FileDispose;
import com.common.Utils;
import com.common.db.ZTStd;
import com.datalabchina.controler.Controler;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import com.sun.rowset.CachedRowSetImpl;

import common.KafkaSender;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseData implements Runnable {
	
	private static Logger logger = Logger.getLogger(ParseData.class);
	
	private static boolean debug = false;
	
	private static boolean useBulkCopy = false;
	
	static ZTStd ztd = new ZTStd();
	
	static DecimalFormat df = new DecimalFormat("######0.00");
	
	static String file;
	
	static String Timestamp = null;
	
	static String MessageDataType = "Odds";
	static String TableName = "lsports_Bookie_liveOdds";
	
	static Object lock = new Object();
	
	static HashMap<String, String> hmSqlAll = new HashMap<String, String>();
	static HashMap<String, String> hmOddsAll = new HashMap<String, String>();
	
//	static String tableName = "LSports_Bookie_liveOdds_history";
	static String tableName = "LSports_Bookie_liveOdds";
	
	static CachedRowSet RacecachedRS = null;
	
	static String connectionString = "";
	static String connectionString_Mirr = "";
	
	public static void init(){
		try {
			connectionString = getConnectionByDBConfig("");
			connectionString_Mirr = getConnectionByDBConfig("_mirr");
			
			//复制表结构
			RacecachedRS = new CachedRowSetImpl();
			RacecachedRS.setUrl(connectionString);
			RacecachedRS.setCommand("select * from " + tableName + " where 1=2");
			RacecachedRS.execute();
			useBulkCopy = true;
		} catch (Exception e) {
			logger.error("", e);
			useBulkCopy = false;
		}
	}
	
	public static void initData() {
		
	}
	
	public static String getConnectionByDBConfig(String ext){
		String sConfigPath = System.getProperty("user.dir")+ System.getProperty("file.separator") + "config.xml";
		String Configbody = FileDispose.readFile(sConfigPath);
		String databaseName = Utils.extractMatchValue(Configbody, "<databaseName"+ext+">(.*?)</databaseName"+ext+">");
		String dbconfigPath = Utils.extractMatchValue(Configbody, "<dbconfigPath"+ext+">(.*?)</dbconfigPath"+ext+">");
		
		String dbConfigBody= FileDispose.readFile(dbconfigPath+File.separator+"dbconfig.xml");
		//jdbc:jtds:sqlserver://192.168.120.216:1433/FRDB;user=spider;password=83862909
		//jdbc:jtds:sqlserver://eusqld:1433/LSports;instance=inst04;user=spider;password=83862909</property>
		String ConnectionString = Utils.extractMatchValue(dbConfigBody, "<property name=\""+databaseName+"\">.*?url=(.*?)</property>");
		if (ConnectionString != null) {
			ConnectionString=ConnectionString.replace("jtds:", "").replace("/LSports", ";databaseName=LSports").replace("instance", "instanceName");
			ConnectionString=ConnectionString.replace(":1433", "").replace(":2882", "");
		}
		
		//:2882/LSports;		
		//jdbc:sqlserver://192.168.120.216:1433;databaseName=FRDB;user=spider;password=83862909
		logger.info("jdbc="+ConnectionString);
		return ConnectionString;
	}
	
	public void run() {
		try {
			
//			while (true) {
//				long l = System.currentTimeMillis();
//				savePage();
//				l = System.currentTimeMillis() - l;
//				l = (1000 * 5) - l;
//				if (l > 1000) {
//					logger.info("sleep ms " + l);
//					Thread.sleep(l);
//				}
//			}

		} catch (Exception e) {
			logger.error("", e);
		}
	}
		
	public static boolean InsertBulk(String tableName, CachedRowSet cachedRS, String connString) {

		if (useBulkCopy == false) return false;
		
		if (connString == null) return false;
		
		SQLServerBulkCopy bulkCopy = null;
		try {
//			RacecachedRS.execute();

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

			bulkCopy = new SQLServerBulkCopy(connString);
			SQLServerBulkCopyOptions copyOptions = new SQLServerBulkCopyOptions();
			copyOptions.setBulkCopyTimeout(0);
			copyOptions.setCheckConstraints(false);
			bulkCopy.setBulkCopyOptions(copyOptions);
			bulkCopy.setDestinationTableName(tableName);

			StopWatch sw = new StopWatch();
			sw.start();
			try {
				bulkCopy.writeToServer(cachedRS);
			} catch (Exception e) {
				if (e.toString().indexOf("The duplicate key") > 0) {
					return false;
				}
				logger.error("", e);
				return false;
//				String s = e.toString();
				//700106395, 2, 112, Nov  6 2017  6:59AM, 160
//				s = Utils.extractMatchValue(s, "The duplicate key value is \\((.*?)\\)");
//				logger.error("", e);
//				System.err.println(s);				
			}

			bulkCopy.close();

			sw.stop();
			logger.info("insert " + cachedRS.size() + " rows, used : "
					+ sw.getTime() / 1000 + " s");

		} catch (Exception e) {
			logger.error("", e);
			return false;
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
		
		return true;
	}	
	
	public static void local(String begin,String end) {
		try {
			
			Date dBegin = DateUtils.toShortDate1(begin);
			Date dEnd = DateUtils.toShortDate1(end);
			while (dBegin.compareTo(dEnd) <= 0) {
				String date = DateUtils.getShortStr(dBegin);
				logger.info(date);
				local(date);
				dBegin = DateUtils.add(dBegin, Calendar.DATE, 1);
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}		
	}	
	
	public static void local(String date) {
		try {
			
			hmSqlAll.clear();
			hmOddsAll.clear();
			String year = date.substring(0, 4);
			String month = date.substring(5, 7);
			String day = date.substring(8, 10);
			
			String path = Controler.saveFilePath + File.separator +
				"BookOdds" + File.separator +
				year + File.separator + month + File.separator +day + File.separator;

			logger.info("parsing  " + path);
			List<String> files = FileDispose.getFiles(path);				
			for (String file : files) {
				logger.info("parsing  " + file);
				fullData(file, null);					
			}				
						
		} catch (Exception e) {
			logger.error("", e);
		}		
	}
	
	public static void parse(String strBody, String fileName) {
		synchronized(lock) {
			fullData(strBody, fileName);
		}
	}
	
	public static void fullData(String strBody, String fileName) {
		
		try {
			
			StopWatch sw = new StopWatch();
			sw.start();
			
			file = fileName;
			
			if (strBody == null) {
				strBody = FileDispose.readFile(fileName);
			}
			
//			-<Header>
//			<Status>000</Status>
//			<Description>OK</Description>
//			<Timestamp>1498452186</Timestamp>
//			<clientsTimestamp>1497912176</clientsTimestamp>
//			</Header>
			
			Timestamp = Utils.extractMatchValue(strBody, "<Header>.*?<Timestamp>(.*?)</Timestamp>");
			if (debug) System.err.println("Timestamp = " + Timestamp);
			
			//String ExtractTime = DateUtils.getLongStr();
			
			//20171015_000812.xml
			String ExtractTime = fileName.substring(fileName.lastIndexOf(File.separator)+1);
			ExtractTime = ExtractTime.replaceAll("_event\\.zip", "");
			String date = ExtractTime.substring(0,4) + "-" + ExtractTime.substring(4,6) + "-" + ExtractTime.substring(6,8) 
					+ " " + ExtractTime.substring(9,11) + ":" + ExtractTime.substring(11,13) + ":" + ExtractTime.substring(13,15);
			ExtractTime = date;
			
			
			HashMap<String, String> hmSql = new HashMap<String, String>();
//			List<String> sqlList = new ArrayList<String>();
			
//			String sql = "delete from lsports_Bookie_liveOdds where ExtractTime = '2017-11-06 14:59:48'";
//			ztd.execSQL(sql);
	
/*

-<Event>
-<Participants>
<Participant Name="The Cruel Sea" Number="1" IsRunning="true" Silk="" Form="" Trainer="P J Fernie" Jockey="D Staeck" Gender="" Weight="59" Age="0"/>
<Participant Name="Casino Belle" Number="2" IsRunning="true" Silk="" Form="" Trainer="P J Fernie" Jockey="L Warwick" Gender="" Weight="57" Age="0"/>
<Participant Name="Haul In" Number="3" IsRunning="true" Silk="" Form="" Trainer="P J Fernie" Jockey="F Kersley" Gender="" Weight="54.5" Age="0"/>
<Participant Name="Kronfeld" Number="4" IsRunning="true" Silk="" Form="" Trainer="M J Dellar" Jockey="W White" Gender="" Weight="54.5" Age="0"/>
<Participant Name="Smart As A Fox" Number="5" IsRunning="true" Silk="" Form="" Trainer="N D Dellar" Jockey="S Bogenhuber" Gender="" Weight="54.5" Age="0"/>
</Participants>
-<RaceResults>
<Participant Name="Casino Belle" Distance="" Number="2" Position="1"/>
<Participant Name="The Cruel Sea" Distance="" Number="1" Position="2"/>
<Participant Name="Kronfeld" Distance="" Number="4" Position="3"/>
<Participant Name="Smart As A Fox" Distance="" Number="5" Position="4"/>
<Participant Name="Haul In" Distance="" Number="3" Position=""/>
</RaceResults>
<Scores/>
<Stat/>
<Scorers/>
<Cards/>

-<Outcomes>
-<Outcome name="Race Winner" id="160">
-<Bookmaker name="Bet365" id="8" isResulting="true" lastUpdate="2017-06-25T06:57:26.934">
<Odds id="343329494699825308" isWinner="1" ProgramNumber="2" Status="Suspended" bookieOutcomeID="" LastUpdate="2017-06-25T06:57:26.934" line="" currentPrice="2.4" startPrice="2.4" bet="Casino Belle"/>
<Odds id="418917272699825308" isWinner="0" ProgramNumber="3" Status="Suspended" bookieOutcomeID="" LastUpdate="2017-06-25T06:57:26.934" line="" currentPrice="17" startPrice="13" bet="Haul In"/>
<Odds id="1941726564699825308" isWinner="0" ProgramNumber="4" Status="Suspended" bookieOutcomeID="" LastUpdate="2017-06-25T06:57:26.934" line="" currentPrice="14" startPrice="17" bet="Kronfeld"/>
<Odds id="1511009028699825308" isWinner="0" ProgramNumber="5" Status="Suspended" bookieOutcomeID="" LastUpdate="2017-06-25T06:57:26.934" line="" currentPrice="12" startPrice="7" bet="Smart As A Fox"/>
<Odds id="1989961972699825308" isWinner="0" ProgramNumber="1" Status="Suspended" bookieOutcomeID="" LastUpdate="2017-06-25T06:57:26.934" line="" currentPrice="2.05" startPrice="1.8" bet="The Cruel Sea"/>
</Bookmaker>
</Outcome>
</Outcomes>
</Event>
 			
 */
			long s = 0;
			int lKafka = 0;
			String jsonArray = "";
			List<String> Events = Utils.extractMatchValues(strBody, "<Event>(.*?)</Event>");
			for (String event : Events) {
				
				if (debug) System.err.println("============================================");
//				<EventID>699825308</EventID>
//				<StartDate>2017-06-25T04:58:00.000</StartDate>
//				<SportID Name="Horse Racing">687888</SportID>
//				<LeagueID Name="Kalgoorlie">50000747</LeagueID>
//				<LocationID Name="Australia">172</LocationID>
//				<Race PlaceOddsFactor="4" PlaceTerm="2" AgeTo="0" AgeFrom="0" Going="" Surface="" Distance="1400" Category="" Type="" Title="Kalsec/Creative Business-Bm70+" Number="1"/>
				
//				<Status>Finished</Status>
//				<LastUpdate>2017-06-26T01:48:49.191</LastUpdate>
//				<HomeTeam Name="Kalgoorlie" ID="50001989"/>				
				
//				String LastUpdateTime = Utils.extractMatchValue(event, " LastUpdate=\"(.*?)\"");      		//LastUpdateTime datetime,
//				if (debug) System.err.println("LastUpdateTime = " + LastUpdateTime);				
				
				String SportName = Utils.extractMatchValue(event, "<SportID Name=\"(.*?)\"");          		//SportName varchar(30),
				if (debug) System.err.println("SportName = " + SportName);
				
				String EventID = Utils.extractMatchValue(event, "<EventID>(.*?)<");          				//EventID bigint,
				if (debug) {
					System.err.println("EventID = " + EventID);
//					if (!EventID.equals("700091799")) continue;
				}
				
				
				String RaceDate = Utils.extractMatchValue(event, "<StartDate>(.*?)T");         			 	//RaceDate smalldatetime,
				if (debug) System.err.println("RaceDate = " + RaceDate);
				
				
				//=====================================

				String StartDate = Utils.extractMatchValue(event, "<StartDate>(.*?)\\.").replaceAll("T", " ");
				if (debug) System.err.println("StartDate = " + StartDate);
				
				
				Date dNow = DateUtils.getUTCNow();
				Date dStartDateUTC = DateUtils.fromLongStringUTC(StartDate);

				//jackfan: 取3天之内的,太久远的就不取
				if ((dStartDateUTC.getTime() - dNow.getTime()) >= 1000 * 60 * 60 * 24 * 3) {
					continue;
				}				
				
				//=====================================
				
				//<StartDate>2017-06-25T04:58:00.000</StartDate>
				String RaceTime = Utils.extractMatchValue(event, "<StartDate>.*?T(\\d{1,2}:\\d{1,2}):");          			//RaceTime varchar(5) = NULL,
//				RaceTime = RaceTime.replaceAll("T", " ").replaceAll(":00.000", "");
				if (debug) System.err.println("RaceTime = " + RaceTime);
				
				String LeagueName = Utils.extractMatchValue(event, "<LeagueID Name=\"(.*?)\"");          	//LeagueName varchar(30) = NULL,
				if (debug) System.err.println("LeagueName = " + LeagueName);
				
				String LocationName = Utils.extractMatchValue(event, "<LocationID Name=\"(.*?)\"");          //LocationName varchar(30) = NULL,
				if (debug) System.err.println("LocationName = " + LocationName);
				
				String RaceNo = Utils.extractMatchValue(event, "<Race .*?Number=\"(.*?)\"");          		//RaceNo tinyint,
				if (debug) System.err.println("RaceNo = " + RaceNo);
				
				if (debug) System.err.println("-------------------------------------------------");
				
//-<Participants>
//<Participant Name="Asian Princess" Number="1" IsRunning="true" Silk="" Form="7" Trainer="Corrie Lensley" Jockey="M. Thackeray" Gender="Mare" Weight="60" Age="2"/>
//<Participant Name="Wings N Things" Number="10" IsRunning="true" Silk="" Form="5 - 3 - 3" Trainer="Phillip Smith" Jockey="G. Wrogemann" Gender="Mare" Weight="60" Age="2"/>
//</Participants>
				
				HashMap<String, String> hmHorseName = new HashMap<String, String>();
				List<String> Participants = Utils.extractMatchValues(event, "<Participant (.*?)/>");				
				for (String participant : Participants) {

					String Name = Utils.extractMatchValue(participant, "Name=\"(.*?)\"");
					if (debug) System.err.println("Name = " + Name);

					String Number = Utils.extractMatchValue(participant, "Number=\"(.*?)\"");
					if (debug) System.err.println("Number = " + Number);
					
					hmHorseName.put(Number, Name);
				}
				
				
//<Scores/>
//<Stat/>
//<Scorers/>
//<Cards/>
//-<Outcomes>
//-<Outcome name="Race Winner" id="160">
//-<Bookmaker name="Bet365" id="8" isResulting="false" lastUpdate="2017-06-25T12:03:31.330">
//<Odds id="278017813699828775" ProgramNumber="1" Status="Open" bookieOutcomeID="" LastUpdate="2017-06-25T12:03:31.330" line="" currentPrice="1" startPrice="1" bet="Asian Princess"/>
//<Odds id="42821281699828775" ProgramNumber="10" Status="Open" bookieOutcomeID="" LastUpdate="2017-06-25T12:03:31.330" line="" currentPrice="1" startPrice="1" bet="Wings N Things"/>
//</Bookmaker>
//-<Bookmaker name="Ladbrokes" id="81" isResulting="false" lastUpdate="2017-06-26T01:52:41.416">
//<Odds id="2072969200699828775" ProgramNumber="1" Status="Open" bookieOutcomeID="" LastUpdate="2017-06-26T01:52:41.416" line="" currentPrice="1" startPrice="1" bet="Asian Princess"/>
//<Odds id="802450856699828775" ProgramNumber="10" Status="Open" bookieOutcomeID="" LastUpdate="2017-06-26T01:52:41.416" line="" currentPrice="1" startPrice="1" bet="Wings N Things"/>
//</Bookmaker>
//</Outcome>
//</Outcomes>				
				
				List<String> Outcomes = Utils.extractMatchValues(event, "<Outcome (.*?)</Outcome>");				
				for (String Outcome : Outcomes) {
					
					
					String OutcomeName = Utils.extractMatchValue(Outcome, "name=\"(.*?)\"");          	  //Bookmakerid int,
					String OutcomeId = Utils.extractMatchValue(Outcome, "id=\"(.*?)\"");          	  		//Bookmakerid int,
					if (debug) System.err.println("OutcomeName = " + OutcomeName);
					if (debug) System.err.println("OutcomeId = " + OutcomeId);
				
					List<String> Bookmakers = Utils.extractMatchValues(Outcome, "<Bookmaker (.*?)</Bookmaker>");				
					for (String book : Bookmakers) {
						//System.out.println("book = " + book);
						String Bookmakerid = Utils.extractMatchValue(book, "id=\"(.*?)\"");          	  //Bookmakerid int,
						if (debug) {
							System.err.println("Bookmakerid = " + Bookmakerid);
//							if (!Bookmakerid.equals("122")) continue;
						}
						
						String BookmakerName = Utils.extractMatchValue(book, " name=\"(.*?)\"");          //BookmakerName varchar(50) = NULL,
						if (debug) System.err.println("BookmakerName = " + BookmakerName);
						
						String IsResulting = Utils.extractMatchValue(book, "isResulting=\"(.*?)\"");      //IsResulting bit = NULL,
						if (debug) System.err.println("IsResulting = " + IsResulting);
						if (IsResulting == null || IsResulting.equals("false")) {
							IsResulting = "0";
						} else {
							IsResulting = "1";
						}
						
						List<String> Odds = Utils.extractMatchValues(book, "<(Odds .*?)/>");				
						for (String odd : Odds) {
					
							//<Odds id="2072969200699828775" ProgramNumber="1" Status="Open" bookieOutcomeID="" LastUpdate="2017-06-26T01:52:41.416" line="" currentPrice="1" startPrice="1" bet="Asian Princess"/>
							String Clothno = Utils.extractMatchValue(odd, " ProgramNumber=\"(.*?)\"");          //Clothno tinyint,
							if (debug) {
								System.err.println("Clothno = " + Clothno);
//								if (!Clothno.equals("1")) continue;
							}
							if (Clothno == null) continue;
							
							try {
								Integer.parseInt(Clothno);
							} catch (Exception e) {
								continue;
							}
							
							String OddsID = Utils.extractMatchValue(odd, "Odds id=\"(.*?)\"");
							if (debug) System.err.println("OddsID = " + OddsID);
							
							String HorseName = hmHorseName.get(Clothno);          								//HorseName varchar(50) = NULL,
							if (debug) System.err.println("HorseName = " + HorseName);
													
							String StartPrice = Utils.extractMatchValue(odd, " startPrice=\"(.*?)\"");          //StartPrice decimal(8, 2) = NULL,
							if (debug) System.err.println("StartPrice = " + StartPrice);
							
							String CurrentPrice = Utils.extractMatchValue(odd, " currentPrice=\"(.*?)\"");       //CurrentPrice decimal(8, 2) = NULL,
							if (debug) System.err.println("CurrentPrice = " + CurrentPrice);
							if (CurrentPrice == null || CurrentPrice.equals("1") || CurrentPrice.equals("0")) {
								continue;
							}
							
							String EventStatus = Utils.extractMatchValue(odd, " Status=\"(.*?)\"");          	//EventStatus varchar(10) = NULL,
							if (debug) System.err.println("EventStatus = " + EventStatus);
							
							String Line = Utils.extractMatchValue(odd, " line=\"(.*?)\"");          			//Line varchar(20) = NULL,
							if (debug) System.err.println("Line = " + Line);
							
							String LastUpdateTime = Utils.extractMatchValue(odd, " LastUpdate=\"(.*?)\"");      //LastUpdateTime datetime,
							LastUpdateTime = LastUpdateTime.replaceAll("T", " ");
							if (debug) System.err.println("LastUpdateTime = " + LastUpdateTime);
	
							StringBuffer sbSql = new StringBuffer();  			// pr_lsports_Bookie_liveOdds_InsertData
							appendStringParameter(sbSql,SportName);				// varchar(30),
							appendParameter(sbSql,EventID);						// bigint,
							appendStringParameter(sbSql,RaceDate);				// smalldatetime,
							appendStringParameter(sbSql,RaceTime);				// varchar(5) = NULL,
							appendStringParameter(sbSql,LeagueName);			// varchar(30) = NULL,
							appendStringParameter(sbSql,LocationName);			// varchar(30) = NULL,
							appendParameter(sbSql,RaceNo);						// tinyint,
							appendParameter(sbSql,Clothno);						// tinyint,
							appendStringParameter(sbSql,HorseName);				// varchar(50) = NULL,
							appendParameter(sbSql,Bookmakerid);					// int,
							appendStringParameter(sbSql,BookmakerName);			// varchar(50) = NULL,
							appendParameter(sbSql,StartPrice);					// decimal(8, 2) = NULL,
							appendParameter(sbSql,CurrentPrice);				// decimal(8, 2) = NULL,
							appendStringParameter(sbSql,EventStatus);			// varchar(10) = NULL,
							appendStringParameter(sbSql,Line);					// varchar(20) = NULL,
							appendParameter(sbSql,IsResulting);					// bit = NULL,
							appendStringParameter(sbSql,LastUpdateTime);		// datetime,
							appendStringParameter(sbSql,ExtractTime);			// datetime = NULL
							
							appendStringParameter(sbSql,OutcomeName);			// varchar(50) = NULL,
							appendParameter(sbSql,OutcomeId);					// Id int,
							appendParameter(sbSql,OddsID);						// OddsID
							String sql = sbSql.toString();
							sql = sql.substring(1);
							
							String key = EventID + "-" + Clothno + "-" + Bookmakerid + "-" + LastUpdateTime + "-" + OutcomeId;
							String oldSql = hmSqlAll.get(key);
							if (oldSql == null) {
								hmSqlAll.put(key, sql);
								hmSql.put(key, sql);
								String odlValue = hmOddsAll.get(key);
								String newValue = StartPrice + "_" + CurrentPrice;
								if (odlValue == null  || !odlValue.equals(newValue)) {
									hmOddsAll.put(key, newValue);
									
									String json = "{}";
									@SuppressWarnings("unchecked")
									Map<String, Object> maps = new ObjectMapper().readValue(json, Map.class);
									maps.put("SportName", SportName);
									maps.put("EventID", EventID);
									maps.put("RaceDate", RaceDate);
									maps.put("RaceTime", RaceTime);
									maps.put("LeagueName", LeagueName);
									maps.put("LocationName", LocationName);
									maps.put("RaceNo", RaceNo);
									
									maps.put("Clothno", Clothno);
									maps.put("HorseName", HorseName);
									maps.put("Bookmakerid", Bookmakerid);
									maps.put("BookmakerName", BookmakerName);
									
									maps.put("StartPrice", StartPrice);
									maps.put("CurrentPrice", CurrentPrice);
									maps.put("EventStatus", EventStatus);
									maps.put("Line", Line);
									maps.put("IsResulting", IsResulting);
									
									maps.put("LastUpdateTime", LastUpdateTime);
									maps.put("ExtractTime", ExtractTime);
									maps.put("OutcomeName", OutcomeName);
									maps.put("OutcomeId", OutcomeId);
									maps.put("OddsID", OddsID);									
									json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(maps);
									
									lKafka++;
									jsonArray = jsonArray + "," + json; 
									
									if (lKafka >= 100) {
										json = "{\"MessageData\" : ["+jsonArray.substring(1)+"]}";
										KafkaSender.send(json, MessageDataType, TableName);
										lKafka=0;
										jsonArray="";
									}
									
								} else {
									continue;
								}
							} else {
//								logger.info("oldSql = " + oldSql);
								continue;
							}
							

							if (useBulkCopy) {
								
								RacecachedRS.moveToInsertRow();
								RacecachedRS.updateString("SportName", SportName);									// varchar(30),
								RacecachedRS.updateInt("EventID", Integer.parseInt(EventID));						// bigint,
								RacecachedRS.updateDate("RaceDate", java.sql.Date.valueOf(RaceDate));				// smalldatetime,
								RacecachedRS.updateString("RaceTime", RaceTime);									// varchar(5) = NULL,
								RacecachedRS.updateString("LeagueName", LeagueName);								// varchar(30) = NULL,
								RacecachedRS.updateString("LocationName", LocationName);							// varchar(30) = NULL,
								RacecachedRS.updateInt("RaceNo", Integer.parseInt(RaceNo));							// tinyint,
								RacecachedRS.updateInt("Clothno", Integer.parseInt(Clothno));						// tinyint,
								RacecachedRS.updateString("HorseName", HorseName);									// varchar(50) = NULL,
								RacecachedRS.updateInt("Bookmakerid", Integer.parseInt(Bookmakerid));				// int,
								RacecachedRS.updateString("BookmakerName", BookmakerName);							// varchar(50) = NULL,
								
								//RacecachedRS.updateDouble("StartPrice", Double.parseDouble(StartPrice));	   			// decimal(8, 2) = NULL,
								//RacecachedRS.updateDouble("CurrentPrice", Double.parseDouble(CurrentPrice));			// decimal(8, 2) = NULL,
								
								RacecachedRS.updateString("StartPrice", parseStr(StartPrice));	   					// decimal(8, 2) = NULL,
								RacecachedRS.updateString("CurrentPrice", parseStr(CurrentPrice));					// decimal(8, 2) = NULL,
								
								RacecachedRS.updateString("EventStatus", EventStatus);								// varchar(10) = NULL,
								RacecachedRS.updateNull("Line");													// varchar(20) = NULL,
								RacecachedRS.updateShort("IsResulting", Short.parseShort(IsResulting));				// bit = NULL,
								RacecachedRS.updateTimestamp("LastUpdateTime", java.sql.Timestamp.valueOf(LastUpdateTime));		// datetime,
								RacecachedRS.updateTimestamp("ExtractTime", java.sql.Timestamp.valueOf(ExtractTime));			// datetime = NULL
								
								RacecachedRS.updateString("OutcomeName", OutcomeName);								// varchar(50) = NULL,
								RacecachedRS.updateInt("OutcomeId", Integer.parseInt(OutcomeId));					// Id int,
								RacecachedRS.updateLong("OddsID", Long.parseLong(OddsID));							// OddsID
								
								RacecachedRS.insertRow();
								RacecachedRS.moveToCurrentRow();			 
							}
														
							s++;
							
						}
						
					} //Bookmakers
				
				} //Outcomes
				
			}
			
			if (lKafka > 0) {
				String json = "{\"MessageData\" : ["+jsonArray.substring(1)+"]}";
				KafkaSender.send(json, MessageDataType, TableName);
				lKafka=0;
				jsonArray="";
			}
			
			sw.stop();
			logger.info("parse " + s + " rows, used : " + sw.getTime() / 1000 + " s");
			
			sw.reset();
			sw.start();
			
			if (s == 0) return;
			
			boolean result = false;
			if (useBulkCopy) {
				CachedRowSet RacecachedRS_Mirr = RacecachedRS.createCopy();
				result = InsertBulk(tableName,RacecachedRS,connectionString);
				if (result == true) {
					result = InsertBulk(tableName,RacecachedRS_Mirr,connectionString_Mirr);
				}
				RacecachedRS.execute();
				sw.stop();
				if (result == false) {
					logger.info("BulkInsert duplicate, use StoredProcedures insert. size = " + hmSql.size());
				}
			}			
			
			if (result == false && tableName.indexOf("history") == -1) {
				sw.reset();
				sw.start();
//				int i = 0;
				for(String sql : hmSql.values()) {
//					i++
//					logger.info(i + " " + sql);
					if (!ztd.ExecStoredProcedures("pr_lsports_Bookie_liveOdds_InsertData",sql)) {
						logger.error("file = " + file); 
					}					
				}
				sw.stop();
				logger.info("StoredProcedures insert " + s + " rows, used : " + sw.getTime() / 1000 + " s");
			} else {
				logger.info("InsertBulk " + s + " rows, used : " + sw.getTime() / 1000 + " s");
			}
			
			logger.info("hmSqlAll.size() =  " + hmSqlAll.size());
		
		} catch (Exception e) {
			logger.error("fullData. file = " + file, e);
		}		
	}	
			
	public static String parseStr(String str){
		str=str==null?null:df.format(Double.parseDouble(str));
//		System.out.println(str);
//		if(str!=null&&!str.contains("."))str=str+".00";
		return str;
	}	
	
	private static void appendParameter(StringBuffer sb, String parameter) {
		if (debug) {
//			System.err.println(" ========== " + parameter);			
		}
		
		if (parameter != null && parameter.trim().equals("")) {
			parameter = null;
		}
			
		sb.append("," + parameter);			
	}
	
	private static void appendStringParameter(StringBuffer sb, String parameter) {
		if (debug) {
//			System.err.println(" ========== " + parameter);			
		}

		if (parameter == null) {
			sb.append("," + parameter);			
		} else {
			parameter = parameter.replaceAll("'", "''");
			sb.append(",'" + parameter + "'");
		}
	}	
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");	
//		debug = true;		
//		new BookOdds().savePage();
		
//		String fileName = "F:\\ZA\\20171105_075113.xml";	
//		ParseData bll = new ParseData();
//		bll.fullData(fileName, null);
		
//		String url = "http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEvents?email=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&sports=687888,687893,687894&lang=en&oddsFormat=EU&";
//		String body = PageHelper.getPageHelper().doGet(url);
//		System.out.println(body);
		
	}
}
