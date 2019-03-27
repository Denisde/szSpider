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

public class OutRight implements Runnable {

	private static Logger logger = Logger.getLogger(OutRight.class);
	
	PageHelper page = PageHelper.getPageHelper();
	
	ZTStd ztd = new ZTStd();
	
	static boolean debug = false;
	
	static long Horsecount = 0;
	static long Racecount = 0;
	
	public static HashMap<String, String> hmRaceInfo = new HashMap<String, String>();
	public static HashMap<String, String> hmClothNo = new HashMap<String, String>();

	private String line = null;
	
	public OutRight() {
	}	
	
	public OutRight(String line) {
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
				logger.info("sleep " + (sleep / 1000 / 60) + " min to get Outright");
				try {
					Thread.sleep(sleep);
				} catch (Exception e) {
				}
				time = System.currentTimeMillis();
				GetOutrightFixtures();
				
			} while (true);
				
		}
	}
	
	public void GetOutrightFixtures() {

		try {
			
			//has data
			//http://prematch.lsports.eu/OddService/GetOutrightFixtures?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=1413&guid=a1772950-77f5-4f24-8926-49393f2e7ad5			
			
//				<option value="687888">Horse Racing (687888)</option>
//				<option value="687894">Trotting (687894)</option>
//				<option value="687893">Greyhounds (687893)</option>
			
			//&Timestamp=1532337621
//			String url = "http://prematch.lsports.eu/OddService/GetOutrightFixtures?username=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5";//&Sports=687893&Lang=English&Sports=687888"; //&lang=en&oddsFormat=EU&
			
			String fileName = Controler.saveFilePath + File.separator + "Fixtures" + File.separator + DateUtils.getFilePath() + File.separator + DateUtils.getFileName2() + ".js";
			String url = Controler.url + "GetOutrightFixtures?username=" + Controler.email + "&password=" + Controler.password + "&guid=" + Controler.guid;  
			
			String body = PageHelper.getPageHelper().doGet(url);
//			FileDispose.saveFile(body, fileName);
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
			//{"Header":{"Type":37,"ServerTimestamp":1542358475},"Body":[{"Id":3763,"Name":"Peterborough","Type":1,"Competitions":[{"Id":
			JSONObject joRoot = new JSONObject(body);
			JSONObject joHeader = joRoot.getJSONObject("Header");
			JSONArray jaBody = joRoot.getJSONArray("Body");
			for (int b = 0; b < jaBody.length(); b++) {
				JSONObject joCompetition = jaBody.getJSONObject(b);
				parseJson(joHeader, joCompetition);
			}
			
			logger.info("insert " + Racecount+ " record to LSports_Bookie_OutRight_Race_RMQ");
			logger.info("insert " + Horsecount+ " record to LSports_Bookie_OutRight_Horse_RMQ");
			Racecount = 0;
			Horsecount = 0;			
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public void parseLine(String line, String fileName) {
		try {
			
			if (line.startsWith("{\"Header\":{\"Type\":1,") == false) return;
			
			//{"Header":{"Type":1,
			JSONObject joRoot = new JSONObject(line);
			JSONObject joHeader = joRoot.getJSONObject("Header");
			JSONObject joBody = joRoot.getJSONObject("Body");
			JSONObject joCompetition = joBody.getJSONObject("Competition");
			parseJson(joHeader, joCompetition);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void parseJson(JSONObject joHeader, JSONObject joCompetition) {
		int spRaceCount = 0;
		int spHorseCount = 0;
		try {
			
			if (joCompetition == null) return;
			
			String ExtractTime = DateUtils.getLongStr();
//			JSONObject joRoot = new JSONObject(line);
			
//			  "Header": {
//			    "Type": 1,
//			    "MsgId": 1,
//			    "MsgGuid": "531dad8b-1604-411f-83f8-d53a7179c54c",
//			    "ServerTimestamp": 1542557319			

//			JSONObject joHeader = joRoot.getJSONObject("Header");
			String MsgType = joHeader.getString("Type");
			String MsgId = joHeader.getString("MsgId");
			String MsgGuid = joHeader.getString("MsgGuid");
			String ServerTimestamp = joHeader.getString("ServerTimestamp");
			Date date = new Date();
			date.setTime(Long.parseLong(ServerTimestamp+"000"));
			ServerTimestamp = DateUtils.getLongStr(date);
			
//			JSONObject joBody = joRoot.getJSONObject("Body");
//			JSONObject joCompetition = joBody.getJSONObject("Competition");
			
//			  "Body": {
//		    "Competition": {
//		      "Id": 2911,
//		      "Name": "Parx Racing",
//		      "Type": 1,
			
			String CourseID = joCompetition.getString("Id");
			String CourseName = joCompetition.getString("Name");
			String CourseType = joCompetition.getString("Type");		

			System.out.println("CourseID = " + CourseID);
			
			JSONArray jaCompetitions = joCompetition.getJSONArray("Competitions");
			if (jaCompetitions.length() == 0) {
//				JSONObject joCom = joCompetition.getJSONObject("Competition");
				jaCompetitions.put(joCompetition);
			}

        	for (int c = 0; c < jaCompetitions.length(); c++) {
        		JSONObject joCom = jaCompetitions.getJSONObject(c);
			
//  		      "Competitions": [
//		        {
//		          "Id": 4654,
//		          "Name": "Race 3",
//		          "Type": 2,
//		          "Events": [
        		
    			String CompetitionID = joCom.getString("Id");
    			String CompetitionName = joCom.getString("Name");
    			String CompetitionType = joCom.getString("Type");
        		
    			JSONArray jaEvents = joCom.getJSONArray("Events");
	        	for (int e = 0; e < jaEvents.length(); e++){
	        		JSONObject joEvent = jaEvents.getJSONObject(e);
//        		            {
//        		              "FixtureId": 4162048,
//        		              "Livescore": null,
//        		              "Markets": null,
	        			
	        			String EventsID = joEvent.getString("FixtureId");
	        			String Livescore = joEvent.getString("Livescore");
	        			String Markets = joEvent.getString("Markets");	 
	        			
	        			if (Livescore != null) {
	        				logger.error("Livescore != null" + Livescore);
	        			}
	        			
	        			if (Markets != null) {
	        				logger.error("Markets != null" + Markets);
	        			}	        			
	        			
//        		              "OutrightFixture": {
//        		                "Sport": {
//        		                  "Id": 687888,
//        		                  "Name": "Horse Racing"
//        		                },
//        		                "Location": {
//        		                  "Id": 4,
//        		                  "Name": "United States"
//        		                },
//        		                "StartDate": "2018-11-18T17:19:00",
//        		                "LastUpdate": "2018-11-18T16:07:57.9071",
//        		                "Status": 1,
	        			
	        			JSONObject joOutrightFixture = joEvent.getJSONObject("OutrightFixture");
	        			
	        			JSONObject joSport = joOutrightFixture.getJSONObject("Sport");
	        			String SportID = joSport.getString("Id");
	        			String SportName = joSport.getString("Name");	        			
	        			
	        			JSONObject joLocation = joOutrightFixture.getJSONObject("Location");
	        			String LocationID = joLocation.getString("Id");
	        	    	String LocationName = joLocation.getString("Name");
	        	    	
	        	    	
	    				if (Controler.LocationName != null && LocationName != null && Controler.LocationName.indexOf(LocationName) == -1) {
	    					continue;
	    				}	        	    	
	        	    			
	        			String StartDate = joOutrightFixture.getString("StartDate");
	        			String EventsLastUpdate = joOutrightFixture.getString("LastUpdate");
	        			String EvenStatus = joOutrightFixture.getString("Status");
	        			
//	        			SELECT TOP 100 EventsID,BetParticipantId,* FROM [LSports_Bookie_Markets_RMQ] WITH(NOLOCK)  WHERE eventsid=4186970 AND betname ='Gold Club'
//            			Select top 100 EventsID,ParticipantsID,StartDate, CourseName,LocationName,CompetitionName,Position,* from [LSports_Bookie_OutRight_RMQ] WITH(NOLOCK)  WHERE eventsid=4186970 AND ParticipantsName='Gold Club'
	        			
            			String RaceNumber = null;				// tinyint = NULL,
            			String PlaceNumber = null;				// tinyint = NULL,
            			String RaceType = null;					// nvarchar(50) = NULL,
            			String FromAge = null;					// varchar(50) = NULL,
            			String ToAge = null;					// varchar(50) = NULL,
            			String TrackSurface = null;				// varchar(20) = NULL,
            			String RaceTitle = null;				// varchar(100) = NULL,
            			String PlaceOddsFactor = null;			// varchar(10) = NULL,
            			String Category = null;					// varchar(20) = NULL,
            			String TrackCondition = null;			// varchar(20) = NULL,
            			String RaceDistance = null;				// tinyint = NULL,
	        			
            			
//	                    "Name": "PlaceNumber","Value": "3"
//	                    "Name": "RaceType","Value": "Trot Attelé"
//	                    "Name": "ToAge","Value": "3"
//	                    "Name": "RaceNumber","Value": "Race 2"
//	                    "Name": "FromAge","Value": "3"
//	                    "Name": "TrackSurface","Value": "Sand"
//	                    "Name": "RaceTitle","Value": "Prix De Pont Saint-Martin"
//	                    "Name": "PlaceOddsFactor","Value": "5"
//	                    "Name": "Category","Value": "Cod"            			
            			
	        	    	JSONArray jaRaceExtraData = joOutrightFixture.getJSONArray("ExtraData");
	    	        	for (int d = 0; d < jaRaceExtraData.length(); d++){
	    	        		JSONObject joExtraData = jaRaceExtraData.getJSONObject(d);
	    	        		
	    	        		String Name = joExtraData.getString("Name");
	    	        		String Value = joExtraData.getString("Value");
	    	        		if (Name.equalsIgnoreCase("RaceNumber")) {
	    	        			RaceNumber = Value;
	    	        			RaceNumber = RaceNumber.replaceAll("[A-Za-z]", "");
	    	        		} else if (Name.equalsIgnoreCase("PlaceNumber")) {
	    	        			PlaceNumber = Value;
	    	        			PlaceNumber = PlaceNumber.replaceAll("[A-Za-z]", "");
	    	        		} else if (Name.equalsIgnoreCase("RaceDistance")) {
	    	        			RaceDistance = Value;
	    	        			RaceDistance = RaceDistance.replaceAll("[A-Za-z]", "");	    	        			
	    	        		} else if (Name.equalsIgnoreCase("RaceType")) {
	    	        			RaceType = Value;
	    	        		} else if (Name.equalsIgnoreCase("FromAge")) {
	    	        			FromAge = Value;
	    	        		} else if (Name.equalsIgnoreCase("ToAge")) {
	    	        			ToAge = Value;
	    	        		} else if (Name.equalsIgnoreCase("TrackSurface")) {
	    	        			TrackSurface = Value;
	    	        		} else if (Name.equalsIgnoreCase("RaceTitle")) {
	    	        			RaceTitle = Value;
	    	        		} else if (Name.equalsIgnoreCase("PlaceOddsFactor")) {
	    	        			PlaceOddsFactor = Value;
	    	        		} else if (Name.equalsIgnoreCase("Category")) {
	    	        			Category = Value;
	    	        		} else if (Name.equalsIgnoreCase("TrackCondition")) {
	    	        			TrackCondition = Value;	    	        			
	    	        		} else {
	    	        			logger.error("New Race ExtraData. Name = " + Name + " Value = " + Value);		    	        			
	    	        		}
	    	        	}
	    	        		
	    	        	if (RaceNumber != null && !RaceNumber.trim().equals("")) {
	    	        		String value = StartDate + "|" + CourseName + "|" +LocationName + "|" +CompetitionName + "|" + RaceNumber + "|" + SportName; 
	    	        		hmRaceInfo.put(EventsID, value);	    	        		
	    	        	}
	        			
		    			try {
                			StringBuffer sbSqlRace = new StringBuffer();  			// pr_LSports_Bookie_OutRight_Race_RMQ_InsertData
                			appendParameter(sbSqlRace,MsgType);						// smallint = NULL,
                			appendParameter(sbSqlRace,MsgId);						// smallint = NULL,
                			appendStringParameter(sbSqlRace,MsgGuid);				// varchar(50) = NULL,
                			appendStringParameter(sbSqlRace,ServerTimestamp);		// datetime,
                			
                			appendParameter(sbSqlRace,CourseID);					// int = NULL,
                			appendStringParameter(sbSqlRace,CourseName);			// int = NULL,
                			appendParameter(sbSqlRace,CourseType);					// tinyint = NULL,
                			appendParameter(sbSqlRace,CompetitionID);				// int,
                			appendStringParameter(sbSqlRace,CompetitionName);		// varchar(50) = NULL,
                			appendStringParameter(sbSqlRace,CompetitionType);		// varchar(50) = NULL,
                			
                			appendParameter(sbSqlRace,EventsID);					// int,
                			appendParameter(sbSqlRace,Livescore);					// decimal(10, 2) = NULL,
                			appendStringParameter(sbSqlRace,Markets);				// varchar(50) = NULL,
                			appendParameter(sbSqlRace,SportID);						// int,
                			appendStringParameter(sbSqlRace,SportName);				// varchar(50) = NULL,
                			appendParameter(sbSqlRace,LocationID);					// int = NULL,
                			appendStringParameter(sbSqlRace,LocationName);			// nvarchar(50) = NULL,
                			appendStringParameter(sbSqlRace,StartDate);				// smalldatetime,
                			appendStringParameter(sbSqlRace,EventsLastUpdate);		// varchar(50) = NULL,
                			appendParameter(sbSqlRace,EvenStatus);					// tinyint = NULL,
                			
                			appendParameter(sbSqlRace,RaceNumber);					// tinyint = NULL,
                			appendParameter(sbSqlRace,PlaceNumber);					// tinyint = NULL,
                			appendStringParameter(sbSqlRace,RaceType);				// nvarchar(50) = NULL,
                			appendStringParameter(sbSqlRace,FromAge);				// varchar(50) = NULL,
                			appendStringParameter(sbSqlRace,ToAge);					// varchar(50) = NULL,
                			appendStringParameter(sbSqlRace,TrackSurface);			// varchar(20) = NULL,
                			appendStringParameter(sbSqlRace,RaceTitle);				// varchar(100) = NULL,
                			appendStringParameter(sbSqlRace,PlaceOddsFactor);		// varchar(10) = NULL,
                			appendStringParameter(sbSqlRace,Category);				// varchar(20) = NULL,
                			
                			appendStringParameter(sbSqlRace,ExtractTime);			// datetime = NULL
                			appendParameter(sbSqlRace,RaceDistance);				// int = NULL,
                			appendStringParameter(sbSqlRace,TrackCondition);		// varchar(20) = NULL

                			
                			String sql = sbSqlRace.toString();
                			sql = sql.substring(1);
                		
                			Racecount++;
                			spRaceCount++;
                			if (!ztd.ExecStoredProcedures("pr_LSports_Bookie_OutRight_Race_RMQ_InsertData",sql)) {
                				logger.error("json = " + joCompetition.toString()); 
                			}
            			
						} catch (Exception e2) {
							logger.error("", e2);                    			
						}	        			
	        			
//    		                "Participants": [
//  		                  {
//  		                    "Id": 51774843,
//  		                    "Name": "Layla's Voyage",
//  		                    "Position": "1",
//  		                    "IsActive": 1,
//  		                    "ExtraData": [
//  		                      {
//  		                        "Name": "TrainerName",
//  		                        "Value": "Francis Meares"
//  		                      },
	    	        		
	        	    	JSONArray jaParticipants = joOutrightFixture.getJSONArray("Participants");
	    	        	for (int p = 0; p < jaParticipants.length(); p++){
	    	        		JSONObject joParticipants = jaParticipants.getJSONObject(p);
    			
			    			String ParticipantsID = joParticipants.getString("Id");
			    			String ParticipantsName = joParticipants.getString("Name");
			    			String Position = joParticipants.getString("Position");
			    			String IsActive = joParticipants.getString("IsActive");
			    			
			    			

			    			if (Position != null && !Position.trim().equals("")) {
//			    				Position = Position.replaceAll("[A-Za-z]", "");
			    				String key = EventsID + "|" + ParticipantsID;
			    				hmClothNo.put(key, Position);
			    			}
			    			
//			    			System.out.println("ParticipantsID = " + ParticipantsID);
			    			
			    			String TrainerName = null;
	    	    			String Jockey = null;
	    	    			String JockeyWeight = null;
	    	    			String Dam = null;
	    	    			String DamSire = null;
	    	    			String Sire = null;
	    	    			String Age = null;
	    	    			String Sex = null;
	    	    			String ProgramNumber = null;
	    	    			String Form = null;
	    	    			String Color = null;
	    	    			String Breeder = null;
	    	    			
	    	    			String IsRunning = null;
	    	    			String Result = null;
	    	    			String OwnerName = null;
	    	    			String StallDraw = null;
	    	    			
		        	    	JSONArray jaHorseExtraData = joParticipants.getJSONArray("ExtraData");
		    	        	for (int d = 0; d < jaHorseExtraData.length(); d++){
		    	        		JSONObject joExtraData = jaHorseExtraData.getJSONObject(d);
		    	        		
		    	        		String Name = joExtraData.getString("Name");
		    	        		String Value = joExtraData.getString("Value");
		    	        		if (Name.equalsIgnoreCase("TrainerName")) {
		    	        			TrainerName = Value;
		    	        		} else if (Name.equalsIgnoreCase("Jockey")) {
		    	        			Jockey = Value;
		    	        		} else if (Name.equalsIgnoreCase("JockeyWeight")) {
		    	        			JockeyWeight = Value;
		    	        		} else if (Name.equalsIgnoreCase("Dam")) {
		    	        			Dam = Value;
		    	        		} else if (Name.equalsIgnoreCase("DamSire")) {
		    	        			DamSire = Value;
		    	        		} else if (Name.equalsIgnoreCase("Sire")) {
		    	        			Sire = Value;
		    	        		} else if (Name.equalsIgnoreCase("Color")) {
		    	        			Color = Value;
		    	        		} else if (Name.equalsIgnoreCase("Age")) {
		    	        			Age = Value;
		    	        		} else if (Name.equalsIgnoreCase("Sex")) {
		    	        			Sex = Value;
		    	        		} else if (Name.equalsIgnoreCase("ProgramNumber")) {
		    	        			ProgramNumber = Value;
		    	        		} else if (Name.equalsIgnoreCase("Form")) {
		    	        			Form = Value;
		    	        		} else if (Name.equalsIgnoreCase("Breeder")) {
		    	        			Breeder = Value;
		    	        		} else if (Name.equalsIgnoreCase("IsRunning")) {
		    	        			IsRunning = Value;
		    	        			if (IsRunning.equalsIgnoreCase("True")) {
		    	        				IsRunning = "1";
		    	        			} else {
		    	        				IsRunning = "0";
		    	        			}
		    	        		} else if (Name.equalsIgnoreCase("Result")) {
		    	        			Result = Value;
		    	        		} else if (Name.equalsIgnoreCase("Owner")) {
		    	        			OwnerName = Value;
		    	        		} else if (Name.equalsIgnoreCase("StallDraw")) {
		    	        			StallDraw = Value;		    	        			
		    	        		} else {
		    	        			if (Name.equalsIgnoreCase("SilkUrl")) {
		    	        				
		    	        			} else if (Name.equalsIgnoreCase("Blinkers")) {
		    	        				
//		    	        			} else if (Name.equalsIgnoreCase("Owner")) {
		    	        				
		    	        			} else {
		    	        				logger.error("New Horse ExtraData. Name = " + Name + " Value = " + Value);		    	        					    	        			
		    	        			}
		    	        		}
		    	        		
		    	        		//SilkUrl
		    	        		
//		    	        		IsRunning Value = True
//		    	        		IsRunning Value = False
		    	        		
//		    	        		IsRunning bit
//		    	        		Result Varchar(10) 
//		    	        		OwnerName Varchar(50) 
//		    	        		StallDraw tinyint  		    	        		
		    	        		
//			                    "Name": "TrainerName",
//			                    "Name": "Jockey",
//			                    "Name": "JockeyWeight",
//			                    "Name": "Dam",
//			                    "Name": "DamSire",
//			                    "Name": "Sire",
		    	        		
		    	        		try {
			    	        		StringBuffer sbSqlHorse = new StringBuffer();  			// pr_LSports_Bookie_OutRight_Horse_RMQ_InsertData
			    	        		appendParameter(sbSqlHorse,EventsID);					// int,		    	        		
	                    			appendStringParameter(sbSqlHorse,ServerTimestamp);		// datetime,
	                    			
	                    			appendParameter(sbSqlHorse,ParticipantsID);				// int,
	                    			appendStringParameter(sbSqlHorse,ParticipantsName);		// nvarchar(1),
	                    			appendStringParameter(sbSqlHorse,Position);				// Position VARCHAR(10)
	                    			appendParameter(sbSqlHorse,IsActive);					// tinyint = NULL,
	                    			appendStringParameter(sbSqlHorse,TrainerName);			// nvarchar(50) = NULL,
	                    			appendStringParameter(sbSqlHorse,Jockey);				// varchar(50) = NULL,
	                    			appendParameter(sbSqlHorse,JockeyWeight);				// decimal(10, 2) = NULL,
	                    			appendStringParameter(sbSqlHorse,Dam);					// nvarchar(50) = NULL,
	                    			appendStringParameter(sbSqlHorse,DamSire);				// nvarchar(50) = NULL,
	                    			appendStringParameter(sbSqlHorse,Sire);					// nvarchar(50) = NULL,
	                    			appendParameter(sbSqlHorse,Age);						// tinyint = NULL,
	                    			appendStringParameter(sbSqlHorse,Sex);					// nvarchar(5) = NULL,
	                    			appendStringParameter(sbSqlHorse,ProgramNumber);		// varchar(50) = NULL,
	                    			appendStringParameter(sbSqlHorse,Form);					// varchar(50) = NULL,
	                    			appendStringParameter(sbSqlHorse,Color);				// varchar(50) = NULL,
	                    			appendStringParameter(sbSqlHorse,Breeder);				// nvarchar(50) = NULL,
	                    			appendStringParameter(sbSqlHorse,ExtractTime);			// datetime = NULL
	                    			
	    	    	        		appendParameter(sbSqlHorse,IsRunning);					// bit
	    	    	        		appendStringParameter(sbSqlHorse,Result);				// Varchar(10) 
	    	    	        		appendStringParameter(sbSqlHorse,OwnerName);			// Varchar(50) 
	    	    	        		appendParameter(sbSqlHorse,StallDraw);					// tinyint
		    	        			
                        			String sql = sbSqlHorse.toString();
                        			sql = sql.substring(1);
                        		
                        			Horsecount++;
                        			spHorseCount++;
                        			if (!ztd.ExecStoredProcedures("pr_LSports_Bookie_OutRight_Horse_RMQ_InsertData",sql)) {
                        				logger.error("json = " + joCompetition.toString()); 
                        			}
                    			
								} catch (Exception e2) {
									logger.error("", e2);
								}		    	        		
		    	        	}
	    	        	}
	    	        
	        	}
        	}
        			
		} catch (Exception e) {
			logger.error(joCompetition.toString(), e);
		}
		
		if (spRaceCount == 0) {
//			logger.error("spRaceCount == 0\n" + joCompetition.toString());
		}
		
		if (spHorseCount == 0) {
//			logger.error("spHorseCount == 0\n" + joCompetition.toString());
		}
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
//		String file = "E:\\WorkSpaceTFS\\UN\\UN_Lsports_RMQ\\2018\\20181119\\2.js";
//		String file = "E:\\WorkSpaceTFS\\UN\\UN_Lsports_RMQ\\2018\\20181119\\GetOutrightFixtures.json";
//		String file = "E:\\WorkSpaceTFS\\UN\\UN_Lsports_RMQ\\2018\\20181119\\20181119.txt";
		String file = "F:\\UN\\UN_Lsports_RMQ\\2018\\1.js";
		String body = FileDispose.readFile(file);
		new OutRight().parseLine(body, file);
//		new OutRight().parseFile(body, file);
		
//		new OutRight().parseLiveFile(file);
		
		
		
//		String Position = "1A";
//		Position = Position.replaceAll("[A-Za-z]", "");
//		System.out.println(Position);
	}
	
}
