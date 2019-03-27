package com.datalabchina.bll1;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.common.DateUtils;
import com.common.FileDispose;
import com.common.PageHelper;
import com.common.Utils;
import com.common.db.ZTStd;
import com.datalabchina.controler.Controler;

public class BookOddsBulk implements Runnable {
	
	private static Logger logger = Logger.getLogger(BookOddsBulk.class);
	
	private static boolean debug = false;
	
	PageHelper page = PageHelper.getPageHelper();
	
	ZTStd ztd = new ZTStd();
	
	static DecimalFormat df = new DecimalFormat("######0.00");
	
	private String file;
	
	static String Timestamp = null;	
	
	public void run() {
		try {
			
			ParseData.init();
			new Thread(new BookOddsBulkByEvent()).start();
			
			while (true) {
				long l = System.currentTimeMillis();
				savePage();
				l = System.currentTimeMillis() - l;
				l = (1000 * 60 * 5) - l; //5 分钟
				if (l > 1000) {
					logger.info("sleep for event list ms " + l);
					Thread.sleep(l);
				}
			}

			
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public void savePage() {
		try {
			
			String date = DateUtils.getLongStr1();
			String year = date.substring(0, 4);
			String month = date.substring(4, 6);
			String day = date.substring(6, 8);
			
//			<Country name="Ireland" id="59"/>
//			<Country name="Great Britain" id="2"/>
			
//			<Country name="United States" id="4"/>
//			<Country name="South Africa" id="14"/>
//			<Country name="France" id="147"/>
//			<Country name="Australia" id="172"/>
			
//			<Country name="England" id="243"/>
						
//			String url = "http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEvents?email=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&sports=687888&countries=14,172&lang=en&oddsFormat=EU&";
			
			//http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEvents?email={email}&password={password}&guid={guid}&timestamp={timestamp}&sports={sports}&countries={countries}&leagues={leagues}&bookmakers={bookmakers}&offertypes={offertypes}&lang={lang}&oddsFormat={oddsFormat}&
//			String url = "http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEvents?email=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&sports=687888,687894&countries=2,4,14,59,147,172,243&lang=en&oddsFormat=EU&";
//			              http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEventByID?email={email}&password={password}&guid={guid}&eventID={eventID}&timestamp={timestamp}&sports={sports}&countries={countries}&bookmakers={bookmakers}&offertypes={offertypes}&lang={lang}&oddsFormat={oddsFormat}&
			String url = Controler.url;
			
//			url = url.replaceAll("GetSportEventByDate", "GetSportEvents");
			
			String sDateStart = DateUtils.getUTCDate(-1);
			String sDateEnd = DateUtils.getUTCDate(3);
			url = url + "startDate="+sDateStart+"&endDate="+sDateEnd+"&";
			
			if (Timestamp != null) {
				url = url + "timestamp=" + Timestamp;				
			}
			
			String fileName = Controler.saveFilePath + File.separator + "BookOdds" + File.separator + 
				year + File.separator + month + File.separator + day + File.separator + date + ".zip";
			
			if (debug) System.err.println(fileName);
			String body = page.doGet(url);
			logger.info(Thread.currentThread().getName() + " get success");
//			if (debug) 
//			System.err.println(body);
			if (body != null && body.length() > 10) {
				int p = body.indexOf("<");
				body = body.substring(p);
				FileDispose.saveAsZip(body, fileName);
//				FileDispose.saveFile(body, fileName);
				fullData(fileName, body);								
			} else {
				logger.info("body = " + body);
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}		
	}
		
	public void fullData(String fileName, String strBody) {
		
		try {
			
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
			ExtractTime = ExtractTime.replaceAll("\\.zip", "").replaceAll("\\.xml", "");
			String date = ExtractTime.substring(0,4) + "-" + ExtractTime.substring(4,6) + "-" + ExtractTime.substring(6,8) 
					+ " " + ExtractTime.substring(9,11) + ":" + ExtractTime.substring(11,13) + ":" + ExtractTime.substring(13,15);
			ExtractTime = date;
			
			List<String> Events = Utils.extractMatchValues(strBody, "<Event>(.*?)</Event>");
			
			if (Events.size() > 0) {
				synchronized (BookOddsBulkByEvent.hmEvent) {
					BookOddsBulkByEvent.hmEvent.clear();
				}
			}
			
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
				
//				String LastUpdateTime = Utils.extractMatchValue(event, " LastUpdate=\"(.*?)\"");      //LastUpdateTime datetime,
//				if (debug) System.err.println("LastUpdateTime = " + LastUpdateTime);				
				
				String SportName = Utils.extractMatchValue(event, "<SportID Name=\"(.*?)\"");          		//SportName varchar(30),
				if (debug) System.err.println("SportName = " + SportName);
				
				String EventID = Utils.extractMatchValue(event, "<EventID>(.*?)<");          				//EventID bigint,
				if (debug) {
					System.err.println("EventID = " + EventID);
//					if (!EventID.equals("700091799")) continue;
				}
				
				String StartDate = Utils.extractMatchValue(event, "<StartDate>(.*?)\\.").replaceAll("T", " ");
				if (debug) System.err.println("StartDate = " + StartDate);
				
				//inprogress
				//Cancelled
				String Status = Utils.extractMatchValue(event, "<Status>(.*?)<");
				if (debug) System.err.println("Status = " + Status);
				if (Status.equals("NSY") || Status.equals("inprogress")) { 
				} else {
					logger.info("EventID - Status = " + EventID + " - " + Status);														
				}
				
				Date dNow = DateUtils.getUTCNow();
				synchronized (BookOddsBulkByEvent.hmEvent) {
					Date dStartDateUTC = DateUtils.fromLongStringUTC(StartDate);
					
					if ((dNow.getTime() - dStartDateUTC.getTime()) >= 1000 * 60 * 5) {
						continue;
					}
					
					if ((dStartDateUTC.getTime() - dNow.getTime()) >= 1000 * 60 * 70) {
						continue;
					}					
					
					BookOddsBulkByEvent.hmEvent.put(EventID, dStartDateUTC);
				}
				
			}
			
			ParseData.parse(strBody, fileName);
		
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
		
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");	
//		debug = true;		
//		new BookOdds().savePage();
		
//		String fileName = "F:\\ZA\\20171105_075113.xml";	
//		BookOddsBulk bll = new BookOddsBulk();
//		bll.fullData(fileName, null);
	}
}
