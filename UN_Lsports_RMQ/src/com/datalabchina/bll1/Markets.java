package com.datalabchina.bll1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;

import com.common.DateUtils;
import com.common.FileDispose;
import com.common.PageHelper;
import com.common.db.ZTStd;
import com.datalabchina.controler.Controler;

import common.KafkaSender;

public class Markets implements Runnable {

	private static Logger logger = Logger.getLogger(Markets.class);

	static PageHelper page = PageHelper.getPageHelper();
	
	static ZTStd ztd = new ZTStd();
	
	static boolean debug = false;
	
	static long count = 0;
	
	private String line;
	
	static HashMap<String, String> hmOdds = new HashMap<String, String>();
	
	public Markets() {
		
	}
	
	public Markets(String line) {
		this.line = line;
	}
	
	public void run() {
		
		if (this.line != null) {
			parseLine(this.line, null);			
		} else {
			
			long time = System.currentTimeMillis();
			do {
				long sleep = Controler.OutRight_Sleep_Hour;
				time = sleep - (System.currentTimeMillis() - time);
				if (time < sleep) {
					sleep = time;
				}
				logger.info("sleep " + (sleep / 1000 / 60) + " min to get Markets");
				try {
					Thread.sleep(sleep);
				} catch (Exception e) {
				}
				time = System.currentTimeMillis();
				GetFixtureMarkets();
				
			} while (true);
		}		
		
	}
	
	public void GetFixtureMarkets() {

		try {
			
			//http://prematch.lsports.eu/OddService/GetFixtureMarkets?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=1413&guid=a1772950-77f5-4f24-8926-49393f2e7ad5
			
//				<option value="687888">Horse Racing (687888)</option>
//				<option value="687894">Trotting (687894)</option>
//				<option value="687893">Greyhounds (687893)</option>
			
			//&Timestamp=1532337621
			//String url = "http://prematch.lsports.eu/OddService/GetFixtureMarkets?username=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5";//&Sports=687893&Lang=English&Sports=687888"; //&lang=en&oddsFormat=EU&
			
			String fileName = Controler.saveFilePath + File.separator + "Markets" + File.separator + DateUtils.getFilePath() + File.separator + DateUtils.getFileName2() + ".js";
			String url = Controler.url + "GetFixtureMarkets?username=" + Controler.email + "&password=" + Controler.password + "&guid=" + Controler.guid;  
			String body = PageHelper.getPageHelper().doGet(url);
			FileDispose.saveFile(body.replaceAll("\"FixtureId\"", "\r\n\"FixtureId\""), fileName);
			parseFile(body, fileName);
//			logger.info("---------------------------");
				
		} catch (Exception e) {
			logger.error("", e);
		}		
	}
	
	
	public void parseLiveFile(String fileName) {
		
		try {
	           File file = new File(fileName);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(file)));
				String line = in.readLine();
//				int i = 1;
				while (line != null) {
					line = line.trim();
					if (line.equals("")) {
						line = in.readLine();
						continue;
					}
					
					try {
						parseLine(line, null);						
					} catch (Exception e) {
						logger.error("", e);
					}
					
					line = in.readLine();
				}
				in.close();			
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void parseFile(String body, String fileName) {
		try {
			//{"Header":{"Type":3,"ServerTimestamp":1542358493},"Body":[{"FixtureId":4157382,"Livescore":null,"Markets":[{"Id":160,
			JSONObject joRoot = new JSONObject(body);
			JSONObject joHeader = joRoot.getJSONObject("Header");
			JSONArray jaBody = joRoot.getJSONArray("Body");
			for (int b = 0; b < jaBody.length(); b++) {
				JSONObject joEvent = jaBody.getJSONObject(b);
				parseJson(joHeader, joEvent, false);
			}
			logger.info("insert " + count + " record to LSports_Bookie_Markets_RMQ");
			count = 0;
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public void parseLine(String line, String fileName) {
		try {
			
			if (line.startsWith("{\"Header\":{\"Type\":3,") == false) return;
			
//			System.out.println(line);
			
			JSONObject joRoot = new JSONObject(line);
			JSONObject joHeader = joRoot.getJSONObject("Header");
			JSONObject joBody = joRoot.getJSONObject("Body");
			JSONArray jaEvents = joBody.getJSONArray("Events");
			int insertRecord = 0;
			for (int b = 0; b < jaEvents.length(); b++) {
				JSONObject joEvent = jaEvents.getJSONObject(b);
				int spCount = parseJson(joHeader, joEvent, true);
				insertRecord = insertRecord + spCount;
			}

			if (insertRecord > 0) {
				try {
					String MsgGuid = joHeader.getString("MsgGuid");
					if (MsgGuid != null) {
						if (SaveAndParseDataThread.bSendKafka) {
							KafkaSender.send(line);	
						}
					}
				} catch (Exception e) {
					logger.error("", e);
				}				
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public int parseJson(JSONObject joHeader, JSONObject joEvent, boolean skipPriceEquals1) {
		int record = 0;
		int spCount = 0;
		try {
			
			String ExtractTime = DateUtils.getLongStr();
			
//			  "Header": {
//			    "Type": 3,
//			    "MsgId": 2,
//			    "MsgGuid": "50c9ddd7-0fde-4cc7-8d58-2e22f2b7d9fe",
//			    "ServerTimestamp": 1542557324
//			  },			

			String MsgType = joHeader.getString("Type");
			String MsgId = joHeader.getString("MsgId");
			if (MsgId == null) MsgId = "3";
			String MsgGuid = joHeader.getString("MsgGuid");
			String ServerTimestamp = joHeader.getString("ServerTimestamp");
			Date d = new Date();
			d.setTime(Long.parseLong(ServerTimestamp+"000"));
			ServerTimestamp = DateUtils.getLongStr(d);
			
//			JSONArray jaEvents = joBody.getJSONArray("Events");
			
//        	for (int e = 0; e < jaEvents.length(); e++){
//        		JSONObject joEvent = jaEvents.getJSONObject(e);
        			
//        	        "FixtureId": 4158771,
//        	        "Livescore": null,
//        	        "Markets": [

    			String EventsID = joEvent.getString("FixtureId");
//    			System.out.println("EventsID = " + EventsID);
    			if (EventsID == null) {
    				logger.error("EventsID = null json = " + joEvent.toString());
    			}
    			
//    			SELECT TOP 100 EventsID,BetParticipantId,* FROM [LSports_Bookie_Markets_RMQ] WITH(NOLOCK)  WHERE eventsid=4186970 AND betname ='Gold Club'
//    			Select top 100 EventsID,ParticipantsID,StartDate, CourseName,LocationName,CompetitionName,Position,* from [LSports_Bookie_OutRight_RMQ] WITH(NOLOCK)  WHERE eventsid=4186970 AND ParticipantsName='Gold Club'
//    			String value = StartDate + "|" + CourseName + "|" +LocationName + "|" +CompetitionName;
    			
    			String RaceDate = null;
    			String RaceTime = null;
    			String CourseName = null;
    			String LocationName = null;
    			String RaceNo = null;
    			String SportName = null;
    			
    			String value = OutRight.hmRaceInfo.get(EventsID);
//    			logger.info(EventsID + " = " + value);
    			//[ INFO] main 2018-12-14 15:16:13 4200177 = 2018-12-12T17:15:00|Kempton|Great Britain|Kempton
    			//[ INFO] main 2018-12-14 16:51:43 4201959 = 2018-12-13T15:40:00|Toulouse|France|Toulouse|1
    			if (value != null) {
    				String a[] = value.split("\\|");
    				
    				//2018-12-04 12:50:00
    				RaceDate = a[0];
    				String rt[] = RaceDate.split("T");
    				RaceDate = rt[0];
    				RaceTime = rt[1];
    				CourseName = a[1];
    				LocationName = a[2];
    				
    				if (Controler.LocationName != null && Controler.LocationName.indexOf(LocationName) == -1) {
    					return 0;
    				}
    				//Race 3
    				//RaceNo = a[3].toLowerCase().replaceAll("race", "").trim();
    				RaceNo = a[4];
    				if (RaceNo == null || RaceNo.equals("null")) {
    					RaceNo = a[3].toLowerCase().replaceAll("race", "").trim();
    				}
    				SportName = a[5];
    			} else {
    				logger.info("RaceInfo not find. EventsID = " + EventsID);
//    				if (Controler.LocationName != null) {
//    					return 0;
//    				}    				
    			}
    			
    			
    			String Livescore = joEvent.getString("Livescore");
    			
    			if (Livescore != null) {
    				logger.error("Livescore != null" + Livescore);
    			}    			
    			
    			JSONArray jaMarkets = joEvent.getJSONArray("Markets");
            	for (int m = 0; m < jaMarkets.length(); m++){
            		JSONObject joMarket = jaMarkets.getJSONObject(m);
//                            "Id": 160,
//                            "Name": "Race Winner",
//                            "Providers": [
        			
        			String MarketsID = joMarket.getString("Id");
        			String MarketsName = joMarket.getString("Name");
        			
        			JSONArray jaProviders = joMarket.getJSONArray("Providers");
                	for (int p = 0; p < jaProviders.length(); p++){
                		JSONObject joProvider = jaProviders.getJSONObject(p);
                		
//        		                "Id": 93,
//        		                "Name": "SkyBet",
//        		                "LastUpdate": "2018-11-18T16:08:44.5699314Z",
//        		                "Bets": [
                		
            			String ProvidersID = joProvider.getString("Id");
            			String ProvidersName = joProvider.getString("Name");
            			String ProvidersLastUpdate = joProvider.getString("LastUpdate");                        		
                		
            			JSONArray jaBets = joProvider.getJSONArray("Bets");
                    	for (int b = 0; b < jaBets.length(); b++){
                    		JSONObject joBet = jaBets.getJSONObject(b);
								                            		
//        		                    "Id": 6992726794158771,
//        		                    "Name": "Autobahn Express",
//        		                    "Status": 1,
//        		                    "StartPrice": "1.0",
//        		                    "Price": "5.0",
                    		
//        		                    "ProviderBetId": "498052196",
//        		                    "LastUpdate": "2018-11-18T16:08:44.5543036Z",
//        		                    "ParticipantId": 52055517
                    		
                			String BetID = joBet.getString("Id");
                			String BetName = joBet.getString("Name");
                			String BetStatus = joBet.getString("Status");
                			String BetStartPrice = joBet.getString("StartPrice");
                			String Price = joBet.getString("Price");
                			
//                			String LayPrice = joBet.getString("LayPrice");
//                			String LayPriceVolume = joBet.getString("LayPriceVolume");
//                			String PriceVolume = joBet.getString("PriceVolume");
                			
                			record++;
                			
                			if (skipPriceEquals1) {
                				if (Price == null || Price.equals("1") || Price.equals("1.0") || Price.equals("1.00")) {
                					continue;
                				}                				
                			}
                			
                			String oldPrice = hmOdds.get(BetID);
                			String newPrice = BetStatus + "|" + Price;
                			if (oldPrice != null && oldPrice.equalsIgnoreCase(newPrice)) {
//                				logger.info("Price Exist. BetID = " + BetID + " newPrice = " + newPrice + " oldPrice = " + oldPrice);
                				continue;
                			}
                			hmOdds.put(BetID, newPrice);
                        	
                			String BetProviderBetId = joBet.getString("ProviderBetId");
                			String BetBetLastUpdate = joBet.getString("LastUpdate");
                			String BetParticipantId = joBet.getString("ParticipantId");
                			
                			StringBuffer sbSql = new StringBuffer();  			// pr_LSports_Bookie_Markets_RMQ_InsertData
                			appendParameter(sbSql,MsgType);						// smallint,
                			appendParameter(sbSql,MsgId);						// smallint,
                			appendStringParameter(sbSql,MsgGuid);				// varchar(50) = NULL,
                			appendStringParameter(sbSql,ServerTimestamp);		// datetime,
                			appendParameter(sbSql,EventsID);					// int,
                			appendParameter(sbSql,Livescore);					// decimal(10, 2) = NULL,
                			appendParameter(sbSql,MarketsID);					// smallint,
                			appendStringParameter(sbSql,MarketsName);			// varchar(50) = NULL,
                			appendParameter(sbSql,ProvidersID);					// smallint,
                			appendStringParameter(sbSql,ProvidersName);			// varchar(50) = NULL,
                			appendStringParameter(sbSql,ProvidersLastUpdate);	// varchar(50),
                			appendParameter(sbSql,BetID);						// bigint,
                			appendStringParameter(sbSql,BetName);				// varchar(50) = NULL,
                			appendParameter(sbSql,BetStatus);					// tinyint = NULL,
                			appendParameter(sbSql,BetStartPrice);				// decimal(10, 2) = NULL,
                			appendParameter(sbSql,Price);						// decimal(10, 2) = NULL,
                			appendStringParameter(sbSql,BetProviderBetId);		// varchar(50) = NULL,
                			appendStringParameter(sbSql,BetBetLastUpdate);		// varchar(50) = NULL,
                			appendParameter(sbSql,BetParticipantId);			// int = NULL,
                			appendStringParameter(sbSql,ExtractTime);			// datetime = NULL

//                			SELECT TOP 100 EventsID,BetParticipantId,* FROM [LSports_Bookie_Markets_RMQ] WITH(NOLOCK)  WHERE eventsid=4186970 AND betname ='Gold Club'
//                			Select top 100 EventsID,ParticipantsID,StartDate, CourseName,LocationName,CompetitionName,Position,* from [LSports_Bookie_OutRight_RMQ] WITH(NOLOCK)  WHERE eventsid=4186970 AND ParticipantsName='Gold Club'
                			
			    			String key = EventsID + "|" + BetParticipantId;	
			    			String ClothNo = OutRight.hmClothNo.get(key);
                			
                			appendStringParameter(sbSql,RaceDate);				// SMALLDATETIME = NULL,
                			appendStringParameter(sbSql,RaceTime);				// VARCHAR(5) = NULL,
                			appendStringParameter(sbSql,CourseName);			// varchar(30)=NULL,
                			appendStringParameter(sbSql,LocationName);			// varchar(30)=NULL,
                			appendParameter(sbSql,RaceNo);						// tinyint = NULL,
                			appendStringParameter(sbSql,ClothNo);				// ClothNo VARCHAR(10)
                			
                			appendStringParameter(sbSql,SportName);				//VARCHAR(30) =NULL
                			
//                			appendParameter(sbSql,LayPrice);					// decimal(8,2)
//                			appendParameter(sbSql,LayPriceVolume);				// decimal(8,2)
//                			appendParameter(sbSql,PriceVolume);					// decimal(8,2)
                			
                			String sql = sbSql.toString();
                			sql = sql.substring(1);
                							
                			count++;
                			spCount++;
                			if (!ztd.ExecStoredProcedures("pr_LSports_Bookie_Markets_RMQ_InsertData",sql)) {
                				//logger.error("line = " + line);
                				logger.error("json = " + joEvent.toString()); 
                			}                			
                			
                    	}	//for (int b
                    	
                	}	//for (int p
                	
            	} //for (int m
                      
		} catch (Exception e) {
			logger.error(joEvent.toString(), e);
		}
		
		if (record == 0) {
//			logger.error("record == 0\n" + joEvent.toString());
		}
		return spCount;
	}
	
	private void appendParameter(StringBuffer sb, String parameter) {
		if (debug) {
			// System.err.println(" ========== " + parameter);
		}

		if (parameter != null && parameter.trim().equals("")) {
			parameter = null;
		}

		sb.append("," + parameter);
	}

	private void appendStringParameter(StringBuffer sb, String parameter) {
		if (debug) {
			// System.err.println(" ========== " + parameter);
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
		debug = true;
//		String file = "E:\\WorkSpaceTFS\\UN\\UN_Lsports_RMQ\\2018\\20181119\\3.js";
		
		String file = "E:\\WorkSpaceTFS\\UN\\UN_Lsports_RMQ\\2018\\20181119\\20181119.txt";
//		new Markets().parseLiveFile(file);
		
		String RaceDate = null;
		String RaceTime = null;
		String CourseName = null;
		String LocationName = null;
		String RaceNo = null;
		
		String value = "2018-12-14 16:51:43 4201959 = 2018-12-13T15:40:00|Toulouse|France|Toulouse|1";
		//[ INFO] main 2018-12-14 15:16:13 4200177 = 2018-12-12T17:15:00|Kempton|Great Britain|Kempton
		//[ INFO] main 2018-12-14 16:51:43 4201959 = 2018-12-13T15:40:00|Toulouse|France|Toulouse|1
		if (value != null) {
			String a[] = value.split("\\|");
			
			//2018-12-04 12:50:00
			RaceDate = a[0];
			String rt[] = RaceDate.split("T");
			RaceDate = rt[0];
			RaceTime = rt[1];
			CourseName = a[1];
			LocationName = a[2];
			//Race 3
			//RaceNo = a[3].toLowerCase().replaceAll("race", "").trim();
			RaceNo = a[4];
			if (RaceNo == null || RaceNo.equals("null")) {
				RaceNo = a[3].toLowerCase().replaceAll("race", "").trim();
			}
			System.out.println(RaceNo);
		}		
		
//		String body = FileDispose.readFile(file);
//		new Markets().parseLine(body, file);
	}
	
	
	
}
